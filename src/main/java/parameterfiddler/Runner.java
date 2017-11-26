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
import javafx.util.Pair;

class Runner extends Observable implements Runnable {

    private final Bot toImprove;
    private final ArrayList<Bot> opponents;
    private final ArrayList<Parameter> parameters;
    private final String brutaltesterPath;
    private final String refereeCommand;
    private final int rounds;
    private final int threads;
    private final double delta;
    private final boolean verbose;
    private final boolean swap;
    private final int players;
    private final ConcurrentLinkedQueue<String> brutaltesterQueue = new ConcurrentLinkedQueue<String>();
    private final ConcurrentLinkedQueue<String> parameterFiddlerQueue = new ConcurrentLinkedQueue<String>();

    public Runner(Bot toImprove, ArrayList<Bot> opponents, ArrayList<Parameter> parameters, String brutaltesterPath, String refereeCommand, int rounds, int threads, double delta, boolean verbose, boolean swap, int players) {
        this.toImprove = toImprove;
        this.opponents = opponents;
        this.parameters = parameters;
        this.brutaltesterPath = brutaltesterPath;
        this.refereeCommand = refereeCommand;
        this.rounds = rounds;
        this.threads = threads;
        this.delta = delta;
        this.verbose = verbose;
        this.swap = swap;
        this.players = players;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
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

    ArrayList<Pair<String, Double>> cache = new ArrayList<Pair<String, Double>>();
    int cacheSize = 5;

    private double runBot(ArrayList<Parameter> parameters) throws IOException {
        while (cache.size() > cacheSize) {
            cache.remove(0);
        }
        String key = "";
        for (Parameter p : parameters) {
            key += p.getTestingValue() + ":" + p.getBestValue() + "|";
        }
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getKey().equals(key)) {
                //just to fill the cache and remove older entries
                cache.add(new Pair<String, Double>("", 0.0));
                return cache.get(i).getValue();
            }
        }

        writeParameters(parameters, "mutatedParameters.txt");

        //run battles
        double wins = 0;
        double losses = 0;
        for (Bot opponent : opponents) {
            ArrayList<String> args = new ArrayList<String>();
            args.add("java");
            args.add("-jar");
            args.add(brutaltesterPath);
            args.add("-r");
            args.add("\"" + refereeCommand + "\"");
            args.add("-p1");
            args.add(toImprove.getRunCommand("mutatedParameters.txt"));
            for (int p = 2; p <= players; p++) {
                args.add("-p" + p);
                args.add(opponent.getRunCommand());
            }
            args.add("-t" + threads);
            args.add("-n" + rounds);
            if (verbose) {
                args.add("-v");
            }
            if (swap) {
                args.add("-s");
            }

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
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String s;
                while ((s = stdInput.readLine()) != null) {
                    brutaltesterQueue.add(s);
                    if (s.contains("End of game ")) {
                        String ranking = s.substring(s.indexOf("End of game"));
                        ranking = ranking.substring(ranking.indexOf(": ") + 2);
                        String[] parts = ranking.replace("\t", " ").split(" ");
                        int botIndex = 0;
                        while (!parts[botIndex].contains("0")) {
                            botIndex++;
                        }
                        int totalLength = 0;
                        for (int i = 0; i < parts.length; i++) {
                            if (i < botIndex) {
                                losses += parts[i].length();
                            }
                            if (i > botIndex) {
                                wins += parts[i].length();
                            }
                            if (i == botIndex) {
                                wins += 0.5 * (parts[i].length() - 1);
                                losses += 0.5 * (parts[i].length() - 1);
                            }
                            totalLength += parts[i].length();
                            if (totalLength >= players) { //don't parse win percentages
                                break;
                            }
                        }
                    }
                    this.setChanged();
                    this.notifyObservers();
                }
                p.waitFor();
            } catch (InterruptedException ex) {
                parameterFiddlerQueue.add("process stopped unexpectedly: " + ex);
                this.setChanged();
                this.notifyObservers();
                return Double.NaN;
            }
            parameterFiddlerQueue.add(new Date() + ": result " + wins + ":" + losses);
            this.setChanged();
            this.notifyObservers();
        }

        cache.add(new Pair<String, Double>(key, wins / (wins + losses)));
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
            if (!original.hasValue()) {
                continue;
            }
            original.setTestActive(true);
            setParameter(parameters, i, original);
            Parameter left = original.mutate(1 - delta);
            left.setTestActive(true);
            Parameter right = original.mutate(1 + delta);
            right.setTestActive(true);

            //run battles to get winrates
            double vm = original.getValue();
            double wm = runBot(parameters);
            setParameter(parameters, i, left);
            double vl = left.getValue();
            double wl = runBot(parameters);
            setParameter(parameters, i, right);
            double vr = right.getValue();
            double wr = runBot(parameters);

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
                maxParam.setTestActive(true);
                setParameter(parameters, i, maxParam);
                double maxWinrate = runBot(parameters);
                if (maxWinrate > bestWinrate) {
                    bestWinrate = maxWinrate;
                    bestParam = maxParam;
                }
            }
            setParameter(parameters, i, bestParam);
            bestParam.setBestValue(String.valueOf(bestParam.getValue()));
            bestParam.setTestActive(false);
            writeParameters(parameters, toImprove.getParamFile());
        }
    }
}
