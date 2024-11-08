package org.pzks.utils;

public class HeadlinePrinter {
    public static void print(String headline, Color textColor) {
        int totalHeadlineLength = 50;
        int lengthOfFirstPartOfTotalHeadline = 10 + headline.length();
        int numberOfDashesAfterHeadline = totalHeadlineLength - lengthOfFirstPartOfTotalHeadline;
        if (numberOfDashesAfterHeadline < 0) {
            numberOfDashesAfterHeadline = 10;
        }
        System.out.println("\n" +
                Font.BOLD.getAnsiValue() +
                textColor.getAnsiValue() +
                "-".repeat(10) +
                headline +
                "-".repeat(numberOfDashesAfterHeadline) +
                Color.DEFAULT.getAnsiValue() +
                Font.DEFAULT.getAnsiValue() +
                "\n");
    }
}
