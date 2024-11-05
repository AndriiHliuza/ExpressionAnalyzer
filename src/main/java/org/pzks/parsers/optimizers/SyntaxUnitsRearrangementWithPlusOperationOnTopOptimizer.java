package org.pzks.parsers.optimizers;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.Operation;
import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class SyntaxUnitsRearrangementWithPlusOperationOnTopOptimizer {
    private List<SyntaxUnit> syntaxUnits;

    public SyntaxUnitsRearrangementWithPlusOperationOnTopOptimizer(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
        rearrangeSyntaxUnits(syntaxUnits);
    }

    private void rearrangeSyntaxUnits(List<SyntaxUnit> syntaxUnits) {
        List<SyntaxUnit> positiveOperationsWithSyntaxUnits = new ArrayList<>();
        List<SyntaxUnit> negativeOperationsWithSyntaxUnits = new ArrayList<>();
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (currentSyntaxUnit instanceof Operation) {
                switch (currentSyntaxUnit.getValue()) {
                    case "+" -> {
                        if (i + 1 < syntaxUnits.size()) {
                            positiveOperationsWithSyntaxUnits.add(currentSyntaxUnit);
                            int currentPosition = i + 1;
                            while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                                positiveOperationsWithSyntaxUnits.add(syntaxUnits.get(currentPosition));
                                currentPosition++;
                            }
                            i = currentPosition - 1;
                        }
                    }
                    case "-" -> {
                        if (i + 1 < syntaxUnits.size()) {
                            negativeOperationsWithSyntaxUnits.add(currentSyntaxUnit);
                            int currentPosition = i + 1;
                            while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                                negativeOperationsWithSyntaxUnits.add(syntaxUnits.get(currentPosition));
                                currentPosition++;
                            }
                            i = currentPosition - 1;
                        }
                    }
                }
            } else if (i == 0) {
                if (currentSyntaxUnit instanceof SyntaxContainer) {
                    rearrangeSyntaxUnits(currentSyntaxUnit.getSyntaxUnits());
                }
                positiveOperationsWithSyntaxUnits.add(currentSyntaxUnit);
                if (i + 1 < syntaxUnits.size()) {
                    int currentPosition = i + 1;
                    while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                        positiveOperationsWithSyntaxUnits.add(syntaxUnits.get(currentPosition));
                        currentPosition++;
                    }
                    i = currentPosition - 1;
                }
            }
        }
        syntaxUnits.clear();
        syntaxUnits.addAll(positiveOperationsWithSyntaxUnits);
        syntaxUnits.addAll(negativeOperationsWithSyntaxUnits);
    }

    public List<SyntaxUnit> getRearrangedSyntaxUnits() throws Exception {
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(
                ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits))
        );
        syntaxUnits = expressionSimplifier.getSimplifiedSyntaxUnit().getSyntaxUnits();
        return ExpressionParser
                .convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits))
                .getSyntaxUnits();
    }
}
