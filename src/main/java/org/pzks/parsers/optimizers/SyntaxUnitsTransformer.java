package org.pzks.parsers.optimizers;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.LogicalBlock;
import org.pzks.units.Operation;
import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class SyntaxUnitsTransformer {
    private List<SyntaxUnit> syntaxUnits;

    public SyntaxUnitsTransformer(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
    }

    public SyntaxUnitsTransformer openBracketsAfterPlusOrMinusOperations() throws Exception {
        String expressionBeforeOpeningTheBrackets;
        String expressionAfterOpeningTheBrackets;
        do {
            expressionBeforeOpeningTheBrackets = ExpressionParser.getExpressionAsString(syntaxUnits);
            openBracketsAfterPlusOrMinusOperations(syntaxUnits);
            expressionAfterOpeningTheBrackets = ExpressionParser.getExpressionAsString(syntaxUnits);
        } while (!expressionBeforeOpeningTheBrackets.equals(expressionAfterOpeningTheBrackets));

        return this;
    }

    public List<SyntaxUnit> getTransformedSyntaxUnits() throws Exception {
        return ExpressionParser
                .convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits))
                .getSyntaxUnits();
    }

    private void openBracketsAfterPlusOrMinusOperations(List<SyntaxUnit> syntaxUnits) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (currentSyntaxUnit instanceof Operation && currentSyntaxUnit.getValue().matches("[+\\-]") && i + 1 < syntaxUnits.size()) {
                SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);
                if (nextSyntaxUnit instanceof LogicalBlock) {
                    List<SyntaxUnit> nextSyntaxUnitSyntaxUnits = nextSyntaxUnit.getSyntaxUnits();
                    syntaxUnits.remove(i + 1);
                    switch (currentSyntaxUnit.getValue()) {
                        case "+" -> syntaxUnits.addAll(i + 1, nextSyntaxUnitSyntaxUnits);
                        case "-" -> {
                            List<SyntaxUnit> modifiedByMultiplicationsByMinusOperatorSyntaxUnits = new ArrayList<>();
                            for (SyntaxUnit syntaxUnit : nextSyntaxUnitSyntaxUnits) {
                                if (syntaxUnit instanceof Operation && syntaxUnit.getValue().matches("[+\\-]")) {
                                    switch (syntaxUnit.getValue()) {
                                        case "+" -> syntaxUnit.setValue("-");
                                        case "-" -> syntaxUnit.setValue("+");
                                    }
                                    modifiedByMultiplicationsByMinusOperatorSyntaxUnits.add(syntaxUnit);
                                } else {
                                    modifiedByMultiplicationsByMinusOperatorSyntaxUnits.add(syntaxUnit);
                                }
                            }
                            syntaxUnits.addAll(i + 1, modifiedByMultiplicationsByMinusOperatorSyntaxUnits);
                        }
                    }
                }
            } else if (currentSyntaxUnit instanceof SyntaxContainer) {
                if (currentSyntaxUnit instanceof LogicalBlock) {
                    if (i == 0) {
                        List<SyntaxUnit> currenSyntaxUnitSyntaxUnits = currentSyntaxUnit.getSyntaxUnits();
                        syntaxUnits.removeFirst();
                        syntaxUnits.addAll(i, currenSyntaxUnitSyntaxUnits);
                    } else {
                        openBracketsAfterPlusOrMinusOperations(currentSyntaxUnit.getSyntaxUnits());
                    }
                } else {
                    openBracketsAfterPlusOrMinusOperations(currentSyntaxUnit.getSyntaxUnits());
                }
            }
        }
    }
}
