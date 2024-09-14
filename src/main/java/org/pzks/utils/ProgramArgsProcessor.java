package org.pzks.utils;

import java.util.Arrays;
import java.util.List;

public class ProgramArgsProcessor {

    private final List<String> programKeys;

    public ProgramArgsProcessor(String[] args) {
        this.programKeys = Arrays.stream(args).toList();
    }

    public boolean shouldBuildTree() {
        return programKeys.contains("--tree");
    }

    public boolean shouldCheckForErrors() {
        return programKeys.contains("--check");
    }

    public String getExpression() {
        return programKeys.getLast();
    }
}
