package org.pzks.utils;

public class ArithmeticUtils {
    public static double calculateResult(String operation, double currentNumber, double nextNumber) {
        return switch (operation) {
            case "+" -> currentNumber + nextNumber;
            case "-" -> currentNumber - nextNumber;
            case "*" -> currentNumber * nextNumber;
            case "/" -> currentNumber / nextNumber;
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
    }

    public static String convertDoubleToString(double number) {
        return String.valueOf(number);
    }
}
