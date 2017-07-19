package parameterfiddler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parameter {

    private String codeLine;
    private static final Pattern valuePattern = Pattern.compile("(?<![\\w_\\-\\d])-?\\d+(\\.?\\d*)?(e\\-?\\d+)?");
    private String initialValue;
    private String bestValue;
    private boolean testActive = false;

    public Parameter(String codeLine) {
        this.codeLine = codeLine;
        this.initialValue = extractValue();
        this.bestValue = this.initialValue;
    }

    private String extractValue() {
        Matcher m = valuePattern.matcher(codeLine);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    public boolean hasValue() {
        return extractValue() != null;
    }

    public double getValue() {
        return Double.parseDouble(extractValue());
    }

    public boolean isFloat() {
        return codeLine.contains("float") || codeLine.contains("double") || codeLine.contains(".");
    }

    public Parameter mutate(double factor) {
        String valueString = this.extractValue();
        if (valueString == null) {
            return null;
        }
        double oldValue = 0;
        try {
            oldValue = getValue();
        } catch (Exception ex) {
            return null;
        }
        double newValue = oldValue * factor;
        if (!this.isFloat()) {
            newValue = Math.round(newValue);
        }
        if (oldValue == newValue) {
            if (factor > 1) {
                newValue++;
            } else {
                newValue--;
            }
        }

        Parameter result = new Parameter(codeLine.replaceFirst(valuePattern.toString(), isFloat() ? String.valueOf(newValue) : String.valueOf((int) newValue)));
        result.initialValue = this.initialValue;
        result.bestValue = this.bestValue;
        return result;
    }

    @Override
    public String toString() {
        return codeLine;
    }

    String getName() {
        String result = codeLine;
        String match = extractValue();
        if (match != null) {
            result = result.substring(0, result.indexOf(match));
        }
        return result;
    }

    String getInitalValue() {
        return initialValue;
    }

    String getBestValue() {
        return bestValue;
    }

    public void setBestValue(String bestValue) {
        this.bestValue = bestValue;
    }

    public void setTestActive(boolean testActive) {
        this.testActive = testActive;
    }

    String getTestingValue() {
        if (!testActive) {
            return "";
        }
        return extractValue();
    }
}
