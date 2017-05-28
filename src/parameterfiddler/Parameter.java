package parameterfiddler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parameter {

    private String codeLine;
    private static final Pattern valuePattern = Pattern.compile("(?<![\\w_\\-])\\d+(\\.?\\d*)?(e\\-?\\d+)?");

    public Parameter(String codeLine) {
        this.codeLine = codeLine;
    }

    private String extractValue() {
        Matcher m = valuePattern.matcher(codeLine);
        if (m.find()) {
            return m.group();
        }
        return null;
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
            if (oldValue == newValue) {
                if (factor > 1) {
                    newValue++;
                } else {
                    newValue--;
                }
            }
        }
        return new Parameter(codeLine.replace(valueString, isFloat() ? String.valueOf(newValue) : String.valueOf((int) newValue)));
    }

    @Override
    public String toString() {
        return codeLine;
    }
}
