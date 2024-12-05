package org.pzks.parsers.optimizers;

import org.pzks.parsers.ExpressionProcessor;
import org.pzks.parsers.converters.ExpressionConverter;
import org.pzks.units.SyntaxUnit;

import java.util.List;

public class ExpressionOptimizer {
    private List<SyntaxUnit> syntaxUnits;

    public ExpressionOptimizer(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        optimize();
    }

    private void optimize() throws Exception {
        SyntaxUnitsTransformer syntaxUnitsTransformer = new SyntaxUnitsTransformer(syntaxUnits);
        syntaxUnits = syntaxUnitsTransformer
                .openBracketsAfterPlusOrMinusOperations()
                .getTransformedSyntaxUnits();
    }

    public SyntaxUnit getOptimizedSyntaxUnit() throws Exception {
        return ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(syntaxUnits));
    }
}
