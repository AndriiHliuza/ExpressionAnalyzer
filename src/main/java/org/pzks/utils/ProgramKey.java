package org.pzks.utils;

public enum ProgramKey {
    TREE("--trees", "Print tree representations of the expression"),
    PARALLEL_CALCULATION_TREE("--p-tree", "Print parallel calculation tree of the expression");

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
