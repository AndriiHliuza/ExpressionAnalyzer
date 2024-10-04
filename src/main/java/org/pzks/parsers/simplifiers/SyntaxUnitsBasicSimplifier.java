package org.pzks.parsers.simplifiers;

import org.pzks.units.*;
import org.pzks.units.Number;

import java.util.ArrayList;
import java.util.List;

public class SyntaxUnitsBasicSimplifier {

    private final List<SyntaxUnit> syntaxUnits;

    public SyntaxUnitsBasicSimplifier(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
    }

    public void removeUnnecessaryBracketsInLogicalBlocks(List<SyntaxUnit> syntaxUnits) {
        if (syntaxUnits == this.syntaxUnits &&
                syntaxUnits.size() == 1 &&
                syntaxUnits.getFirst() instanceof LogicalBlock logicalBlock) {
            List<SyntaxUnit> syntaxUnitsInLogicalBlock = logicalBlock.getSyntaxUnits();
            syntaxUnits.clear();
            syntaxUnits.addAll(syntaxUnitsInLogicalBlock);
        }
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof LogicalBlock && syntaxContainer.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInSyntaxContainer = syntaxContainer.getSyntaxUnits().getFirst();
                    if (!(syntaxUnitInSyntaxContainer instanceof Number number && number.getValue().matches("[+\\-]\\d+(\\.\\d+)?"))) {
                        syntaxUnits.set(i, syntaxUnitInSyntaxContainer);
                    } else if (i == 0) {
                        if (syntaxUnitInSyntaxContainer.getValue().contains("+")) {
                            syntaxUnitInSyntaxContainer.setValue(syntaxUnitInSyntaxContainer.getValue().replace("+", ""));
                        }
                        syntaxUnits.set(i, syntaxUnitInSyntaxContainer);
                    } else {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                        if (previousOperationAsSyntaxUnit instanceof Operation) {
                            switch (syntaxUnitInSyntaxContainer.getValue()) {
                                case String currentNumberValue when currentNumberValue.contains("+") -> {
                                    syntaxUnitInSyntaxContainer.setValue(syntaxUnitInSyntaxContainer.getValue().replace("+", ""));
                                    syntaxUnits.set(i, syntaxUnitInSyntaxContainer);
                                }
                                case String currentNumberValue when currentNumberValue.contains("-") -> {
                                    if (previousOperationAsSyntaxUnit.getValue().equals("+")) {
                                        syntaxUnits.set(i - 1, new Operation(0, "-"));
                                        syntaxUnitInSyntaxContainer.setValue(syntaxUnitInSyntaxContainer.getValue().replace("-", ""));
                                        syntaxUnits.set(i, syntaxUnitInSyntaxContainer);
                                    } else if (previousOperationAsSyntaxUnit.getValue().equals("-")) {
                                        syntaxUnits.set(i - 1, new Operation(0, "+"));
                                        syntaxUnitInSyntaxContainer.setValue(syntaxUnitInSyntaxContainer.getValue().replace("-", ""));
                                        syntaxUnits.set(i, syntaxUnitInSyntaxContainer);
                                    }
                                }
                                default ->
                                        throw new IllegalStateException("Unexpected value: " + syntaxUnitInSyntaxContainer.getValue());
                            }
                        }
                    }
                } else {
                    if (syntaxContainer.getSyntaxUnits().size() == 2) {
                        SyntaxUnit firstSyntaxUnit = syntaxContainer.getSyntaxUnits().getFirst();
                        SyntaxUnit secondSyntaxUnit = syntaxContainer.getSyntaxUnits().getLast();
                        if (firstSyntaxUnit instanceof Operation syntaxUnitAsOperation && syntaxUnitAsOperation.getValue().matches("[+\\-]")) {
                            if (i == 0) {
                                if (syntaxUnitAsOperation.getValue().equals("+")) {
                                    syntaxUnits.set(i, secondSyntaxUnit);
                                } else {
                                    syntaxUnits.set(i, syntaxUnitAsOperation);
                                    syntaxUnits.addFirst(firstSyntaxUnit);
                                }
                            } else {
                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                if (syntaxUnitAsOperation.getValue().equals("+")) {
                                    syntaxUnits.set(i, secondSyntaxUnit);
                                } else if (syntaxUnitAsOperation.getValue().equals("-")) {
                                    if (previousOperationAsSyntaxUnit.getValue().equals("+")) {
                                        previousOperationAsSyntaxUnit.setValue("-");
                                        syntaxUnits.set(i, secondSyntaxUnit);
                                    } else if (previousOperationAsSyntaxUnit.getValue().equals("-")) {
                                        previousOperationAsSyntaxUnit.setValue("+");
                                        syntaxUnits.set(i, secondSyntaxUnit);
                                    }
                                }
                            }
                        }
                    } else {
                        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnit.getSyntaxUnits());
                    }
                }
            } else {
                removeUnnecessaryBracketsInLogicalBlocks(syntaxUnit.getSyntaxUnits());
            }
        }
    }

    public static void processMultiplicationByNegativeOne(List<SyntaxUnit> syntaxUnits) throws Exception {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);

            if (syntaxUnit instanceof Variable) {
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 2);

                    if (nextOperationAsSyntaxUnit instanceof Operation && nextOperationAsSyntaxUnit.getValue().matches("[*/]") && nextSyntaxUnit instanceof LogicalBlock && nextSyntaxUnit.getSyntaxUnits().size() == 1) {
                        SyntaxUnit syntaxUnitInsideLogicalBlock = nextSyntaxUnit.getSyntaxUnits().getFirst();
                        if (syntaxUnitInsideLogicalBlock instanceof org.pzks.units.Number && syntaxUnitInsideLogicalBlock.getValue().matches("-1|-1\\.0")) {
                            if (i == 0) {
                                syntaxUnits.subList(i + 1, i + 3).clear();
                                syntaxUnits.addFirst(new Operation(0, "-"));
                            } else {
                                SyntaxUnit previousOperationsAsSyntaxUnit = syntaxUnits.get(i - 1);
                                switch (previousOperationsAsSyntaxUnit.getValue()) {
                                    case String previousOperationValue when previousOperationValue.equals("+") -> {
                                        previousOperationsAsSyntaxUnit.setValue("-");
                                        syntaxUnits.subList(i + 1, i + 3).clear();
                                    }
                                    case String previousOperationValue when previousOperationValue.equals("-") -> {
                                        previousOperationsAsSyntaxUnit.setValue("+");
                                        syntaxUnits.subList(i + 1, i + 3).clear();
                                    }
                                    case String previousOperationValue when previousOperationValue.matches("[*/]") -> {
                                        syntaxUnits.subList(i + 1, i + 3).clear();
                                        List<SyntaxUnit> syntaxUnitsToAddToNewLogicalBlock = new ArrayList<>();
                                        syntaxUnitsToAddToNewLogicalBlock.add(new Operation(0, "-"));
                                        syntaxUnitsToAddToNewLogicalBlock.add(syntaxUnit);
                                        LogicalBlock logicalBlock = new LogicalBlock(0, new ArrayList<>());
                                        logicalBlock.getDetails().put("openingBracket", "(");
                                        logicalBlock.getDetails().put("closingBracket", ")");
                                        logicalBlock.setSyntaxUnits(syntaxUnitsToAddToNewLogicalBlock);
                                        syntaxUnits.set(i, logicalBlock);
                                    }
                                    default -> throw new IllegalStateException("Unexpected value: " + previousOperationsAsSyntaxUnit.getValue());
                                }
                            }
                        }
                    }
                }
            } else if (syntaxUnit instanceof SyntaxContainer) {
                processMultiplicationByNegativeOne(syntaxUnit.getSyntaxUnits());
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 2);

                    if (nextOperationAsSyntaxUnit instanceof Operation && nextOperationAsSyntaxUnit.getValue().matches("[*/]") && nextSyntaxUnit instanceof LogicalBlock && nextSyntaxUnit.getSyntaxUnits().size() == 1) {
                        SyntaxUnit syntaxUnitInsideLogicalBlock = nextSyntaxUnit.getSyntaxUnits().getFirst();
                        if (syntaxUnitInsideLogicalBlock instanceof Number && syntaxUnitInsideLogicalBlock.getValue().matches("-1|-1\\.0")) {
                            if (i == 0) {
                                syntaxUnits.subList(i + 1, i + 3).clear();
                                syntaxUnits.addFirst(new Operation(0, "-"));
                            } else {
                                SyntaxUnit previousOperationsAsSyntaxUnit = syntaxUnits.get(i - 1);
                                switch (previousOperationsAsSyntaxUnit.getValue()) {
                                    case String previousOperationValue when previousOperationValue.equals("+") -> {
                                        previousOperationsAsSyntaxUnit.setValue("-");
                                        syntaxUnits.subList(i + 1, i + 3).clear();
                                    }
                                    case String previousOperationValue when previousOperationValue.equals("-") -> {
                                        previousOperationsAsSyntaxUnit.setValue("+");
                                        syntaxUnits.subList(i + 1, i + 3).clear();
                                    }
                                    case String previousOperationValue when previousOperationValue.matches("[*/]") -> {
                                        syntaxUnits.subList(i + 1, i + 3).clear();
                                        List<SyntaxUnit> syntaxUnitsToAddToNewLogicalBlock = new ArrayList<>();
                                        syntaxUnitsToAddToNewLogicalBlock.add(new Operation(0, "-"));
                                        syntaxUnitsToAddToNewLogicalBlock.add(syntaxUnit);
                                        LogicalBlock logicalBlock = new LogicalBlock(0, new ArrayList<>());
                                        logicalBlock.getDetails().put("openingBracket", "(");
                                        logicalBlock.getDetails().put("closingBracket", ")");
                                        logicalBlock.setSyntaxUnits(syntaxUnitsToAddToNewLogicalBlock);
                                        syntaxUnits.set(i, logicalBlock);
                                    }
                                    default -> throw new IllegalStateException("Unexpected value: " + previousOperationsAsSyntaxUnit.getValue());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
