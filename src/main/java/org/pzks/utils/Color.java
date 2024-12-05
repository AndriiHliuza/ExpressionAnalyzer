package org.pzks.utils;

public enum Color {
    DEFAULT("\u001B[0m"),
    RED("\u001b[31;1m"),
    CYAN("\u001b[36;1m"),
    GREEN("\u001b[32m"),
    BRIGHT_MAGENTA("\u001b[35;1m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    LIGHT_GRAY("\u001B[37m"),
    BLACK("\u001B[30m"),
    DARK_BLUE("\u001b[38;2;23;31;51m"),
    YELLOW_BACKGROUND("\u001B[103m"),
    LIGHT_GRAY_BACKGROUND("\u001B[47m"),
    BRIGHT_BLUE_GRAY_BACKGROUND("\u001b[48;2;130;155;196m"),
    DARK_GREEN_BACKGROUND("\u001b[48;2;45;59;48m"),
    DARK_RED_BACKGROUND("\u001b[48;2;54;36;41m"),
    BRIGHT_RED_BACKGROUND("\u001b[48;2;224;195;191m"),
    RED_BACKGROUND("\u001B[41m"),
    GREEN_BACKGROUND("\u001B[42m"),
    BLUE_BACKGROUND("\u001B[44m");

    private final String ansiValue;

    Color(String ansiValue) {
        this.ansiValue = ansiValue;
    }

    public String getAnsiValue() {
        return ansiValue;
    }
}
