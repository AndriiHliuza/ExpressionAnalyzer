package org.pzks.parsers.optimizers;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.SyntaxUnit;

import java.util.List;

public class ExpressionParallelizationOptimizer {
    private List<SyntaxUnit> syntaxUnits;

    public ExpressionParallelizationOptimizer(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        optimize();
    }

    private void optimize() throws Exception {
        MultiplicationAndDivisionOperationsParallelizationOptimizer.replaceDivisionWithMultiplication(syntaxUnits);
        AdditionAndSubtractionOperationsParallelizationOptimizer.replaceSubtractionWithAddition(syntaxUnits);
    }

    public SyntaxUnit getOptimizedSyntaxUnit() throws Exception {
        return ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits));
    }
}
