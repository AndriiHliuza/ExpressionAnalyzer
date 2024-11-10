package org.pzks.parsers.math.laws;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.Operation;
import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CommutativePropertyBasedSyntaxUnitProcessor {
    private List<SyntaxUnit> syntaxUnits;

    public CommutativePropertyBasedSyntaxUnitProcessor(SyntaxUnit syntaxUnit) {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        process(syntaxUnits);
    }

    private void process(List<SyntaxUnit> syntaxUnits) {
        List<List<SyntaxUnit>> sortedListOfSyntaxUnits = new ArrayList<>();
        for (int i = 0; i < syntaxUnits.size(); i++) {
            List<SyntaxUnit> syntaxUnitsWithMultiplicationAndDivisionOperations = new ArrayList<>();

            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);

            if (currentSyntaxUnit instanceof Operation && currentSyntaxUnit.getValue().matches("[+\\-]")) {
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 2);
                    if (nextOperationAsSyntaxUnit instanceof Operation && nextOperationAsSyntaxUnit.getValue().matches("[*/]")) {
                        int startingPosition = i;
                        int endingPosition = i;
                        syntaxUnitsWithMultiplicationAndDivisionOperations.add(currentSyntaxUnit);
                        int currentPosition = i + 1;
                        while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                            SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                            if (syntaxUnitToAdd instanceof SyntaxContainer) {
                                process(syntaxUnitToAdd.getSyntaxUnits());
                            }
                            syntaxUnitsWithMultiplicationAndDivisionOperations.add(syntaxUnitToAdd);
                            currentPosition++;
                            endingPosition = currentPosition;
                        }

                        syntaxUnits.subList(startingPosition, endingPosition).clear();
                        i = startingPosition - 1;
                    }
                }
            } else if (i == 0) {
                if (currentSyntaxUnit instanceof SyntaxContainer) {
                    process(currentSyntaxUnit.getSyntaxUnits());
                }
                if (i + 1 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    if (nextOperationAsSyntaxUnit instanceof Operation && nextOperationAsSyntaxUnit.getValue().matches("[*/]")) {
                        int startingPosition = i;
                        int endingPosition = i;
                        syntaxUnitsWithMultiplicationAndDivisionOperations.add(new Operation(0, "+"));
                        syntaxUnitsWithMultiplicationAndDivisionOperations.add(currentSyntaxUnit);
                        int currentPosition = i + 1;
                        while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                            SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                            if (syntaxUnitToAdd instanceof SyntaxContainer) {
                                process(syntaxUnitToAdd.getSyntaxUnits());
                            }
                            syntaxUnitsWithMultiplicationAndDivisionOperations.add(syntaxUnitToAdd);
                            currentPosition++;
                            endingPosition = currentPosition;
                        }

                        syntaxUnits.subList(startingPosition, endingPosition).clear();
                        i = -1;
                    }
                }
            } else if (currentSyntaxUnit instanceof SyntaxContainer) {
                process(currentSyntaxUnit.getSyntaxUnits());
            }


            if (!syntaxUnitsWithMultiplicationAndDivisionOperations.isEmpty()) {
                sortedListOfSyntaxUnits.add(syntaxUnitsWithMultiplicationAndDivisionOperations);
            }
        }
        sortedListOfSyntaxUnits.sort(Comparator.comparingInt(List::size));

        if (!sortedListOfSyntaxUnits.isEmpty()) {
            if (!syntaxUnits.isEmpty() && !(syntaxUnits.getFirst() instanceof Operation)) {
                syntaxUnits.addFirst(new Operation(0, "+"));
            }
            for (List<SyntaxUnit> syntaxUnitsWithMultiplicationAndDivisionOps : sortedListOfSyntaxUnits) {
                syntaxUnits.addAll(0, syntaxUnitsWithMultiplicationAndDivisionOps);
            }
        }
    }


    public SyntaxUnit getProcessedSyntaxUnit() throws Exception {
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(
                ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits))
        );
        syntaxUnits = expressionSimplifier.getSimplifiedSyntaxUnit().getSyntaxUnits();

        return ExpressionParser
                .convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits));
    }
}
