package org.pzks.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicExpressionUnitRecognizer {

    private final Pattern integerNumberMatcher = Pattern.compile("\\d+");
    private final Pattern floatNumberMatcher = Pattern.compile("\\d+(\\.\\d+)?");
    private final Pattern validAlphaNumericNaming = Pattern.compile("\\p{Alpha}+\\w*");
    private final Pattern inValidAlphaNumericNaming = Pattern.compile("\\d+\\w*");
    private final Pattern specialCharactersPatters = Pattern.compile("\\W+");
    private final Pattern operationPattern = Pattern.compile("[+\\-*/]");
    private final Pattern openingParenthesisPattern = Pattern.compile("\\(");
    private final Pattern closingParenthesisPattern = Pattern.compile("\\(");

    public boolean isIntegerNumber(String value) {
        Matcher matcher = integerNumberMatcher.matcher(value);
        return matcher.matches();
    }

    public boolean isFloatNumber(String value) {
        Matcher matcher = floatNumberMatcher.matcher(value);
        return matcher.matches();
    }

    public boolean isValidAlphaNumericNaming(String value) {
        Matcher matcher = validAlphaNumericNaming.matcher(value);
        return matcher.matches();
    }

    public boolean isInvalidAlphaNumericNaming(String value) {
        Matcher matcher = inValidAlphaNumericNaming.matcher(value);
        return matcher.matches();
    }

    public boolean isSpecialCharacters(String value) {
        Matcher matcher = specialCharactersPatters.matcher(value);
        return matcher.matches();
    }

    public boolean isOperation(String value) {
        Matcher matcher = operationPattern.matcher(value);
        return matcher.matches();
    }

    public boolean isOpeningParenthesis(String value) {
        Matcher matcher = openingParenthesisPattern.matcher(value);
        return matcher.matches();
    }

    public boolean isClosingParenthesis(String value) {
        Matcher matcher = closingParenthesisPattern.matcher(value);
        return matcher.matches();
    }
}
