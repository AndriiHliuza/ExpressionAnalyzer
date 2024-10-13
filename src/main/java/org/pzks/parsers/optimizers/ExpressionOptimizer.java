package org.pzks.parsers.optimizers;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.SyntaxUnit;

import java.util.List;

public class ExpressionOptimizer {
    private List<SyntaxUnit> syntaxUnits;
    private List<SyntaxUnit> optimizedSyntaxUnits;

    public ExpressionOptimizer(List<SyntaxUnit> syntaxUnits) throws Exception {
        this.syntaxUnits = syntaxUnits;
        optimize();
    }

    private void optimize() throws Exception {

    }

    public SyntaxUnit getOptimizedSyntaxUnits() throws Exception {
        return ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(optimizedSyntaxUnits));
    }
}
