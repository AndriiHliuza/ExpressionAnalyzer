package org.pzks.settings.args.processor;

import org.pzks.settings.GlobalSettings;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ProgramKey {
    VERBOSE("--verbose", "Verbose output."),
    FIX("--fix", "Fix expression if there are syntax errors."),
    OPTIMIZATION("--optimize", List.of(BoolArg.values()), """
            Optimize expression.
            Optimization is done by default.
            To turn off optimization, set --optimize=false
            Available properties: %s.
            """.formatted(
            Stream.of(BoolArg.values())
                    .map(value -> value.toString().toLowerCase())
                    .collect(Collectors.joining(","))
    )
    ),
    PARALLEL_CALCULATION_TREE("--tree", "Build parallel calculation tree of the expression."),
    BiNARY_PARALLEL_CALCULATION_TREE("--binary-tree", """
            Build parallel calculation tree of the expression as binary tree.
            Note:
            - Can be used only in combination with --tree
            - This will make impossible creation of trees out of expressions that contains functions.
            """),
    PROPERTY("--property", List.of(PropertyArg.values()), """
            Set property to use.
            Available properties: %s.
            Usage: --property=<value>.
            """.formatted(
            Stream.of(PropertyArg.values())
                    .map(value -> value.toString().toLowerCase())
                    .collect(Collectors.joining(",")))
    ),
    NUMBER_OF_PROPERTY_BASED_EXPRESSIONS_GENERATED("--expr-gen-limit", """
            Set number of generated expressions on property processing stage.
            Usage: --expr-gen-limit=<number>.
            Note: Default is up to ~%s expressions if not specified.
            """.formatted(GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT)
    ),
    NO_LIMIT_FOR_NUMBER_OF_PROPERTY_BASED_EXPRESSIONS_GENERATED("--expr-gen-no-limit", """
            Removes default limit of %s expressions for number of generated expressions on property processing stage.
            Note: This may result in large CPU and RAM consumption!
            """.formatted(GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT)
    ),
    NO_WARNINGS("--no-warnings", "Turns off any warnings"),
    DATAFLOW("--dataflow", "Enables simulation of dataflow system"),
    HELP("--help", "Show manual");

    private final String value;
    private List<ProgramKeyArg> programKeyArgs;
    private int numberParam;
    private final String description;

    ProgramKey(String value, String description) {
        this.value = value;
        this.description = description;
    }

    ProgramKey(String value, List<ProgramKeyArg> programKeyArgs, String description) {
        this.value = value;
        this.programKeyArgs = programKeyArgs;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public List<ProgramKeyArg> getProgramKeyArgs() {
        return programKeyArgs;
    }
}
