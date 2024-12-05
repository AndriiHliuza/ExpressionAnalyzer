package org.pzks.settings.args.processor;

import org.pzks.utils.Color;
import org.pzks.utils.HeadlinePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramArgsProcessor {

    private final List<String> programKeys;
    private boolean isValidUsage;

    public ProgramArgsProcessor(String[] args) {
        this.programKeys = Arrays.stream(args).toList();

        StringBuilder programKeysPattern = new StringBuilder("^(");
        for (ProgramKey programKey : ProgramKey.values()) {
            programKeysPattern.append("(").append(programKey.getValue()).append(")|");
        }
        programKeysPattern.deleteCharAt(programKeysPattern.length() - 1);
        programKeysPattern.append(")$");

        if (programKeys.isEmpty() || getExpression().matches(programKeysPattern.toString()) || programKeys.contains(ProgramKey.HELP.getValue())) {
            HeadlinePrinter.print("Manual", Color.GREEN);
            System.out.println(Color.YELLOW.getAnsiValue() + "Available keys:\n" + Color.DEFAULT.getAnsiValue());
            for (int i = 0; i < ProgramKey.values().length; i++) {
                ProgramKey programKey = ProgramKey.values()[i];
                System.out.println((i + 1) + ") " + Color.BRIGHT_MAGENTA.getAnsiValue() + programKey.getValue() + Color.DEFAULT.getAnsiValue());
                System.out.println("Description: " + programKey.getDescription() + "\n");
            }
            System.out.println("Note: Expression should be the last argument provided!\n");
            System.out.println(Color.YELLOW.getAnsiValue() + "Program usage: " + Color.DEFAULT.getAnsiValue() + "java -jar expressionAnalyzer.jar <program arguments> <expression>\n");
            isValidUsage = false;
        } else {
            isValidUsage = true;
        }
    }

    public boolean shouldShowVerboseOutput() {
        return programKeys.contains(ProgramKey.VERBOSE.getValue());
    }

    public boolean shouldFixExpressionIfErrorsPresent() {
        return programKeys.contains(ProgramKey.FIX.getValue());
    }

    public BoolArg getOptimizationArg() {
        String optimizationKeyValuesPattern = "(" + ProgramKey.OPTIMIZATION.getProgramKeyArgs().stream()
                .filter(BoolArg.class::isInstance)
                .map(BoolArg.class::cast)
                .map(boolArgs -> boolArgs.name().toLowerCase())
                .collect(Collectors.joining("|")) + ")";
        BoolArg optimizationArg = BoolArg.TRUE;
        for (String programKey : programKeys) {
            if (programKey.matches(ProgramKey.OPTIMIZATION.getValue() + "=" + optimizationKeyValuesPattern)) {
                optimizationArg = Arrays.stream(programKey.split("[=,]"))
                        .filter(programKeyComponent -> !programKeyComponent.equals(ProgramKey.OPTIMIZATION.getValue()))
                        .map(String::toUpperCase)
                        .map(BoolArg::valueOf)
                        .findFirst()
                        .orElse(BoolArg.TRUE);
            }
        }

        return optimizationArg;
    }

    public boolean shouldBuildParallelCalculationTree() {
        return programKeys.contains(ProgramKey.PARALLEL_CALCULATION_TREE.getValue());
    }

    public boolean shouldBuildBinaryParallelCalculationTree() {
        return programKeys.contains(ProgramKey.BiNARY_PARALLEL_CALCULATION_TREE.getValue());
    }

    public List<PropertyArg> getPropertyArgs() {
        String propertyKeyValuesPattern = ProgramKey.PROPERTY.getProgramKeyArgs().stream()
                .filter(PropertyArg.class::isInstance)
                .map(PropertyArg.class::cast)
                .map(propertyArgs -> propertyArgs.name().toLowerCase())
                .collect(Collectors.joining("|"));
        List<PropertyArg> propertyArgs = new ArrayList<>();
        for (String programKey : programKeys) {
            if (programKey.matches(ProgramKey.PROPERTY.getValue() + "=" + ".+")) {
                propertyArgs = Arrays.stream(programKey.split("[=,]"))
                        .filter(programKeyComponent -> !programKeyComponent.equals(ProgramKey.PROPERTY.getValue()))
                        .filter(propertyArg -> propertyArg.matches(propertyKeyValuesPattern))
                        .map(String::toUpperCase)
                        .map(PropertyArg::valueOf)
                        .toList();
            }
        }

        return propertyArgs;
    }

    public boolean shouldShowWarnings() {
        return !programKeys.contains(ProgramKey.NO_WARNINGS.getValue());
    }

    public long getLimitNumberOfGeneratedExpressionsBasedOnProperty() {
        for (String programKey : programKeys) {
            if (programKey.matches("^" + ProgramKey.NUMBER_OF_PROPERTY_BASED_EXPRESSIONS_GENERATED.getValue() + "=" + "\\d+$")) {
                return Arrays.stream(programKey.split("="))
                        .filter(programKeyComponent -> !programKeyComponent.equals(ProgramKey.NUMBER_OF_PROPERTY_BASED_EXPRESSIONS_GENERATED.getValue()))
                        .map(Long::parseLong)
                        .findFirst()
                        .orElse(-1L);
            }
        }
        return -1L;
    }

    public boolean shouldRemoveLimitOfGeneratedExpressionsBasedOnProperty() {
        return programKeys.contains(ProgramKey.NO_LIMIT_FOR_NUMBER_OF_PROPERTY_BASED_EXPRESSIONS_GENERATED.getValue());
    }

    public boolean shouldEnableDataflowSystemSimulation() {
        return programKeys.contains(ProgramKey.DATAFLOW.getValue());
    }

    public boolean shouldShowOnlyDataflowSystemStatistics() {
        return programKeys.contains(ProgramKey.DATAFLOW.getValue());
    }

    public String getExpression() {
        return programKeys.getLast();
    }

    public boolean isValidUsage() {
        return isValidUsage;
    }
}
