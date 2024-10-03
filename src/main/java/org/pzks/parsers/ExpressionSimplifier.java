package org.pzks.parsers;

import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.SyntaxUnitStructurePrinter;

import java.util.ArrayList;
import java.util.List;

public class ExpressionSimplifier {
    private final List<SyntaxUnit> syntaxUnits;

    public ExpressionSimplifier(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
    }

    public List<SyntaxUnit> getSyntaxUnits() {
        return syntaxUnits;
    }

    public void simplify() throws Exception {
        combineAdjacentSyntaxUnits(syntaxUnits);
    }

    private void processMultiplicationByNegativeOne(List<SyntaxUnit> syntaxUnits) throws Exception {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);

            if (syntaxUnit instanceof Variable) {
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

    private void removeUnnecessaryBracketsInLogicalBlocks(List<SyntaxUnit> syntaxUnits) {
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

    private void combineAdjacentSyntaxUnits(List<SyntaxUnit> syntaxUnits) throws Exception {
        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);
        simplifySimpleUnits(syntaxUnits);

        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (currentSyntaxUnit instanceof Number) {
                i = combineAdjacentSyntaxUnitsIfCurrentUnitIsNumber(i, syntaxUnits);
            } else if (currentSyntaxUnit instanceof SyntaxContainer) {
                i = combineAdjacentSyntaxUnitsIfCurrentUnitIsSyntaxContainer(i, syntaxUnits);
            } else if (!(currentSyntaxUnit instanceof Operation)) {
                boolean decrementI = false;
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 2);
                    if (nextOperationAsSyntaxUnit instanceof Operation operation && nextSyntaxUnit instanceof Number nextNumberAsSyntaxUnit) {
                        double nextNumber = Double.parseDouble(nextNumberAsSyntaxUnit.getValue());
                        if (nextNumber == 0 && operation.getValue().equals("*")) {
                            if (i - 2 >= 0) {
                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                if (previousOperationAsSyntaxUnit.getValue().matches("[*+\\-]")) {
                                    syntaxUnits.subList(i, i + 2).clear();
                                    decrementI = true;
                                }
                            } else {
                                syntaxUnits.subList(i, i + 2).clear();
                                decrementI = true;
                            }
                        }
                    }
                }
                if (decrementI) {
                    i--;
                }
            }
        }

        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);
        simplifySimpleUnits(syntaxUnits);
        processMultiplicationByNegativeOne(syntaxUnits);
    }

    // combine if current syntax unit is Number
    private int combineAdjacentSyntaxUnitsIfCurrentUnitIsNumber(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) throws Exception {
        if (currentIndexInSyntaxUnits + 2 < syntaxUnits.size()) {
            SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
            if (nextSyntaxUnit instanceof Number) {
                currentIndexInSyntaxUnits = combineNumbers(currentIndexInSyntaxUnits, syntaxUnits);
            } else if (nextSyntaxUnit instanceof SyntaxContainer) {
                currentIndexInSyntaxUnits = combineNumberWithContainer(currentIndexInSyntaxUnits, syntaxUnits);
            } else {
                currentIndexInSyntaxUnits = combineNumberWithSyntaxUnit(currentIndexInSyntaxUnits, syntaxUnits);
            }
        }

        return currentIndexInSyntaxUnits;
    }

    private int combineNumbers(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) {
        SyntaxUnit currentNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits);
        double currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());

        SyntaxUnit nextNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);

        double nextNumber = Double.parseDouble(nextNumberAsSyntaxUnit.getValue());
        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
        if (nextOperationAsSyntaxUnit instanceof Operation operation) {
            switch (operation.getValue()) {
                case String operationValue when operationValue.matches("[+\\-]") -> {
                    if (currentIndexInSyntaxUnits - 2 >= 0 && currentIndexInSyntaxUnits + 4 < syntaxUnits.size()) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 3);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]") && nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                            double result = calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else if (currentIndexInSyntaxUnits - 2 >= 0) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
                            double result = calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else if (currentIndexInSyntaxUnits + 4 < syntaxUnits.size()) {
                        SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 3);
                        if (nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                            double result = calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else {
                        double result = calculateResult(operationValue, currentNumber, nextNumber);
                        syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                        syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                        currentIndexInSyntaxUnits--;
                    }
                }
                case String operationValue when operationValue.equals("*") -> {
                    if (currentIndexInSyntaxUnits - 2 >= 0) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-*]")) {
                            double result = calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else {
                        double result = calculateResult(operationValue, currentNumber, nextNumber);
                        syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                        syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                        currentIndexInSyntaxUnits--;
                    }
                }
                case String operationValue when operationValue.equals("/") -> {
                    if (currentIndexInSyntaxUnits - 2 >= 0) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]") && nextNumber != 0) {
                            double result = calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else if (nextNumber != 0) {
                        double result = calculateResult(operationValue, currentNumber, nextNumber);
                        syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                        syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToString(result)));
                        currentIndexInSyntaxUnits--;
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + operation.getValue());
            }
        }

        return currentIndexInSyntaxUnits;
    }

    private int combineNumberWithContainer(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) throws Exception {
        SyntaxUnit syntaxContainer = syntaxUnits.get(currentIndexInSyntaxUnits + 2);

        if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
            SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
            syntaxUnits.set(currentIndexInSyntaxUnits + 2, syntaxUnitInsideLogicalBlock);
            currentIndexInSyntaxUnits--;
        } else {
            boolean decrementCurrentIndexInSyntaxUnits = false;
            combineAdjacentSyntaxUnits(syntaxContainer.getSyntaxUnits());
            if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                syntaxUnits.set(currentIndexInSyntaxUnits + 2, syntaxUnitInsideLogicalBlock);
                decrementCurrentIndexInSyntaxUnits = true;
            }
            currentIndexInSyntaxUnits = combineZeroNumberWithSyntaxContainerOrSyntaxUnit(currentIndexInSyntaxUnits, syntaxUnits, decrementCurrentIndexInSyntaxUnits);
        }
        return currentIndexInSyntaxUnits;
    }

    private int combineNumberWithSyntaxUnit(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) {
        boolean decrementCurrentIndexInSyntaxUnits = false;
        currentIndexInSyntaxUnits = combineZeroNumberWithSyntaxContainerOrSyntaxUnit(currentIndexInSyntaxUnits, syntaxUnits, decrementCurrentIndexInSyntaxUnits);
        return currentIndexInSyntaxUnits;
    }

    private int combineZeroNumberWithSyntaxContainerOrSyntaxUnit(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits, boolean decrementCurrentIndexInSyntaxUnits) {
        SyntaxUnit currentNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits);
        double currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());

        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);

        SyntaxUnit nextSyntaxUnit;

        if (nextOperationAsSyntaxUnit instanceof Operation operation) {
            if (currentNumber == 0) {
                switch (operation.getValue()) {
                    case String operationValue when operationValue.matches("[*/]") -> {
                        nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
                        nextSyntaxUnit.analyzeArithmeticErrors();
                        if (nextSyntaxUnit.getArithmeticErrors().isEmpty()) {
                            if (!(operation.getValue().equals("/") && nextSyntaxUnit.getValue().matches("0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0"))) {
                                if (currentIndexInSyntaxUnits - 2 >= 0) {
                                    SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                                    if (previousOperationAsSyntaxUnit.getValue().matches("[*+\\-]")) {
                                        syntaxUnits.subList(currentIndexInSyntaxUnits + 1, currentIndexInSyntaxUnits + 3).clear();
                                        decrementCurrentIndexInSyntaxUnits = true;
                                    }
                                } else {
                                    syntaxUnits.subList(currentIndexInSyntaxUnits + 1, currentIndexInSyntaxUnits + 3).clear();
                                    decrementCurrentIndexInSyntaxUnits = true;
                                }
                            }
                        }
                    }
                    case String operationValue when operationValue.equals("+") -> {
                        if (currentIndexInSyntaxUnits - 2 >= 0) {
                            SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                            if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                syntaxUnits.subList(currentIndexInSyntaxUnits - 1, currentIndexInSyntaxUnits + 1).clear();
                                decrementCurrentIndexInSyntaxUnits = true;
                            }
                        } else {
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 2).clear();
                            decrementCurrentIndexInSyntaxUnits = true;
                        }
                    }
                    case String operationValue when operationValue.equals("-") -> {
                        if (currentIndexInSyntaxUnits - 2 >= 0) {
                            SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                            if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                syntaxUnits.subList(currentIndexInSyntaxUnits - 1, currentIndexInSyntaxUnits + 1).clear();
                                decrementCurrentIndexInSyntaxUnits = true;
                            }
                        } else {
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 1).clear();
                            decrementCurrentIndexInSyntaxUnits = true;
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + operation.getValue());
                }
            }
        }

        if (decrementCurrentIndexInSyntaxUnits) {
            currentIndexInSyntaxUnits--;
        }

        return currentIndexInSyntaxUnits;
    }

    private int combineAdjacentSyntaxUnitsIfCurrentUnitIsSyntaxContainer(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) throws Exception {
        SyntaxUnit syntaxContainer = syntaxUnits.get(currentIndexInSyntaxUnits);

        if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
            SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
            if (syntaxUnitInsideLogicalBlock instanceof Number number && number.getValue().matches("[+\\-]\\d+(\\.\\d+)?")) {
                if (currentIndexInSyntaxUnits + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
                    if (nextSyntaxUnit instanceof Number && !(nextOperationAsSyntaxUnit.getValue().equals("/") && nextSyntaxUnit.getValue().matches("0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0"))) {
                        syntaxUnits.set(currentIndexInSyntaxUnits, syntaxUnitInsideLogicalBlock);
                        currentIndexInSyntaxUnits--;
                    } else if (nextSyntaxUnit instanceof LogicalBlock nextLogicalBlock && nextLogicalBlock.getSyntaxUnits().size() == 1) {
                        SyntaxUnit syntaxUnitInsideNextLogicalBlock = nextLogicalBlock.getSyntaxUnits().getFirst();
                        if (syntaxUnitInsideNextLogicalBlock instanceof Number && !(nextOperationAsSyntaxUnit.getValue().equals("/") && nextSyntaxUnit.getValue().matches("0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0"))) {
                            syntaxUnits.set(currentIndexInSyntaxUnits, syntaxUnitInsideLogicalBlock);
                            currentIndexInSyntaxUnits--;
                        }
                    }
                }
            } else {
                syntaxUnits.set(currentIndexInSyntaxUnits, syntaxUnitInsideLogicalBlock);
                currentIndexInSyntaxUnits--;
            }
        } else {
            boolean decrementI = false;
            combineAdjacentSyntaxUnits(syntaxContainer.getSyntaxUnits());
            if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                if (syntaxUnitInsideLogicalBlock instanceof Number number && number.getValue().matches("[+\\-]\\d+(\\.\\d+)?")) {
                    if (currentIndexInSyntaxUnits + 2 < syntaxUnits.size()) {
                        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
                        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
                        if (nextSyntaxUnit instanceof Number && !(nextOperationAsSyntaxUnit.getValue().equals("/") && nextSyntaxUnit.getValue().matches("0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0"))) {
                            syntaxUnits.set(currentIndexInSyntaxUnits, syntaxUnitInsideLogicalBlock);
                            currentIndexInSyntaxUnits--;
                        } else if (nextSyntaxUnit instanceof LogicalBlock nextLogicalBlock && nextLogicalBlock.getSyntaxUnits().size() == 1) {
                            SyntaxUnit syntaxUnitInsideNextLogicalBlock = nextLogicalBlock.getSyntaxUnits().getFirst();
                            if (syntaxUnitInsideNextLogicalBlock instanceof Number && !(nextOperationAsSyntaxUnit.getValue().equals("/") && nextSyntaxUnit.getValue().matches("0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0"))) {
                                syntaxUnits.set(currentIndexInSyntaxUnits, syntaxUnitInsideLogicalBlock);
                                currentIndexInSyntaxUnits--;
                            }
                        }
                    }
                } else {
                    syntaxUnits.set(currentIndexInSyntaxUnits, syntaxUnitInsideLogicalBlock);
                    currentIndexInSyntaxUnits--;
                }
            }
            if (currentIndexInSyntaxUnits + 2 < syntaxUnits.size()) {
                SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
                SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
                if (nextOperationAsSyntaxUnit instanceof Operation operation && nextSyntaxUnit instanceof Number nextNumberAsSyntaxUnit) {
                    double nextNumber = Double.parseDouble(nextNumberAsSyntaxUnit.getValue());
                    if (nextNumber == 0 && operation.getValue().equals("*")) {
                        syntaxContainer = syntaxUnits.get(currentIndexInSyntaxUnits);
                        syntaxContainer.analyzeArithmeticErrors();
                        if (syntaxContainer.getArithmeticErrors().isEmpty()) {
                            if (currentIndexInSyntaxUnits - 2 >= 0) {
                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                                if (previousOperationAsSyntaxUnit.getValue().matches("[*+\\-]")) {
                                    syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 2).clear();
                                    decrementI = true;
                                }
                            } else {
                                syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 2).clear();
                                decrementI = true;
                            }
                        }
                    }
                }
            }
            if (decrementI) {
                currentIndexInSyntaxUnits--;
            }
        }

        return currentIndexInSyntaxUnits;
    }

    // simple units simplifications
    private void simplifySimpleUnits(List<SyntaxUnit> syntaxUnits) throws Exception {
        String expression = SyntaxUnitStructurePrinter.getExpressionAsString(syntaxUnits);
        // operations with 1
        expression = expression.replaceAll("\\*(1|\\+1|1\\.1|\\+1\\.1)", "");         // *1
        expression = expression.replaceAll("/(1|\\+1|1\\.1|\\+1\\.1)", "");           // /1

        expression = expression.replaceAll("(?<=[+\\-*])(1|\\+1|1\\.1|\\+1\\.1)\\*", "");   // 1*
        expression = expression.replaceAll("^(1|\\+1|1\\.1|\\+1\\.1)\\*", "");

        expression = expression.replaceAll("(?<=[+\\-*])(-1|-1\\.1)\\*", "-");   // 1*
        expression = expression.replaceAll("^(-1|-1\\.1)\\*", "-");

        // operations with 0: 0 * or 0 /
        expression = expression.replaceAll("(?<=[+\\-*])(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+", "0");                   // [+-*]0*/variable or 0*number
        expression = expression.replaceAll("(?<=[+\\-*])(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\(\\)", "0");             // [+-*]0*/func()
        expression = expression.replaceAll("(?<=[+\\-*])(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\((\\w+,)*\\w+\\)", "0"); // [+-*]0*/func(5) or 0*/func(a) or 0*/func(a,4,b)

        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+", "0");
        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\(\\)", "0");
        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?!/0\\.?0*)[*/]\\w+\\((\\w+,)*\\w+\\)", "0");


        // operations with 0: * 0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");                    // [+-*]variable*0 or number*0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\(\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");              // [+-*]func()*0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\((\\w+,)*\\w+\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");  // [+-*]func(5)*0 or func(a)*0 or func(a,4,b)*0

        expression = expression.replaceAll("^\\w+\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");
        expression = expression.replaceAll("^\\w+\\(\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");
        expression = expression.replaceAll("^\\w+\\((\\w+,)*\\w+\\)\\*(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)", "0");

        // just zero with + or -
        expression = expression.replaceAll("^(-0|\\+0|-0\\.0|\\+0\\.0)$", "0");

        // operations with 0: 0+ or 0- or +0 or -0
        expression = expression.replaceAll("[+\\-](0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)(?=[+\\-])", "");
        expression = expression.replaceAll("^(0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)[+\\-]", "");
        expression = expression.replaceAll("[+\\-](0|-0|\\+0|0\\.0|-0\\.0|\\+0\\.0)$", "");

        SyntaxUnit syntaxUnit = new ExpressionParser().convertExpressionToParsedSyntaxUnit(expression);
        syntaxUnits.clear();
        syntaxUnits.addAll(syntaxUnit.getSyntaxUnits());
    }


    // helping methods
    private double calculateResult(String operation, double currentNumber, double nextNumber) {
        return switch (operation) {
            case "+" -> currentNumber + nextNumber;
            case "-" -> currentNumber - nextNumber;
            case "*" -> currentNumber * nextNumber;
            case "/" -> currentNumber / nextNumber;
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
    }

    private String convertDoubleToString(double number) {
        return String.valueOf(number);
    }
}
