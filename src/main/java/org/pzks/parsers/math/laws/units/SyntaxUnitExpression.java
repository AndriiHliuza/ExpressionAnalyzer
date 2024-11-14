package org.pzks.parsers.math.laws.units;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class SyntaxUnitExpression {
    private SyntaxUnit syntaxUnit;
    private List<SyntaxUnitExpression> syntaxUnitExpressions = new ArrayList<>();
    private final StringBuilder spaceBuilder = new StringBuilder();

    public SyntaxUnitExpression(SyntaxUnit syntaxUnit) {
        this.syntaxUnit = syntaxUnit;
    }

    public SyntaxUnit getSyntaxUnit() {
        return syntaxUnit;
    }

    public void setSyntaxUnit(SyntaxUnit syntaxUnit) {
        this.syntaxUnit = syntaxUnit;
    }

    public List<SyntaxUnitExpression> getSyntaxUnitExpressions() {
        return syntaxUnitExpressions;
    }

    public void setSyntaxUnitExpressions(List<SyntaxUnitExpression> syntaxUnitExpressions) {
        this.syntaxUnitExpressions = syntaxUnitExpressions;
    }

    public void printTreeOfDependentSyntaxUnitExpressions() throws Exception {
        spaceBuilder.append(" ".repeat(5));
        printTreeOfDependentSyntaxUnitExpressions(getSyntaxUnitExpressions(), "");
    }

    private void printTreeOfDependentSyntaxUnitExpressions(List<SyntaxUnitExpression> syntaxUnitExpressions, String levelPrefix) throws Exception {
        for (int i = 0; i < syntaxUnitExpressions.size(); i++) {
            SyntaxUnitExpression syntaxUnitExpression = syntaxUnitExpressions.get(i);
            System.out.println(spaceBuilder + levelPrefix + (i + 1) + ") " + ExpressionParser.getExpressionAsString(syntaxUnitExpression.syntaxUnit.getSyntaxUnits()) + "\n");
            if (!syntaxUnitExpression.getSyntaxUnitExpressions().isEmpty()) {
                List<SyntaxUnitExpression> internalSyntaxUnitExpressions = syntaxUnitExpression.getSyntaxUnitExpressions();
                spaceBuilder.append(" ".repeat(5));
                printTreeOfDependentSyntaxUnitExpressions(internalSyntaxUnitExpressions, levelPrefix + (i + 1) + ".");
                spaceBuilder.setLength(spaceBuilder.length() - 5);
            }
        }
    }
}
