package org.pzks.parsers.math.laws.units;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.SyntaxUnit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class SyntaxUnitExpression {
    private SyntaxUnit syntaxUnit;
    private final List<SyntaxUnitExpression> syntaxUnitExpressions = new ArrayList<>();
    private final StringBuilder spaceBuilder = new StringBuilder();
    private final StringBuilder fileSpaceBuilder = new StringBuilder();

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

    public void printTreeOfDependentSyntaxUnitExpressions() {
        spaceBuilder.append(" ".repeat(5));
        printTreeOfDependentSyntaxUnitExpressions(syntaxUnitExpressions, "");
    }

    private void printTreeOfDependentSyntaxUnitExpressions(List<SyntaxUnitExpression> syntaxUnitExpressions, String levelPrefix) {
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

    public void saveTreeOfDependentSyntaxUnitExpressionsToFile(String filePath) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            saveTreeOfDependentSyntaxUnitExpressions(syntaxUnitExpressions, "", writer);
        }
    }

    private void saveTreeOfDependentSyntaxUnitExpressions(List<SyntaxUnitExpression> syntaxUnitExpressions, String levelPrefix, BufferedWriter writer) throws Exception {
        for (int i = 0; i < syntaxUnitExpressions.size(); i++) {
            SyntaxUnitExpression syntaxUnitExpression = syntaxUnitExpressions.get(i);
            writer.write(fileSpaceBuilder + levelPrefix + (i + 1) + ") " + ExpressionParser.getExpressionAsString(syntaxUnitExpression.syntaxUnit.getSyntaxUnits()) + "\n\n");

            if (!syntaxUnitExpression.getSyntaxUnitExpressions().isEmpty()) {
                List<SyntaxUnitExpression> internalSyntaxUnitExpressions = syntaxUnitExpression.getSyntaxUnitExpressions();
                fileSpaceBuilder.append(" ".repeat(5));
                saveTreeOfDependentSyntaxUnitExpressions(internalSyntaxUnitExpressions, levelPrefix + (i + 1) + ".", writer);
                fileSpaceBuilder.setLength(fileSpaceBuilder.length() - 5);
            }
        }
    }
}
