package org.pzks.utils;

public enum ProgramKey {
    TREE("--print-exp-trees", "(Print) (tree) representations of the (exp)ression"),
    FIX("--fix", "Fix expression if there are syntax errors"),
    PARALLEL_CALCULATION_TREE("--build-pc-tree", "(Build) (p)arallel (c)alculation (tree) of the expression"),
    EXPRESSION_OPTIMIZATION_BEFORE_BUILDING_PARALLEL_CALCULATION_TREE(
            "--optimize", """
            Optimize expression before building parallel calculation tree.
            Note: This flag is helpful only if used in combination with " %s
            """.formatted(PARALLEL_CALCULATION_TREE.getValueForManual())
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
