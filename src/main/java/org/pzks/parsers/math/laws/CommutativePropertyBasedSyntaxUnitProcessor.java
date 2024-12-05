package org.pzks.parsers.math.laws;

import org.pzks.parsers.ExpressionProcessor;
import org.pzks.parsers.converters.ExpressionConverter;
import org.pzks.parsers.optimizers.AdditionAndSubtractionOperationsParallelizationOptimizer;
import org.pzks.parsers.optimizers.ExpressionOptimizer;
import org.pzks.parsers.optimizers.MultiplicationAndDivisionOperationsParallelizationOptimizer;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.*;
import org.pzks.units.Number;

import java.util.ArrayList;
import java.util.List;

public class CommutativePropertyBasedSyntaxUnitProcessor {
    private List<SyntaxUnit> syntaxUnits;

    public CommutativePropertyBasedSyntaxUnitProcessor(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        process(syntaxUnits);
    }

    private void process(List<SyntaxUnit> syntaxUnits) throws Exception {
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
        sortedListOfSyntaxUnits.sort((list1, list2) -> Integer.compare(list2.size(), list1.size()));
        processSyntaxUnitsWithMultiplicationOperationsBasedOnSyntaxContainersWeights(sortedListOfSyntaxUnits);
        processSyntaxUnitsWithoutMultiplicationOperationsBasedOnSyntaxContainersWeights(syntaxUnits);
        List<List<SyntaxUnit>> reversedSortedListOfSyntaxUnits = sortedListOfSyntaxUnits.reversed();
        if (!sortedListOfSyntaxUnits.isEmpty()) {
            if (!syntaxUnits.isEmpty() && !(syntaxUnits.getFirst() instanceof Operation) && !(syntaxUnits.getFirst() instanceof Number number && number.getValue().matches("-\\d+(\\.\\d+)?"))) {
                syntaxUnits.addFirst(new Operation(0, "+"));
            }
            for (List<SyntaxUnit> syntaxUnitsWithMultiplicationAndDivisionOps : reversedSortedListOfSyntaxUnits) {
                syntaxUnits.addAll(0, syntaxUnitsWithMultiplicationAndDivisionOps);
            }
        }
    }

