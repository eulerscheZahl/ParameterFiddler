package parameterfiddler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

class Runner extends Observable implements Runnable {

    private Bot toImprove;
    private ArrayList<Bot> opponents;
    private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
    private String brutaltesterPath;
    private String refereeCommand;
    private int rounds;
    private int threads;
    private double delta;
    private ConcurrentLinkedQueue<String> brutaltesterQueue = new ConcurrentLinkedQueue<String>();
    private ConcurrentLinkedQueue<String> parameterFiddlerQueue = new ConcurrentLinkedQueue<String>();

    public Runner(Bot toImprove, ArrayList<Bot> opponents, ArrayList<Parameter> parameters, String brutaltesterPath, String refereeCommand, int rounds, int threads, double delta) {
        this.toImprove = toImprove;
        this.opponents = opponents;
        this.parameters = parameters;
        this.brutaltesterPath = brutaltesterPath;
        this.refereeCommand = refereeCommand;
        this.rounds = rounds;
        this.threads = threads;
        this.delta = delta;
    }

    private void writeParameters(ArrayList<Parameter> parameters, String filename) {
        ArrayList<String> lines = new ArrayList<String>();
        for (Parameter p : parameters) {
            lines.add(p.toString());
        }
        Path file = Paths.get(filename);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            parameterFiddlerQueue.add("failed to write file: " + ex);
            this.setChanged();
            this.notifyObservers();
        }
    }

    private double runBot(ArrayList<Parameter> parameters, boolean sameValue) throws IOException {
        writeParameters(parameters, "mutatedParameters.txt");
        ArrayList<String> output = new ArrayList<String>();

        //run battles
        double wins = 0;
        double losses = 0;
        for (Bot opponent : opponents) {
            //I will most likely win 50% against myself, no need wo waste CPU
            if (sameValue && toImprove.getParamFile().equals(opponent.getParamFile())) {
                wins += 0.5 * rounds;
                losses += 0.5 * rounds;
                continue;
            }

            ArrayList<String> args = new ArrayList<String>();
            args.add("java");
            args.add("-jar");
            args.add(brutaltesterPath);
            args.add("-r");
            args.add("\"" + refereeCommand + "\"");
            args.add("-p1");
            args.add(toImprove.getRunCommand("mutatedParameters.txt"));
            args.add("-p2");
            args.add(opponent.getRunCommand());
            args.add("-t" + threads);
            args.add("-n" + rounds);

            ProcessBuilder pb = new ProcessBuilder(args);
            parameterFiddlerQueue.add(new Date() + ": executing: " + pb.command());
            this.setChanged();
            this.notifyObservers();
            Process p = null;
            try {
                p = pb.start();
            } catch (IOException ex) {
                parameterFiddlerQueue.add("failed to start process: " + ex);
                this.setChanged();
                this.notifyObservers();
                return Double.NaN;
            }

            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                parameterFiddlerQueue.add("process stopped unexpectedly: " + ex);
                this.setChanged();
                this.notifyObservers();
                return Double.NaN;
            }
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
                brutaltesterQueue.add(s);
                this.setChanged();
                this.notifyObservers();
            }

            for (int i = 0; i < output.size(); i++) {
                if (output.get(i).contains("Position 1")) {
                    String tmp = output.get(i);
                    tmp = tmp.substring(tmp.indexOf("Position 1") + 13);
                    tmp = tmp.substring(0, tmp.indexOf(" "));
                    int currentWins = Integer.parseInt(tmp);
                    tmp = output.get(i + 1);
                    tmp = tmp.substring(tmp.indexOf("Position 2") + 13);
                    tmp = tmp.substring(0, tmp.indexOf(" "));
                    int currentLosses = Integer.parseInt(tmp);
                    parameterFiddlerQueue.add(new Date() + ": result " + currentWins + ":" + currentLosses);
                    this.setChanged();
                    this.notifyObservers();
                    wins += currentWins;
                    losses += currentLosses;
                    break;
                }
            }
            output.clear();
        }

        return wins / (wins + losses);
    }

    public ConcurrentLinkedQueue<String> getBrutaltesterQueue() {
        return brutaltesterQueue;
    }

    public ConcurrentLinkedQueue<String> getParameterFiddlerQueue() {
        return parameterFiddlerQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mutateBot();
            } catch (IOException ex) {
                parameterFiddlerQueue.add("failed: " + ex);
                this.setChanged();
                this.notifyObservers();
            }
        }
    }

    private void setParameter(List<Parameter> parameters, int index, Parameter toSet) {
        parameters.set(index, toSet);
        parameterFiddlerQueue.add(new Date() + ": set parameter: " + toSet);
        this.setChanged();
        this.notifyObservers();
    }

    private void mutateBot() throws IOException {
        for (int i = 0; i < parameters.size(); i++) {
            Parameter original = parameters.get(i);
            Parameter left = original.mutate(1 - delta);
            Parameter right = original.mutate(1 + delta);
            if (left != null && right != null) {
                //run battles to get winrates
                double vm = original.getValue();
                double wm = runBot(parameters, true);
                setParameter(parameters, i, left);
                double vl = left.getValue();
                double wl = runBot(parameters, false);
                setParameter(parameters, i, right);
                double vr = right.getValue();
                double wr = runBot(parameters, false);

                //solve equation system to get parameter value with maximum winrate (assuming parabola)
                double det = vl * vl * vm + vl * vr * vr + vm * vm * vr - vl * vl * vr - vm * vm * vl - vr * vr * vm;
                double det1 = wl * vm + vl * wr + wm * vr - wl * vr - wm * vl - wr * vm;
                double det2 = vl * vl * wm + wl * vr * vr + vm * vm * wr - vl * vl * wr - vm * vm * wl - vr * vr * wm;
                double det3 = vl * vl * vm * wr + vl * vr * vr * wm + vm * vm * vr * wl - vl * vl * vr * wm - vm * vm * vl * wr - vr * vr * vm * wl;

                double a = det1 / det;
                double b = det2 / det;
                double c = det3 / det;
                double x = -b / (2 * a);

                //result gets better in both directions? strange
                //TODO: parameter so far off, that tuning has no effect? -> warning message
                if (a > 0) {
                    if (wl > wr) {
                        x = vl;
                    } else {
                        x = vr;
                    }
                }

                //interpolation, no extrapolation
                if (x > vr) {
                    x = vr;
                }
                if (x < vl) {
                    x = vl;
                }
                if (!original.isFloat()) {
                    double xOld = x;
                    x = Math.round(x);
                    if (x == vm) {
                        if (xOld < vm) {
                            x--;
                        }
                        if (xOld > vm) {
                            x++;
                        }
                    }
                }

                Parameter bestParam = original;
                double bestWinrate = wm;
                if (wl > bestWinrate) {
                    bestWinrate = wl;
                    bestParam = left;
                }
                if (wr > bestWinrate) {
                    bestWinrate = wr;
                    bestParam = right;
                }
                if (x != vm && x != vr && x != vl) {
                    Parameter maxParam = original.mutate(x / vm);
                    setParameter(parameters, i, maxParam);
                    double maxWinrate = runBot(parameters, false);
                    if (maxWinrate > bestWinrate) {
                        bestWinrate = maxWinrate;
                        bestParam = maxParam;
                    }
                }
                setParameter(parameters, i, bestParam);
                writeParameters(parameters, toImprove.getParamFile());
            }
        }
    }
}
