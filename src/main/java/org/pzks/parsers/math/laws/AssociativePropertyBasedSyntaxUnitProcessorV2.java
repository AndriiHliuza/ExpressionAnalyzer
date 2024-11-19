package org.pzks.parsers.math.laws;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.math.laws.units.SyntaxUnitExpression;
import org.pzks.parsers.optimizers.AdditionAndSubtractionOperationsParallelizationOptimizer;
import org.pzks.parsers.optimizers.ExpressionOptimizer;
import org.pzks.parsers.optimizers.MultiplicationAndDivisionOperationsParallelizationOptimizer;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.Color;
import org.pzks.utils.GlobalSettings;

import java.util.*;

public class AssociativePropertyBasedSyntaxUnitProcessorV2 {
    private List<SyntaxUnit> syntaxUnits;
    private final SyntaxUnitExpression syntaxUnitExpression;
    private final Set<String> allGeneratedExpressions = new HashSet<>();

    public AssociativePropertyBasedSyntaxUnitProcessorV2(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        syntaxUnitExpression = new SyntaxUnitExpression(syntaxUnit);
        process(syntaxUnits, syntaxUnitExpression);
        System.out.println("\n" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.GREEN.getAnsiValue() + "Done!" + Color.DEFAULT.getAnsiValue() + "\n");
    }

