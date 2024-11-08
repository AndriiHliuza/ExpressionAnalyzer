package org.pzks.utils.args.processor;

import org.pzks.utils.Color;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ProgramKey {
    VERBOSE("--verbose", "Verbose output."),
    FIX("--fix", "Fix expression if there are syntax errors."),
    PARALLEL_CALCULATION_TREE("--tree", "Build parallel calculation tree of the expression."),
    PROPERTY("--property", List.of(PropertyArg.values()), """
    Set property to use.
    Available properties: %s.
    Usage: --property=<value>.
    """.formatted(
            Stream.of(PropertyArg.values())
                    .map(value -> value.toString().toLowerCase())
                    .collect(Collectors.joining(",")))
    );

    private final String value;
    private List<ProgramKeyArg> programKeyArgs;
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
