package org.pzks.utils;

public class HeadlinePrinter {
    public static void print(String headline, Color color) {
        System.out.println("\n" +
                color.getAnsiValue() +
                "-".repeat(10) +
                headline +
                "-".repeat(10) +
                Color.DEFAULT.getAnsiValue() +
                "\n");
    }
}
