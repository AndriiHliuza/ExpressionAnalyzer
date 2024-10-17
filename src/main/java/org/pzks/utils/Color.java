package org.pzks.utils;

public enum Color {
    DEFAULT("\u001B[0m"),
    RED("\u001b[31;1m"),
    CYAN("\u001b[36;1m"),
    GREEN("\u001b[32m"),
    BRIGHT_MAGENTA("\u001b[35;1m"),
    YELLOW("\u001B[33m");

    private final String ansiValue;

    Color(String ansiValue) {
        this.ansiValue = ansiValue;
    }

    public String getAnsiValue() {
        return ansiValue;
    }
}
