package org.pzks;

import org.pzks.parsers.ExpressionParser;
import org.pzks.utils.ProgramArgsProcessor;

public class Main {

    public static void main(String[] args) throws Exception {
        ProgramArgsProcessor programArgsProcessor = new ProgramArgsProcessor(args);

        if (programArgsProcessor.isValidUsage()) {
            ExpressionParser.parse(
                    programArgsProcessor.getExpression(),
                    programArgsProcessor.shouldShowExpressionTrees(),
                    programArgsProcessor.shouldFixExpression(),
                    programArgsProcessor.shouldBuildParallelCalculationTree(),
                    programArgsProcessor.shouldOptimizeExpressionBeforeBuildingParallelCalculationTree()
            );
        }
    }
}