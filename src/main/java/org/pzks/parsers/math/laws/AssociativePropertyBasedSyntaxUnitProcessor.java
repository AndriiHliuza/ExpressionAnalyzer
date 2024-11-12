package org.pzks.parsers.math.laws;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.optimizers.AdditionAndSubtractionOperationsParallelizationOptimizer;
import org.pzks.parsers.optimizers.ExpressionOptimizer;
import org.pzks.parsers.optimizers.MultiplicationAndDivisionOperationsParallelizationOptimizer;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.LogicalBlock;
import org.pzks.units.Number;
import org.pzks.units.Operation;
import org.pzks.units.SyntaxUnit;
import org.pzks.utils.SyntaxUnitExpression;

import java.util.ArrayList;
import java.util.List;

public class AssociativePropertyBasedSyntaxUnitProcessor {
    private List<SyntaxUnit> syntaxUnits;
    private SyntaxUnitExpression syntaxUnitExpression;
    private List<String> allGeneratedSyntaxUnits = new ArrayList<>();

    public AssociativePropertyBasedSyntaxUnitProcessor(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        syntaxUnitExpression = new SyntaxUnitExpression(syntaxUnit);
        process(syntaxUnits, syntaxUnitExpression);
    }

    private void process(List<SyntaxUnit> syntaxUnits, SyntaxUnitExpression syntaxUnitExpression) throws Exception {
        MultiplicationAndDivisionOperationsParallelizationOptimizer.replaceDivisionWithMultiplication(syntaxUnits);
        AdditionAndSubtractionOperationsParallelizationOptimizer.replaceSubtractionWithAddition(syntaxUnits);

        List<List<SyntaxUnit>> listOfSyntaxUnitsBlocks = new ArrayList<>();
        for (int i = 0; i < syntaxUnits.size(); i++) {
            List<SyntaxUnit> syntaxUnitsBlock = new ArrayList<>();
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);

            if (currentSyntaxUnit instanceof Operation && currentSyntaxUnit.getValue().matches("[+\\-]")) {
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 2);
                    if (nextOperationAsSyntaxUnit instanceof Operation) {
                        int currentPosition = i + 1;
                        while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                            SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                            syntaxUnitsBlock.add(syntaxUnitToAdd);
                            currentPosition++;
                        }

                        i = currentPosition - 1;
                    }
                } else if (i + 1 < syntaxUnits.size()) {
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);
                    syntaxUnitsBlock.add(nextSyntaxUnit);

                    i = i + 1;
                }
            } else if (i == 0) {
                if (i + 1 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    if (nextOperationAsSyntaxUnit instanceof Operation) {
                        syntaxUnitsBlock.add(currentSyntaxUnit);
                        int currentPosition = i + 1;
                        while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                            SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                            syntaxUnitsBlock.add(syntaxUnitToAdd);
                            currentPosition++;
                        }

                        i = currentPosition - 1;
                    }
                } else {
                    syntaxUnitsBlock.add(currentSyntaxUnit);
                    i = i + 1;
                }
            }

            if (!syntaxUnitsBlock.isEmpty()) {
                listOfSyntaxUnitsBlocks.add(syntaxUnitsBlock);
            }
        }

        List<List<SyntaxUnit>> listOfSyntaxUnitsAsExpressions = new ArrayList<>();

        for (int i = 0; i < listOfSyntaxUnitsBlocks.size(); i++) {
            List<SyntaxUnit> currentSyntaxUnitsBlock = listOfSyntaxUnitsBlocks.get(i);
            for (int j = i + 1; j < listOfSyntaxUnitsBlocks.size(); j++) {
                List<SyntaxUnit> nextSyntaxUnitsBlock = listOfSyntaxUnitsBlocks.get(j);
                List<SyntaxUnit> commonElementsWithNoOperations = currentSyntaxUnitsBlock.stream()
                        .filter(syntaxUnitFromCurrentSyntaxUnitsBlock -> nextSyntaxUnitsBlock
                                .stream()
                                .anyMatch(syntaxUnitFromNextSyntaxUnitsBlock -> syntaxUnitFromCurrentSyntaxUnitsBlock.getValue()
                                        .equals(syntaxUnitFromNextSyntaxUnitsBlock.getValue()) &&
                                        !(syntaxUnitFromCurrentSyntaxUnitsBlock instanceof Operation) &&
                                        !(syntaxUnitFromNextSyntaxUnitsBlock instanceof Operation)
                                )
                        ).toList();

                List<SyntaxUnit> commonElements = new ArrayList<>();
                List<SyntaxUnit> currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations = new ArrayList<>();
                List<SyntaxUnit> nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations = new ArrayList<>();

                for (SyntaxUnit syntaxUnit : currentSyntaxUnitsBlock) {
                    if (!(syntaxUnit instanceof Operation)) {
                        currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.add(syntaxUnit);
                    }
                }

                for (SyntaxUnit common : commonElementsWithNoOperations) {
                    currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.stream()
                            .filter(syntaxUnit -> syntaxUnit.getValue().equals(common.getValue()))
                            .findFirst()
                            .ifPresent(currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations::remove);
                }

                List<SyntaxUnit> currentSyntaxUnitsBlockWithNoCommonElements = new ArrayList<>();
                if (!currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.isEmpty()) {
                    for (int k = 0; k < currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.size(); k++) {
                        currentSyntaxUnitsBlockWithNoCommonElements.add(currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.get(k));

                        if (k != currentSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.size() - 1) {
                            currentSyntaxUnitsBlockWithNoCommonElements.add(new Operation(0, "*"));
                        }
                    }
                } else {
                    currentSyntaxUnitsBlockWithNoCommonElements.add(new Number(0, "1"));
                }




                for (SyntaxUnit syntaxUnit : nextSyntaxUnitsBlock) {
                    if (!(syntaxUnit instanceof Operation)) {
                        nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.add(syntaxUnit);
                    }
                }

                for (SyntaxUnit common : commonElementsWithNoOperations) {
                    nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.stream()
                            .filter(syntaxUnit -> syntaxUnit.getValue().equals(common.getValue()))
                            .findFirst()
                            .ifPresent(nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations::remove);
                }

                List<SyntaxUnit> nextSyntaxUnitsBlockWithNoCommonElements = new ArrayList<>();
                if (!nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.isEmpty()) {
                    for (int k = 0; k < nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.size(); k++) {
                        nextSyntaxUnitsBlockWithNoCommonElements.add(nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.get(k));

                        if (k != nextSyntaxUnitsBlockWithNoCommonElementsAndNoOperations.size() - 1) {
                            nextSyntaxUnitsBlockWithNoCommonElements.add(new Operation(0, "*"));
                        }
                    }
                } else {
                    nextSyntaxUnitsBlockWithNoCommonElements.add(new Number(0, "1"));
                }




                if (!commonElementsWithNoOperations.isEmpty()) {
                    for (int k = 0; k < commonElementsWithNoOperations.size(); k++) {
                        commonElements.add(commonElementsWithNoOperations.get(k));

                        if (k != commonElementsWithNoOperations.size() - 1) {
                            commonElements.add(new Operation(0, "*"));
                        }
                    }

                    List<SyntaxUnit> syntaxUnitsAsExpression = new ArrayList<>();
                    for (int k = 0; k < i; k++) {
                        syntaxUnitsAsExpression.addAll(listOfSyntaxUnitsBlocks.get(k));
                        syntaxUnitsAsExpression.add(new Operation(0, "+"));
                    }

                    syntaxUnitsAsExpression.addAll(commonElements);
                    syntaxUnitsAsExpression.add(new Operation(0, "*"));
                    LogicalBlock logicalBlock = new LogicalBlock();
                    logicalBlock.getSyntaxUnits().addAll(currentSyntaxUnitsBlockWithNoCommonElements);
                    logicalBlock.getSyntaxUnits().add(new Operation(0, "+"));
                    logicalBlock.getSyntaxUnits().addAll(nextSyntaxUnitsBlockWithNoCommonElements);
                    syntaxUnitsAsExpression.add(logicalBlock);

                    for (int k = i + 1; k < j; k++) {
                        syntaxUnitsAsExpression.add(new Operation(0, "+"));
                        syntaxUnitsAsExpression.addAll(listOfSyntaxUnitsBlocks.get(k));
                    }

                    for (int k = j + 1; k < listOfSyntaxUnitsBlocks.size(); k++) {
                        syntaxUnitsAsExpression.add(new Operation(0, "+"));
                        syntaxUnitsAsExpression.addAll(listOfSyntaxUnitsBlocks.get(k));
                    }

                    listOfSyntaxUnitsAsExpressions.add(syntaxUnitsAsExpression);
                }
            }
        }


        for (List<SyntaxUnit> syntaxUnitsAsExpression : listOfSyntaxUnitsAsExpressions) {

            SyntaxUnit parsedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnitsAsExpression));
            ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(parsedSyntaxUnit);
            parsedSyntaxUnit = expressionSimplifier.getSimplifiedSyntaxUnit();
            ExpressionOptimizer expressionOptimizer = new ExpressionOptimizer(parsedSyntaxUnit);
            parsedSyntaxUnit = expressionOptimizer.getOptimizedSyntaxUnit();
            parsedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits()));

            if (!allGeneratedSyntaxUnits.contains(ExpressionParser.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits()))) {
                allGeneratedSyntaxUnits.add(ExpressionParser.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits()));
                SyntaxUnitExpression currentSyntaxUnitExpression = new SyntaxUnitExpression(
                        ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits()))
                );
                syntaxUnitExpression.getSyntaxUnitExpressions().add(currentSyntaxUnitExpression);

                process(parsedSyntaxUnit.getSyntaxUnits(), currentSyntaxUnitExpression);
            }
        }
    }


    public SyntaxUnitExpression getSyntaxUnitExpression() {
        return syntaxUnitExpression;
    }

    public int getNumberOfGeneratedExpressions() {
        return allGeneratedSyntaxUnits.size();
    }
}
