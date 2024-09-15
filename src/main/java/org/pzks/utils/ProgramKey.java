package org.pzks.utils;

public enum ProgramKey {
    TREE("--tree", "Print tree representation of the expression"),
    CHECK("--check", "Check if provided expression is valid"),
    FIX("--fix", "Provide possible fixes if expression is invalid");

    private final String value;
    private final String description;

    ProgramKey(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
