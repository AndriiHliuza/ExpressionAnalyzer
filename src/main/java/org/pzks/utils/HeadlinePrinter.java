package org.pzks.utils;

public class HeadlinePrinter {
    public static void print(String headline, Color color) {
        System.out.println("\n".repeat(2) +
                color.getAnsiValue() +
                "-".repeat(10) +
                headline +
                "-".repeat(10) +
                Color.DEFAULT.getAnsiValue() +
                "\n".repeat(1));
    }
}
