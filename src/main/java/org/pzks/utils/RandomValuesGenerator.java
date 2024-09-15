package org.pzks.utils;

import java.util.Random;

public abstract class RandomValuesGenerator {

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String POPULAR_LOWERCASE_LETTERS = "abcdfghkmnpqrstuvxyz";
    private static final String DIGITS = "0123456789";
    private static final String POSITIVE_DIGITS = "123456789";
    private static final String LETTERS_DIGITS = UPPERCASE_LETTERS + LOWERCASE_LETTERS + DIGITS + "_";
    private static final char[] symbols = {'+', '-', '/', '*'};

    public static String generateOperation() {
        Random random = new Random();
        int randomIndex = random.nextInt(symbols.length);
        return String.valueOf(symbols[randomIndex]);
    }

    public static String generateVariableName() {
        Random random = new Random();
        return String.valueOf(POPULAR_LOWERCASE_LETTERS.charAt(random.nextInt(POPULAR_LOWERCASE_LETTERS.length()))) +
                POSITIVE_DIGITS.charAt(random.nextInt(POSITIVE_DIGITS.length()));
    }

    public static String generateComplexVariableName() {
        Random random = new Random();
        StringBuilder variableName = new StringBuilder();
        int variableLength = random.nextInt(0, 5);

        variableName.append(LOWERCASE_LETTERS.charAt(random.nextInt(LOWERCASE_LETTERS.length())));

        for (int i = 1; i < variableLength; i++) {
            variableName.append(LETTERS_DIGITS.charAt(random.nextInt(LETTERS_DIGITS.length())));
        }

        return variableName.toString();
    }

}
