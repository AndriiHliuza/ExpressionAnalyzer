package org.pzks.parsers.simplifiers;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.ArithmeticUtils;
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
        SyntaxUnitsBasicSimplifier.processMultiplicationByNegativeOne(syntaxUnits);
    }

    private void removeUnnecessaryBracketsInLogicalBlocks(List<SyntaxUnit> syntaxUnits) {
        new SyntaxUnitsBasicSimplifier(this.syntaxUnits).removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);
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
                            double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else if (currentIndexInSyntaxUnits - 2 >= 0) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
                            double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else if (currentIndexInSyntaxUnits + 4 < syntaxUnits.size()) {
                        SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 3);
                        if (nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                            double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else {
                        double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                        syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                        syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                        currentIndexInSyntaxUnits--;
                    }
                }
                case String operationValue when operationValue.equals("*") -> {
                    if (currentIndexInSyntaxUnits - 2 >= 0) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-*]")) {
                            double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else {
                        double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                        syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                        syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                        currentIndexInSyntaxUnits--;
                    }
                }
                case String operationValue when operationValue.equals("/") -> {
                    if (currentIndexInSyntaxUnits - 2 >= 0) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]") && nextNumber != 0) {
                            double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else if (nextNumber != 0) {
                        double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);
                        syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                        syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
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

        BasicExpressionSimplifier basicExpressionSimplifier = new BasicExpressionSimplifier(expression);
        expression = basicExpressionSimplifier.simplifyOnes().simplifyZeros().getExpression();

        SyntaxUnit syntaxUnit = new ExpressionParser().convertExpressionToParsedSyntaxUnit(expression);
        syntaxUnits.clear();
        syntaxUnits.addAll(syntaxUnit.getSyntaxUnits());
    }
}
