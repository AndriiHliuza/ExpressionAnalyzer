package org.pzks;

import org.pzks.parsers.ExpressionParser;
import org.pzks.utils.args.processor.ProgramArgsProcessor;

public class Main {

    public static void main(String[] args) throws Exception {
        ProgramArgsProcessor programArgsProcessor = new ProgramArgsProcessor(args);

        if (programArgsProcessor.isValidUsage()) {
            ExpressionParser.parse(
                    programArgsProcessor.getExpression(),
                    programArgsProcessor.shouldShowVerboseOutput(),
                    programArgsProcessor.shouldFixExpressionIfErrorsPresent(),
                    programArgsProcessor.getPropertyArgs(),
                    programArgsProcessor.shouldBuildParallelCalculationTree()
            );
        }
    }
}