package org.pzks.utils;

public enum ProgramKey {
    TREE("--print-exp-trees", "(Print) (tree) representations of the (exp)ression"),
    PARALLEL_CALCULATION_TREE("--build-pc-tree", "(Build) (p)arallel (c)alculation (tree) of the expression"),
    EXPRESSION_OPTIMIZATION_BEFORE_BUILDING_PARALLEL_CALCULATION_TREE(
            "-optimize", "Optimize expression before building parallel calculation tree.\n" +
            "Note: This flag is valid only if used in combination with " + PARALLEL_CALCULATION_TREE.getValueForManual()
    );

    private final String value;
    private final String description;

    ProgramKey(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getValueForManual() {
        return Color.BRIGHT_MAGENTA.getAnsiValue() + value + Color.DEFAULT.getAnsiValue();
    }

    public String getDescription() {
        return description;
    }
}
