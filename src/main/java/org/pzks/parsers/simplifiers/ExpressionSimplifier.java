package org.pzks.parsers.simplifiers;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.ArithmeticUtils;

import java.util.List;

public class ExpressionSimplifier {
    private final List<SyntaxUnit> syntaxUnits;

    public ExpressionSimplifier(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        String providedExpression;
        String simplifiedExpression;
        do {
            providedExpression = ExpressionParser.getExpressionAsString(syntaxUnits);
            simplify();
            simplifiedExpression = ExpressionParser.getExpressionAsString(syntaxUnits);
        } while (!simplifiedExpression.equals(providedExpression));
    }

    public ExpressionSimplifier(List<SyntaxUnit> syntaxUnits) throws Exception {
        this.syntaxUnits = syntaxUnits;
        String providedExpression;
        String simplifiedExpression;
        do {
            providedExpression = ExpressionParser.getExpressionAsString(syntaxUnits);
            simplify();
            simplifiedExpression = ExpressionParser.getExpressionAsString(syntaxUnits);
        } while (!simplifiedExpression.equals(providedExpression));

    }

    public SyntaxUnit getSimplifiedSyntaxUnit() throws Exception {
        return ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits));
    }

    private void simplify() throws Exception {
        combineAdjacentSyntaxUnits(syntaxUnits);
        String simplifiedExpression = ExpressionParser.getExpressionAsString(this.syntaxUnits);
        simplifiedExpression = new BasicExpressionSimplifier(simplifiedExpression)
                .removeUnnecessaryZerosAfterDotInNumbers()
                .removeOuterBracketsForRootExpression()
                .getExpression();
        SyntaxUnit simplifiedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(simplifiedExpression);
        this.syntaxUnits.clear();
        this.syntaxUnits.addAll(simplifiedSyntaxUnit.getSyntaxUnits());

        combineSameAdjacentVarNumUnits(syntaxUnits);
        simplifiedExpression = ExpressionParser.getExpressionAsString(this.syntaxUnits);
        simplifiedExpression = new BasicExpressionSimplifier(simplifiedExpression)
                .removeUnnecessaryZerosAfterDotInNumbers()
                .removeOuterBracketsForRootExpression()
                .getExpression();
        simplifiedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(simplifiedExpression);
        this.syntaxUnits.clear();
        this.syntaxUnits.addAll(simplifiedSyntaxUnit.getSyntaxUnits());
    }

    private void combineSameAdjacentVarNumUnits(List<SyntaxUnit> syntaxUnits) throws Exception {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (currentSyntaxUnit instanceof Variable) {
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit operationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 2);

                    if (nextSyntaxUnit instanceof Variable && currentSyntaxUnit.getValue().equals(nextSyntaxUnit.getValue())) {

                        switch (operationAsSyntaxUnit.getValue()) {
                            case "-" -> {
                                if (i - 2 >= 0 && i + 4 < syntaxUnits.size()) {
                                    SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                    SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(i + 3);
                                    if (previousOperationAsSyntaxUnit.getValue().equals("+") && nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                        previousOperationAsSyntaxUnit.setValue("+");
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "0"));
                                    }
                                } else if (i - 2 >= 0) {
                                    SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                    if (previousOperationAsSyntaxUnit.getValue().equals("+")) {
                                        previousOperationAsSyntaxUnit.setValue("+");
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "0"));
                                    }
                                } else if (i + 4 < syntaxUnits.size()) {
                                    SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(i + 3);
                                    if (nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "0"));
                                    }
                                } else {
                                    syntaxUnits.subList(i, i + 3).clear();
                                    syntaxUnits.add(i, new Number(0, "0"));
                                }
                            }
                            case "+" -> {
                                if (i - 2 >= 0 && i + 4 < syntaxUnits.size()) {
                                    SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                    SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(i + 3);
                                    if (previousOperationAsSyntaxUnit.getValue().equals("-") && nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "0"));
                                    }
                                } else if (i - 2 >= 0) {
                                    SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                    if (previousOperationAsSyntaxUnit.getValue().equals("-")) {
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "0"));
                                    }
                                } else if (i + 4 < syntaxUnits.size()) {
                                    SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(i + 3);
                                    if (nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "0"));
                                    }
                                } else {
                                    syntaxUnits.subList(i, i + 3).clear();
                                    syntaxUnits.add(i, new Number(0, "0"));
                                }
                            }
                            case "/" -> {
                                if (i - 2 >= 0) {
                                    SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                    if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-*]")) {
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "1"));
                                    }
                                } else {
                                    syntaxUnits.subList(i, i + 3).clear();
                                    syntaxUnits.add(i, new Number(0, "1"));
                                }
                            }
                            case "*" -> {
                                if (i - 2 >= 0) {
                                    SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                    if (previousOperationAsSyntaxUnit.getValue().equals("/")) {
                                        syntaxUnits.subList(i, i + 3).clear();
                                        syntaxUnits.add(i, new Number(0, "1"));
                                        previousOperationAsSyntaxUnit.setValue("*");
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (currentSyntaxUnit instanceof SyntaxContainer) {
                combineSameAdjacentVarNumUnits(currentSyntaxUnit.getSyntaxUnits());
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
            } else {
                simplifyMultiplicationOrDivisionOnLogicalBlockIfPossible(syntaxUnits, i);
            }
        }

        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);
        simplifySimpleUnits(syntaxUnits);
        SyntaxUnitsBasicSimplifier.processMultiplicationByNegativeOne(syntaxUnits);
    }

    private void simplifyMultiplicationOrDivisionOnLogicalBlockIfPossible(List<SyntaxUnit> syntaxUnits, int currentIndex) {
        SyntaxUnit currentSyntaxUnit = syntaxUnits.get(currentIndex);
        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndex + 1);
        if (nextSyntaxUnit instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 3) {
            SyntaxUnit firstSyntaxUnitInLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
            SyntaxUnit middleSyntaxUnitInLogicalBlock = logicalBlock.getSyntaxUnits().get(1);
            SyntaxUnit lastSyntaxUnitInLogicalBlock = logicalBlock.getSyntaxUnits().getLast();

            if (firstSyntaxUnitInLogicalBlock.getValue().equals("1") &&
                    middleSyntaxUnitInLogicalBlock.getValue().equals("/")) {
                if (currentSyntaxUnit.getValue().equals("*")) {
                    currentSyntaxUnit.setValue("/");
                    syntaxUnits.set(currentIndex + 1, lastSyntaxUnitInLogicalBlock);
                } else if (currentSyntaxUnit.getValue().equals("/")) {
                    currentSyntaxUnit.setValue("*");
                    syntaxUnits.set(currentIndex + 1, lastSyntaxUnitInLogicalBlock);
                }
            }
        }
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
                            if (previousOperationAsSyntaxUnit.getValue().equals("-")) {
                                previousOperationAsSyntaxUnit.setValue("+");
                                currentNumberAsSyntaxUnit.setValue("-" + currentNumber);
                                currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());
                            }
                            double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);

                            if (result < 0) {
                                previousOperationAsSyntaxUnit.setValue("-");
                            }
                            result = Math.abs(result);

                            syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                            syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, ArithmeticUtils.convertDoubleToString(result)));
                            currentIndexInSyntaxUnits--;
                        }
                    } else if (currentIndexInSyntaxUnits - 2 >= 0) {
                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                        if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
                            if (previousOperationAsSyntaxUnit.getValue().equals("-")) {
                                previousOperationAsSyntaxUnit.setValue("+");
                                currentNumberAsSyntaxUnit.setValue("-" + currentNumber);
                                currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());
                            }
                            double result = ArithmeticUtils.calculateResult(operationValue, currentNumber, nextNumber);

                            if (result < 0) {
                                previousOperationAsSyntaxUnit.setValue("-");
                            }

                            result = Math.abs(result);

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
        String expression = ExpressionParser.getExpressionAsString(syntaxUnits);

        BasicExpressionSimplifier basicExpressionSimplifier = new BasicExpressionSimplifier(expression);
        expression = basicExpressionSimplifier
                .removeUnnecessaryZerosAfterDotInNumbers()
                .simplifyOnes()
                .simplifyZeros()
                .removePlusAtTheBeginningOfTheLogicalBlockOrExpression()
                .getExpression();

        SyntaxUnit syntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(expression);
        syntaxUnits.clear();
        syntaxUnits.addAll(syntaxUnit.getSyntaxUnits());
    }
}
