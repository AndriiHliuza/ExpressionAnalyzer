package org.pzks.utils;

import java.time.Duration;

public class Statistics {
    public static Time calculateTotalExecutionTime(long startTimeNanos, long endTimeNanos) {
        Duration duration = Duration.ofNanos(endTimeNanos - startTimeNanos);
        return new Time(duration);
    }

    public static void displayTime (long startTime, long endTime) {
        int timeStringLength = Statistics.calculateTotalExecutionTime(startTime, endTime).toString().length();
        System.out.println(Font.BOLD.getAnsiValue() + Color.BRIGHT_MAGENTA.getAnsiValue() + "-".repeat(26 + timeStringLength) + Color.DEFAULT.getAnsiValue());
        System.out.println(Font.BOLD.getAnsiValue() + Color.BRIGHT_MAGENTA.getAnsiValue() + "| TOTAL EXECUTION TIME: " + Color.DEFAULT.getAnsiValue() + Font.BOLD.getAnsiValue() + Statistics.calculateTotalExecutionTime(startTime, endTime) + Color.BRIGHT_MAGENTA.getAnsiValue() + " |");
        System.out.println(Font.BOLD.getAnsiValue() + Color.BRIGHT_MAGENTA.getAnsiValue() + "-".repeat(26 + timeStringLength) + Color.DEFAULT.getAnsiValue());
    }
}
