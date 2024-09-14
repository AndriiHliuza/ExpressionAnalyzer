package org.pzks.parsers;

import org.pzks.utils.BasicExpressionUnitRecognizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharSequenceParser {
    private final CharSequenceNode charSequenceNode;
    private final BasicExpressionUnitRecognizer basicExpressionUnitRecognizer = new BasicExpressionUnitRecognizer();
    private final Pattern specialCharactersPattern = Pattern.compile("[^\\w\\s]|\\s+");
    private final Pattern alphaNumericNamingPattern = Pattern.compile("^\\d+|[\\p{Alpha}_\\d]+");

    public CharSequenceParser(CharSequenceNode charSequenceNode) {
        this.charSequenceNode = charSequenceNode;
    }

    public CharSequenceParser processAlphaNumericCharacters() {
        if (charSequenceNode.isProcessed()) {
            return this;
        }
        String value = charSequenceNode.getNonProcessedValue();
        if (basicExpressionUnitRecognizer.isValidAlphaNumericNaming(value) || basicExpressionUnitRecognizer.isIntegerNumber(value)) {
            charSequenceNode.getProcessedValue().add(value);
            charSequenceNode.setProcessed(true);
        } else if (basicExpressionUnitRecognizer.isInvalidAlphaNumericNaming(value)) {
            List<String> alphaNumerics = charSequenceNode.getProcessedValue();
            Matcher matcher = alphaNumericNamingPattern.matcher(value);
            while (matcher.find()) {
                alphaNumerics.add(matcher.group());
            }

            String joinedProcessedValue = String.join("", alphaNumerics);
            if (value.equals(joinedProcessedValue)) {
                charSequenceNode.setProcessed(true);
            }
        }
        return this;
    }

    public CharSequenceParser processSpecialCharacters() {
        if (charSequenceNode.isProcessed()) {
            return this;
        }
        String value = charSequenceNode.getNonProcessedValue();
        if (basicExpressionUnitRecognizer.isSpecialCharacters(value)) {
            List<String> specialCharacters = charSequenceNode.getProcessedValue();

            Matcher matcher = specialCharactersPattern.matcher(value);
            while (matcher.find()) {
                specialCharacters.add(matcher.group());
            }

            String joinedProcessedValue = String.join("", specialCharacters);
            if (value.equals(joinedProcessedValue)) {
                charSequenceNode.setProcessed(true);
            }
        }

        return this;
    }

    public List<String> getProcessedValue() {
        return charSequenceNode.getProcessedValue();
    }

}