    private List<List<SyntaxUnit>> extractSyntaxUnitsGroupsDividedWithPlusOperations(List<SyntaxUnit> syntaxUnits) throws Exception {
        List<List<SyntaxUnit>> listOfSyntaxUnitsGroups = new ArrayList<>();
        for (int i = 0; i < syntaxUnits.size(); i++) {
            List<SyntaxUnit> syntaxUnitsGroup = new ArrayList<>();
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);

            if (currentSyntaxUnit instanceof Operation && currentSyntaxUnit.getValue().matches("[+\\-]")) {
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 2);
                    if (nextOperationAsSyntaxUnit instanceof Operation) {
                        int currentPosition = i + 1;
                        while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                            SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                            syntaxUnitsGroup.add(syntaxUnitToAdd);
                            currentPosition++;
                        }

                        i = currentPosition - 1;
                    }
                } else if (i + 1 < syntaxUnits.size()) {
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);
                    syntaxUnitsGroup.add(nextSyntaxUnit);

                    i = i + 1;
                }
            } else if (i == 0) {
                if (i + 1 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    if (nextOperationAsSyntaxUnit instanceof Operation) {
                        syntaxUnitsGroup.add(currentSyntaxUnit);
                        int currentPosition = i + 1;
                        while (currentPosition < syntaxUnits.size() && !syntaxUnits.get(currentPosition).getValue().matches("[+\\-]")) {
                            SyntaxUnit syntaxUnitToAdd = syntaxUnits.get(currentPosition);
                            syntaxUnitsGroup.add(syntaxUnitToAdd);
                            currentPosition++;
                        }

                        i = currentPosition - 1;
                    }
                } else {
                    syntaxUnitsGroup.add(currentSyntaxUnit);
                    i = i + 1;
                }
            }

            if (!syntaxUnitsGroup.isEmpty()) {
                listOfSyntaxUnitsGroups.add(syntaxUnitsGroup);
            }
        }

        return listOfSyntaxUnitsGroups;
    }

    private List<List<SyntaxUnit>> generateExpressionsAccordingToAssociativePropertyFromListOfSyntaxUnitsGroups(List<List<SyntaxUnit>> listOfSyntaxUnitsGroups) throws Exception {
        List<List<SyntaxUnit>> listOfSyntaxUnitsAsExpressions = new ArrayList<>();
        for (int i = 0; i < listOfSyntaxUnitsGroups.size(); i++) {
            List<SyntaxUnit> currentSyntaxUnitsGroup = listOfSyntaxUnitsGroups.get(i);

            List<SyntaxUnit> commonSyntaxUnitsExcludingOperations = new ArrayList<>();
            List<List<SyntaxUnit>> syntaxUnitsGroupsWithCommonSyntaxUnits = new ArrayList<>();
            List<List<SyntaxUnit>> syntaxUnitsGroupsWithNoCommonElements = new ArrayList<>();

            List<List<SyntaxUnit>> remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits = new ArrayList<>();
            for (int j = i + 1; j < listOfSyntaxUnitsGroups.size(); j++) {
                List<SyntaxUnit> followingSyntaxUnitGroup = listOfSyntaxUnitsGroups.get(j);

                if (commonSyntaxUnitsExcludingOperations.isEmpty()) {
                    commonSyntaxUnitsExcludingOperations = findCommonSyntaxUnitsInTwoSyntaxUnitsGroups(currentSyntaxUnitsGroup, followingSyntaxUnitGroup);
                    if (!commonSyntaxUnitsExcludingOperations.isEmpty()) {
                        syntaxUnitsGroupsWithCommonSyntaxUnits.addAll(List.of(currentSyntaxUnitsGroup, followingSyntaxUnitGroup));
                    } else {
                        remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits.add(followingSyntaxUnitGroup);
                    }
                } else {
                    boolean isContainsCommonSyntaxUnits = isTwoSyntaxUnitsGroupsContainsTheProvidedCommonSyntaxUnits(currentSyntaxUnitsGroup, followingSyntaxUnitGroup, commonSyntaxUnitsExcludingOperations);
                    if (isContainsCommonSyntaxUnits) {
                        syntaxUnitsGroupsWithCommonSyntaxUnits.add(followingSyntaxUnitGroup);
                    } else {
                        remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits.add(followingSyntaxUnitGroup);
                    }
                }

                if (j == listOfSyntaxUnitsGroups.size() - 1 &&
                        !commonSyntaxUnitsExcludingOperations.isEmpty() &&
                        !syntaxUnitsGroupsWithCommonSyntaxUnits.isEmpty()
                ) {
                    for (List<SyntaxUnit> syntaxUnitsGroup : syntaxUnitsGroupsWithCommonSyntaxUnits) {
                        List<SyntaxUnit> syntaxUnitsGroupExcludingCommonElements = createSyntaxUnitsGroupExcludingCommonElements(syntaxUnitsGroup, commonSyntaxUnitsExcludingOperations);
                        syntaxUnitsGroupsWithNoCommonElements.add(syntaxUnitsGroupExcludingCommonElements);
                    }
                }
            }
            if (commonSyntaxUnitsExcludingOperations.isEmpty()) {
                remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits.add(currentSyntaxUnitsGroup);
            }

            List<SyntaxUnit> newSyntaxUnitsExpression = createNewSyntaxUnitsGroup(commonSyntaxUnitsExcludingOperations, syntaxUnitsGroupsWithNoCommonElements, remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits);
            if (!newSyntaxUnitsExpression.isEmpty()) {
                listOfSyntaxUnitsAsExpressions.add(newSyntaxUnitsExpression);

                if (allGeneratedExpressions.size() + listOfSyntaxUnitsAsExpressions.size() > GlobalSettings.NUMBER_OF_GENERATED_EXCEPTIONS_LIMIT) {
                    return listOfSyntaxUnitsAsExpressions;
                }
            }

            System.out.println(ExpressionParser.getExpressionAsString(newSyntaxUnitsExpression));
        }

        return listOfSyntaxUnitsAsExpressions;
    }

    private boolean isTwoSyntaxUnitsGroupsContainsTheProvidedCommonSyntaxUnits(List<SyntaxUnit> currentSyntaxUnitsGroup, List<SyntaxUnit> followingSyntaxUnitGroup, List<SyntaxUnit> commonSyntaxUnits) {
        List<SyntaxUnit> commonSyntaxUnitsFromGroups = findCommonSyntaxUnitsInTwoSyntaxUnitsGroups(currentSyntaxUnitsGroup, followingSyntaxUnitGroup);
        List<SyntaxUnit> commonSyntaxUnitsBetweenCommonSyntaxUnitsFromGroupsAndProvidedCommonSyntaxUnits = findCommonSyntaxUnitsInTwoSyntaxUnitsGroups(commonSyntaxUnitsFromGroups, commonSyntaxUnits);
        return !commonSyntaxUnitsBetweenCommonSyntaxUnitsFromGroupsAndProvidedCommonSyntaxUnits.isEmpty();
    }

    private List<SyntaxUnit> findCommonSyntaxUnitsInTwoSyntaxUnitsGroups(List<SyntaxUnit> currentSyntaxUnitsGroup, List<SyntaxUnit> followingSyntaxUnitGroup) {
        List<SyntaxUnit> commonSyntaxUnits = currentSyntaxUnitsGroup.stream()
                .filter(syntaxUnitFromCurrentSyntaxUnitsGroup -> followingSyntaxUnitGroup
                        .stream()
                        .anyMatch(syntaxUnitFromFollowingSyntaxUnitsGroup -> syntaxUnitFromCurrentSyntaxUnitsGroup.getValue()
                                .equals(syntaxUnitFromFollowingSyntaxUnitsGroup.getValue()) &&
                                !(syntaxUnitFromCurrentSyntaxUnitsGroup instanceof Operation) &&
                                !(syntaxUnitFromFollowingSyntaxUnitsGroup instanceof Operation)
                        )
                ).toList();


        Map<String, Integer> commonSyntaxUnitsMap = new HashMap<>();
        for (SyntaxUnit syntaxUnit : followingSyntaxUnitGroup) {
            commonSyntaxUnitsMap.put(syntaxUnit.getValue(), commonSyntaxUnitsMap.getOrDefault(syntaxUnit.getValue(), 0) + 1);
        }

        List<SyntaxUnit> resultListOfCommonSyntaxUnits = new ArrayList<>();
        Map<String, Integer> addedSyntaxUnitsValueCountMap = new HashMap<>();

        for (SyntaxUnit syntaxUnit : commonSyntaxUnits) {
            String value = syntaxUnit.getValue();
            int countOfSyntaxUnitInFollowingGroup = commonSyntaxUnitsMap.getOrDefault(value, 0);
            int countOfAddedSyntaxUnitsInCountMap = addedSyntaxUnitsValueCountMap.getOrDefault(value, 0);

            if (countOfAddedSyntaxUnitsInCountMap < countOfSyntaxUnitInFollowingGroup) {
                resultListOfCommonSyntaxUnits.add(syntaxUnit);
                addedSyntaxUnitsValueCountMap.put(value, countOfAddedSyntaxUnitsInCountMap + 1);
            }
        }


        return resultListOfCommonSyntaxUnits;
    }

    private List<SyntaxUnit> createNewSyntaxUnitsGroup(
            List<SyntaxUnit> commonSyntaxUnitsWithNoOperations,
            List<List<SyntaxUnit>> syntaxUnitsGroupsWithNoCommonElements,
            List<List<SyntaxUnit>> remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits
    ) {
        List<SyntaxUnit> commonElements = new ArrayList<>();

        List<SyntaxUnit> syntaxUnitsAsExpression = new ArrayList<>();
        if (!commonSyntaxUnitsWithNoOperations.isEmpty()) {
            for (int i = 0; i < commonSyntaxUnitsWithNoOperations.size(); i++) {
                commonElements.add(commonSyntaxUnitsWithNoOperations.get(i));

                if (i != commonSyntaxUnitsWithNoOperations.size() - 1) {
                    commonElements.add(new Operation(0, "*"));
                }
            }

            syntaxUnitsAsExpression.addAll(commonElements);
            syntaxUnitsAsExpression.add(new Operation(0, "*"));
            LogicalBlock logicalBlock = new LogicalBlock();
            for (int i = 0; i < syntaxUnitsGroupsWithNoCommonElements.size(); i++) {
                List<SyntaxUnit> syntaxUnitsGroupWithNoCommonElements = syntaxUnitsGroupsWithNoCommonElements.get(i);
                logicalBlock.getSyntaxUnits().addAll(syntaxUnitsGroupWithNoCommonElements);
                if (i != syntaxUnitsGroupsWithNoCommonElements.size() - 1) {
                    logicalBlock.getSyntaxUnits().add(new Operation(0, "+"));
                }
            }
            syntaxUnitsAsExpression.add(logicalBlock);

            if (!remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits.isEmpty()) {
                syntaxUnitsAsExpression.add(new Operation(0, "+"));
            }

            for (int i = 0; i < remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits.size(); i++) {
                List<SyntaxUnit> remainingSyntaxUnitsGroupAfterFindingCommonSyntaxUnits = remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits.get(i);
                syntaxUnitsAsExpression.addAll(remainingSyntaxUnitsGroupAfterFindingCommonSyntaxUnits);
                if (i != remainingSyntaxUnitsGroupsAfterFindingCommonSyntaxUnits.size() - 1) {
                    logicalBlock.getSyntaxUnits().add(new Operation(0, "+"));
                }
            }


        }
        return syntaxUnitsAsExpression;
    }

    private List<SyntaxUnit> createSyntaxUnitsGroupExcludingCommonElements(List<SyntaxUnit> providedSyntaxUnitsGroup, List<SyntaxUnit> commonSyntaxUnitsWithNoOperations) {
        List<SyntaxUnit> syntaxUnitsGroupExcludingCommonElementsAndOperations = excludeOperationsFromProvidedSyntaxUnitsGroup(providedSyntaxUnitsGroup);
        syntaxUnitsGroupExcludingCommonElementsAndOperations = excludeCommonSyntaxUnitsFromProvidedSyntaxUnitsGroup(syntaxUnitsGroupExcludingCommonElementsAndOperations, commonSyntaxUnitsWithNoOperations);

        List<SyntaxUnit> currentSyntaxUnitsGroupExcludingCommonSyntaxUnits = new ArrayList<>();
        if (!syntaxUnitsGroupExcludingCommonElementsAndOperations.isEmpty()) {
            for (int k = 0; k < syntaxUnitsGroupExcludingCommonElementsAndOperations.size(); k++) {
                currentSyntaxUnitsGroupExcludingCommonSyntaxUnits.add(syntaxUnitsGroupExcludingCommonElementsAndOperations.get(k));

                if (k != syntaxUnitsGroupExcludingCommonElementsAndOperations.size() - 1) {
                    currentSyntaxUnitsGroupExcludingCommonSyntaxUnits.add(new Operation(0, "*"));
                }
            }
        } else {
            currentSyntaxUnitsGroupExcludingCommonSyntaxUnits.add(new Number(0, "1"));
        }

        return currentSyntaxUnitsGroupExcludingCommonSyntaxUnits;
    }

    private List<SyntaxUnit> excludeOperationsFromProvidedSyntaxUnitsGroup(List<SyntaxUnit> providedSyntaxUnitsGroup) {
        List<SyntaxUnit> syntaxUnitsGroupExcludingCommonElementsAndOperations = new ArrayList<>();
        for (SyntaxUnit syntaxUnit : providedSyntaxUnitsGroup) {
            if (!(syntaxUnit instanceof Operation)) {
                syntaxUnitsGroupExcludingCommonElementsAndOperations.add(syntaxUnit);
            }
        }
        return syntaxUnitsGroupExcludingCommonElementsAndOperations;
    }

    private List<SyntaxUnit> excludeCommonSyntaxUnitsFromProvidedSyntaxUnitsGroup(List<SyntaxUnit> providedSyntaxUnitsGroup, List<SyntaxUnit> commonSyntaxUnitsWithNoOperations) {
        List<SyntaxUnit> syntaxUnitsGroupExcludingCommonElementsAndOperations = new ArrayList<>(providedSyntaxUnitsGroup);
        for (SyntaxUnit commonSyntaxUnit : commonSyntaxUnitsWithNoOperations) {
            syntaxUnitsGroupExcludingCommonElementsAndOperations.stream()
                    .filter(syntaxUnit -> syntaxUnit.getValue().equals(commonSyntaxUnit.getValue()))
                    .findFirst()
                    .ifPresent(syntaxUnitsGroupExcludingCommonElementsAndOperations::remove);
        }
        return syntaxUnitsGroupExcludingCommonElementsAndOperations;
    }

    private List<SyntaxUnit> generateExpressionsAccordingToAssociativeProperty(List<SyntaxUnit> syntaxUnits) throws Exception {
        MultiplicationAndDivisionOperationsParallelizationOptimizer.replaceDivisionWithMultiplication(syntaxUnits);
        AdditionAndSubtractionOperationsParallelizationOptimizer.replaceSubtractionWithAddition(syntaxUnits);

        List<List<SyntaxUnit>> listOfSyntaxUnitsGroups = extractSyntaxUnitsGroupsDividedWithPlusOperations(syntaxUnits);

        List<List<SyntaxUnit>> listOfSyntaxUnitsAsExpressions = generateExpressionsAccordingToAssociativePropertyFromListOfSyntaxUnitsGroups(listOfSyntaxUnitsGroups);

        List<SyntaxUnit> generatedExpressions = new ArrayList<>();
        for (List<SyntaxUnit> syntaxUnitsAsExpression : listOfSyntaxUnitsAsExpressions) {
            SyntaxUnit generatedExpression = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnitsAsExpression));
            ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(generatedExpression);
            generatedExpression = expressionSimplifier.getSimplifiedSyntaxUnit();
            ExpressionOptimizer expressionOptimizer = new ExpressionOptimizer(generatedExpression);
            generatedExpression = expressionOptimizer.getOptimizedSyntaxUnit();
            generatedExpression = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(generatedExpression.getSyntaxUnits()));

            generatedExpressions.add(generatedExpression);
        }
        return generatedExpressions;
    }

    private void process(List<SyntaxUnit> syntaxUnits, SyntaxUnitExpression syntaxUnitExpression) throws Exception {
        generatedSyntaxUnitsExpressionsByProcessingSyntaxContainersOfOriginalSyntaxUnits(syntaxUnits, syntaxUnitExpression);
        if (allGeneratedExpressions.size() > GlobalSettings.NUMBER_OF_GENERATED_EXCEPTIONS_LIMIT) {
            return;
        }
        generatedSyntaxUnitsExpressionsByProcessingInsideSyntaxContainersOfOriginalSyntaxUnits(syntaxUnits, syntaxUnitExpression);
    }

    private void generatedSyntaxUnitsExpressionsByProcessingSyntaxContainersOfOriginalSyntaxUnits(List<SyntaxUnit> syntaxUnits, SyntaxUnitExpression syntaxUnitExpression) throws Exception {
        List<SyntaxUnit> generatedExpressions = generateExpressionsAccordingToAssociativeProperty(
                ExpressionParser.convertExpressionToParsedSyntaxUnit(
                        ExpressionParser.getExpressionAsString(syntaxUnits)
                ).getSyntaxUnits()
        );

        saveGeneratedSyntaxUnitsExpression(generatedExpressions, syntaxUnitExpression);
    }

    private void generatedSyntaxUnitsExpressionsByProcessingInsideSyntaxContainersOfOriginalSyntaxUnits(List<SyntaxUnit> syntaxUnits, SyntaxUnitExpression syntaxUnitExpression) throws Exception {
        syntaxUnits = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits)).getSyntaxUnits();
        SyntaxUnit providedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits));
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(providedSyntaxUnit);
        providedSyntaxUnit = expressionSimplifier.getSimplifiedSyntaxUnit();
        ExpressionOptimizer expressionOptimizer = new ExpressionOptimizer(providedSyntaxUnit);
        providedSyntaxUnit = expressionOptimizer.getOptimizedSyntaxUnit();
        syntaxUnits = providedSyntaxUnit.getSyntaxUnits();

        List<SyntaxUnit> generatedExpressions = new ArrayList<>();

        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (currentSyntaxUnit instanceof SyntaxContainer) {
                if (currentSyntaxUnit instanceof LogicalBlock) {
                    List<SyntaxUnit> generatedExpressionsFromSyntaxContainer = generateExpressionsAccordingToAssociativeProperty(
                            ExpressionParser.convertExpressionToParsedSyntaxUnit(
                                    ExpressionParser.getExpressionAsString(currentSyntaxUnit.getSyntaxUnits())
                            ).getSyntaxUnits()
                    );

                    if (!generatedExpressionsFromSyntaxContainer.isEmpty()) {
                        for (SyntaxUnit generatedExpression : generatedExpressionsFromSyntaxContainer) {
                            currentSyntaxUnit.setSyntaxUnits(generatedExpression.getSyntaxUnits());
                            generatedExpressions.add(ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits)));
                        }
                    }
                } else if (currentSyntaxUnit instanceof Function) {
                    for (SyntaxUnit functionParam : currentSyntaxUnit.getSyntaxUnits()) {
                        if (functionParam instanceof FunctionParam) {
                            List<SyntaxUnit> generatedExpressionsFromSyntaxContainer = generateExpressionsAccordingToAssociativeProperty(
                                    ExpressionParser.convertExpressionToParsedSyntaxUnit(
                                            ExpressionParser.getExpressionAsString(functionParam.getSyntaxUnits())
                                    ).getSyntaxUnits()
                            );

                            if (!generatedExpressionsFromSyntaxContainer.isEmpty()) {
                                for (SyntaxUnit generatedExpression : generatedExpressionsFromSyntaxContainer) {
                                    functionParam.setSyntaxUnits(generatedExpression.getSyntaxUnits());
                                    generatedExpressions.add(ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits)));
                                }
                            }
                        }
                    }
                }
            }
        }

        saveGeneratedSyntaxUnitsExpression(generatedExpressions, syntaxUnitExpression);
    }

    private void saveGeneratedSyntaxUnitsExpression(List<SyntaxUnit> generatedExpressions, SyntaxUnitExpression syntaxUnitExpression) throws Exception {
        for (SyntaxUnit generatedExpression : generatedExpressions) {
            String generatesExpressionStrRepresentation = ExpressionParser.getExpressionAsString(generatedExpression.getSyntaxUnits());
            boolean wasNewExpressionSaved = allGeneratedExpressions.add(generatesExpressionStrRepresentation);
            if (wasNewExpressionSaved) {
                System.out.print("\r" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Log [Number of generated expressions]: " + Color.DEFAULT.getAnsiValue() + allGeneratedExpressions.size());

                SyntaxUnitExpression currentSyntaxUnitExpression = new SyntaxUnitExpression(
                        ExpressionParser.convertExpressionToParsedSyntaxUnit(generatesExpressionStrRepresentation)
                );
                syntaxUnitExpression.getSyntaxUnitExpressions().add(currentSyntaxUnitExpression);

                if (allGeneratedExpressions.size() > GlobalSettings.NUMBER_OF_GENERATED_EXCEPTIONS_LIMIT) {
                    return;
                }

                process(generatedExpression.getSyntaxUnits(), currentSyntaxUnitExpression);
            }
        }
    }


    public SyntaxUnitExpression getSyntaxUnitExpression() {
        return syntaxUnitExpression;
    }

    public int getNumberOfGeneratedExpressions() {
        return allGeneratedExpressions.size();
    }
}
