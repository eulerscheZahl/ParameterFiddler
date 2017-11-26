package parameterfiddler;

class Bot {

    private String runCommand;
    private String paramFile;

    public Bot(String runCommand, String paramFile) {
        this.runCommand = runCommand;
        this.paramFile = paramFile;
    }

    public String getRunCommand() {
        return getRunCommand(paramFile);
    }

    public String getRunCommand(String file) {
        return "\"" + runCommand + " " + file + "\"";
    }

    public String getParamFile() {
        return paramFile;
    }
}
