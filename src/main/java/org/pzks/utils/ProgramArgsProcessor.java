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

        if (getExpression().matches(programKeysPattern.toString())) {
            HeadlinePrinter.print("Program Usage", Color.CYAN);
            System.out.println("Available keys:");
            for (int i = 0; i < ProgramKey.values().length; i++) {
                ProgramKey programKey = ProgramKey.values()[i];
                System.out.println((i + 1) + ") " + programKey.getValue());
                System.out.println("Description: " + programKey.getDescription() + "\n");
            }
            System.out.println("Please provide keys first. Expression should be the last argument provided");
            isValidUsage = false;
        } else {
            isValidUsage = true;
        }
    }

    public boolean shouldBuildTree() {
        return programKeys.contains(ProgramKey.TREE.getValue());
    }

    public boolean shouldCheckForErrors() {
        return programKeys.contains(ProgramKey.CHECK.getValue());
    }

    public boolean shouldMakeFixes() {
        return programKeys.contains(ProgramKey.FIX.getValue());
    }

    public String getExpression() {
        return programKeys.getLast();
    }

    public boolean isValidUsage() {
        return isValidUsage;
    }
}
