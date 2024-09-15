package org.pzks;

import org.pzks.parsers.ExpressionParser;
import org.pzks.utils.ProgramArgsProcessor;

public class Main {

    public static void main(String[] args) throws Exception {
        ProgramArgsProcessor programArgsProcessor = new ProgramArgsProcessor(args);

        if (programArgsProcessor.isValidUsage()) {
            new ExpressionParser().parse(
                    programArgsProcessor.getExpression(),
                    programArgsProcessor.shouldBuildTree(),
                    programArgsProcessor.shouldCheckForErrors(),
                    programArgsProcessor.shouldMakeFixes()
            );
        }
    }
}