    private void processSyntaxUnitsWithoutMultiplicationOperationsBasedOnSyntaxContainersWeights(List<SyntaxUnit> providedSyntaxUnits) throws Exception {
        if (!providedSyntaxUnits.isEmpty()) {
            SyntaxUnit parsedSyntaxUnit = ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(providedSyntaxUnits));
            ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(parsedSyntaxUnit);
            SyntaxUnit simplifiedSyntaxUnit = expressionSimplifier.getSimplifiedSyntaxUnit();
            ExpressionOptimizer expressionOptimizer = new ExpressionOptimizer(simplifiedSyntaxUnit);
            SyntaxUnit optimizedSyntaxUnit = expressionOptimizer.getOptimizedSyntaxUnit();
            MultiplicationAndDivisionOperationsParallelizationOptimizer.replaceDivisionWithMultiplication(optimizedSyntaxUnit.getSyntaxUnits());
            AdditionAndSubtractionOperationsParallelizationOptimizer.replaceSubtractionWithAddition(optimizedSyntaxUnit.getSyntaxUnits());
            List<SyntaxUnit> syntaxUnits = optimizedSyntaxUnit.getSyntaxUnits();

            List<List<SyntaxUnit>> syntaxUnitsGroups = new ArrayList<>();
            for (int i = 0; i < syntaxUnits.size(); i++) {
                SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);

                List<SyntaxUnit> syntaxUnitsGroup = new ArrayList<>();
                if (currentSyntaxUnit instanceof Operation operation && operation.getValue().equals("+")) {
                    if (i + 2 < syntaxUnits.size()) {
                        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 2);
                        if (nextOperationAsSyntaxUnit instanceof Operation && nextOperationAsSyntaxUnit.getValue().equals("*")) {
                            syntaxUnitsGroup.add(currentSyntaxUnit);
                            int currentPosition = i + 1;
                            while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().equals("+")) {
                                SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                                syntaxUnitsGroup.add(syntaxUnitToAdd);
                                currentPosition++;
                            }
                            i = currentPosition - 1;
                        } else {
                            SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);
                            syntaxUnitsGroup.add(currentSyntaxUnit);
                            syntaxUnitsGroup.add(nextSyntaxUnit);
                        }
                    } else if (i + 1 < syntaxUnits.size()) {
                        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);
                        syntaxUnitsGroup.add(currentSyntaxUnit);
                        syntaxUnitsGroup.add(nextSyntaxUnit);
                    }
                } else if (i == 0) {
                    if (i + 1 < syntaxUnits.size()) {
                        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                        if (nextOperationAsSyntaxUnit instanceof Operation && nextOperationAsSyntaxUnit.getValue().equals("*")) {
                            syntaxUnitsGroup.add(new Operation(0, "+"));
                            syntaxUnitsGroup.add(currentSyntaxUnit);
                            int currentPosition = i + 1;
                            while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().equals("+")) {
                                SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                                syntaxUnitsGroup.add(syntaxUnitToAdd);
                                currentPosition++;
                            }
                            i = currentPosition - 1;
                        } else {
                            syntaxUnitsGroup.add(new Operation(0, "+"));
                            syntaxUnitsGroup.add(currentSyntaxUnit);
                        }
                    } else {
                        syntaxUnitsGroup.add(new Operation(0, "+"));
                        syntaxUnitsGroup.add(currentSyntaxUnit);
                    }
                }

                if (!syntaxUnitsGroup.isEmpty()) {
                    syntaxUnitsGroups.add(syntaxUnitsGroup);
                }
            }

            processSyntaxUnitsWithMultiplicationOperationsBasedOnSyntaxContainersWeights(syntaxUnitsGroups);

            providedSyntaxUnits.clear();
            for (List<SyntaxUnit> syntaxUnitsGroup : syntaxUnitsGroups) {
                providedSyntaxUnits.addAll(syntaxUnitsGroup);
            }
        }
    }

    private void processSyntaxUnitsWithMultiplicationOperationsBasedOnSyntaxContainersWeights(List<List<SyntaxUnit>> providedSyntaxUnitsGroups) throws Exception {
        if (!providedSyntaxUnitsGroups.isEmpty()) {
            List<List<SyntaxUnit>> syntaxUnitsGroupsWithSyntaxContainers = new ArrayList<>();
            List<List<SyntaxUnit>> otherSyntaxUnitsGroupsWithoutSyntaxContainers = new ArrayList<>();
            for (int i = 0; i < providedSyntaxUnitsGroups.size(); i++) {
                List<SyntaxUnit> syntaxUnitsGroup = providedSyntaxUnitsGroups.get(i);
                SyntaxUnit parsedSyntaxUnit = ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(syntaxUnitsGroup));
                MultiplicationAndDivisionOperationsParallelizationOptimizer.replaceDivisionWithMultiplication(parsedSyntaxUnit.getSyntaxUnits());
                AdditionAndSubtractionOperationsParallelizationOptimizer.replaceSubtractionWithAddition(parsedSyntaxUnit.getSyntaxUnits());
                syntaxUnitsGroup = parsedSyntaxUnit.getSyntaxUnits();

                List<SyntaxUnit> syntaxUnitsWithNoOperationsInGroup = new ArrayList<>();
                for (int j = 0; j < syntaxUnitsGroup.size(); j++) {
                    SyntaxUnit syntaxUnitInGroup = syntaxUnitsGroup.get(j);
                    if (!(syntaxUnitInGroup instanceof Operation)) {
                        syntaxUnitsWithNoOperationsInGroup.add(syntaxUnitInGroup);
                    }
                }

                boolean isSyntaxContainerPresentInSyntaxUnitsGroup = false;
                List<SyntaxUnit> sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations = new ArrayList<>();
                List<SyntaxUnit> syntaxContainersInGroup = new ArrayList<>();
                List<SyntaxUnit> otherSyntaxUnitsInGroup = new ArrayList<>();
                for (SyntaxUnit syntaxUnitInGroup : syntaxUnitsWithNoOperationsInGroup) {
                    if (syntaxUnitInGroup instanceof LogicalBlock logicalBlock) {
                        if (logicalBlock.getSyntaxUnits().size() == 1) {
                            SyntaxUnit syntaxUnitInLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                            if (syntaxUnitInLogicalBlock instanceof Number number && number.getValue().matches("-1|-1.0")) {
                                otherSyntaxUnitsInGroup.addFirst(syntaxUnitInGroup);
                            } else {
                                syntaxContainersInGroup.add(syntaxUnitInGroup);
                                isSyntaxContainerPresentInSyntaxUnitsGroup = true;
                            }
                        } else if (logicalBlock.getSyntaxUnits().size() == 3) {
                            SyntaxUnit firstSyntaxUnitInLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                            SyntaxUnit secondSyntaxUnitInLogicalBlock = logicalBlock.getSyntaxUnits().get(1);
                            SyntaxUnit thirdSyntaxUnitInLogicalBlock = logicalBlock.getSyntaxUnits().getLast();
                            if (firstSyntaxUnitInLogicalBlock instanceof Number number &&
                                    number.getValue().matches("-1|-1.0|1|1.0") &&
                                    secondSyntaxUnitInLogicalBlock instanceof Operation operation &&
                                    operation.getValue().equals("/")) {
                                if (thirdSyntaxUnitInLogicalBlock instanceof LogicalBlock || thirdSyntaxUnitInLogicalBlock instanceof Function) {
                                    syntaxContainersInGroup.add(syntaxUnitInGroup);
                                    isSyntaxContainerPresentInSyntaxUnitsGroup = true;
                                } else {
                                    otherSyntaxUnitsInGroup.add(syntaxUnitInGroup);
                                }
                            } else {
                                syntaxContainersInGroup.add(syntaxUnitInGroup);
                                isSyntaxContainerPresentInSyntaxUnitsGroup = true;
                            }
                        } else {
                            syntaxContainersInGroup.add(syntaxUnitInGroup);
                            isSyntaxContainerPresentInSyntaxUnitsGroup = true;
                        }
                    } else if (syntaxUnitInGroup instanceof Function) {
                        syntaxContainersInGroup.add(syntaxUnitInGroup);
                        isSyntaxContainerPresentInSyntaxUnitsGroup = true;
                    } else {
                        otherSyntaxUnitsInGroup.add(syntaxUnitInGroup);
                    }
                }
                sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations.addAll(syntaxContainersInGroup);
                sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations.addAll(otherSyntaxUnitsInGroup);

                List<SyntaxUnit> sortedSyntaxUnitsInGroupBySyntaxContainerWeights = new ArrayList<>();
                for (int j = 0; j < sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations.size(); j++) {
                    SyntaxUnit syntaxUnitInGroup = sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations.get(j);
                    sortedSyntaxUnitsInGroupBySyntaxContainerWeights.add(syntaxUnitInGroup);
                    if (j != sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations.size() - 1) {
                        SyntaxUnit nextSyntaxUnitInGroup = sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations.get(j + 1);
                        if (nextSyntaxUnitInGroup instanceof Number number && number.getValue().matches("-\\d+(\\.\\d+)?")) {
                            LogicalBlock logicalBlock = new LogicalBlock();
                            logicalBlock.getSyntaxUnits().add(nextSyntaxUnitInGroup);
                            sortedSyntaxUnitsInGroupBySyntaxContainerWeightsWithNoOperations.set(j + 1, logicalBlock);
                        }
                        sortedSyntaxUnitsInGroupBySyntaxContainerWeights.add(new Operation(0, "*"));
                    }
                }

                SyntaxUnit processedSyntaxUnit = ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(sortedSyntaxUnitsInGroupBySyntaxContainerWeights));
                ExpressionSimplifier expressionSimplifierForProcessedSyntaxUnit = new ExpressionSimplifier(processedSyntaxUnit);
                processedSyntaxUnit = expressionSimplifierForProcessedSyntaxUnit.getSimplifiedSyntaxUnit();
                ExpressionOptimizer expressionOptimizerForProcessedSyntaxUnit = new ExpressionOptimizer(processedSyntaxUnit);
                processedSyntaxUnit = expressionOptimizerForProcessedSyntaxUnit.getOptimizedSyntaxUnit();

                if (isSyntaxContainerPresentInSyntaxUnitsGroup) {
                    syntaxUnitsGroupsWithSyntaxContainers.add(processedSyntaxUnit.getSyntaxUnits());
                } else {
                    otherSyntaxUnitsGroupsWithoutSyntaxContainers.add(processedSyntaxUnit.getSyntaxUnits());
                }
            }
            providedSyntaxUnitsGroups.clear();

            syntaxUnitsGroupsWithSyntaxContainers.sort((list1, list2) -> Integer.compare(list2.size(), list1.size()));
            otherSyntaxUnitsGroupsWithoutSyntaxContainers.sort((list1, list2) -> Integer.compare(list2.size(), list1.size()));

            providedSyntaxUnitsGroups.addAll(syntaxUnitsGroupsWithSyntaxContainers);
            providedSyntaxUnitsGroups.addAll(otherSyntaxUnitsGroupsWithoutSyntaxContainers);

            for (List<SyntaxUnit> syntaxUnitGroup : providedSyntaxUnitsGroups) {
                if (!(syntaxUnitGroup.getFirst() instanceof Operation) && !(syntaxUnitGroup.getFirst() instanceof Number number && number.getValue().matches("-\\d+(\\.\\d+)?"))) {
                    syntaxUnitGroup.addFirst(new Operation(0, "+"));
                }
            }
        }
    }


    public SyntaxUnit getProcessedSyntaxUnit() throws Exception {
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(
                ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(syntaxUnits))
        );
        syntaxUnits = expressionSimplifier.getSimplifiedSyntaxUnit().getSyntaxUnits();

        return ExpressionConverter
                .convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(syntaxUnits));
    }
}
