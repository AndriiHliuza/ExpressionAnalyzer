package org.pzks.utils;

import java.text.DecimalFormat;

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
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedNumber = decimalFormat.format(number);
        return formattedNumber.replace(",", ".");
//        return String.valueOf(number);
    }
}
