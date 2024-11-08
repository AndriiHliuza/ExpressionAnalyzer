package org.pzks.utils;

public enum Font {
    DEFAULT("\u001b[0m"),
    BOLD("\u001b[1m");

    private final String ansiValue;

    Font(String ansiValue) {
        this.ansiValue = ansiValue;
    }

    public String getAnsiValue() {
        return ansiValue;
    }
}
