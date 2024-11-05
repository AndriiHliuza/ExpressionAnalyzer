package org.pzks.utils;

import java.util.Arrays;
import java.util.List;

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

        if (programKeys.isEmpty() || getExpression().matches(programKeysPattern.toString())) {
            HeadlinePrinter.print("Program Usage", Color.CYAN);
            System.out.println("Available keys:\n");
            for (int i = 0; i < ProgramKey.values().length; i++) {
                ProgramKey programKey = ProgramKey.values()[i];
                System.out.println((i + 1) + ") " + programKey.getValueForManual());
                System.out.println("Description: " + programKey.getDescription() + "\n");
            }
            System.out.println("Note: Expression should be the last argument provided!\n");
            isValidUsage = false;
        } else {
            isValidUsage = true;
        }
    }

    public boolean shouldShowExpressionTrees() {
        return programKeys.contains(ProgramKey.TREE.getValue());
    }

    public boolean shouldBuildParallelCalculationTree() {
        return programKeys.contains(ProgramKey.PARALLEL_CALCULATION_TREE.getValue());
    }

    public boolean shouldOptimizeExpressionBeforeBuildingParallelCalculationTree() {
        return programKeys.contains(ProgramKey.EXPRESSION_OPTIMIZATION_BEFORE_BUILDING_PARALLEL_CALCULATION_TREE.getValue());
    }

    public String getExpression() {
        return programKeys.getLast();
    }

    public boolean isValidUsage() {
        return isValidUsage;
    }
}
