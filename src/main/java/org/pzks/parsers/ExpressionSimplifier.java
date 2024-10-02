package org.pzks.parsers;

import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.SyntaxUnitStructurePrinter;

import java.text.DecimalFormat;
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
//        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);

        combineAdjacentSyntaxUnits(syntaxUnits);
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
                    if (!((syntaxUnitInSyntaxContainer instanceof Number number && number.getValue().contains("-")))) {
                        syntaxUnits.set(i, syntaxUnitInSyntaxContainer);
                    }
                } else {
                    removeUnnecessaryBracketsInLogicalBlocks(syntaxUnit.getSyntaxUnits());
                }
            } else {
                removeUnnecessaryBracketsInLogicalBlocks(syntaxUnit.getSyntaxUnits());
            }
        }
    }

    private void combineAdjacentSyntaxUnits2(List<SyntaxUnit> syntaxUnits) throws Exception {
        syntaxUnits = simplifySimpleUnits(syntaxUnits);

        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (!(currentSyntaxUnit instanceof Operation)) {
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    i = switch (nextOperationAsSyntaxUnit.getValue()) {
                        case String operationValue when operationValue.equals("+") ->
                                processPlusOperationWithSyntaxUnits(i, syntaxUnits);
                        case String operationValue when operationValue.equals("-") ->
                                processMinusOperationWithSyntaxUnits(i, syntaxUnits);
                        case String operationValue when operationValue.equals("*") ->
                                processMultiplicationOperationWithSyntaxUnits(i, syntaxUnits);
                        case String operationValue when operationValue.equals("/") ->
                                processDivisionOperationWithSyntaxUnits(i, syntaxUnits);
                        default ->
                                throw new IllegalStateException("Unexpected operation: " + nextOperationAsSyntaxUnit.getValue());
                    };
                } else {
                    //TODO maybe implement functionality
                }
            }
        }
    }

    private void combineAdjacentSyntaxUnits(List<SyntaxUnit> syntaxUnits) throws Exception {
        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);
        syntaxUnits = simplifySimpleUnits(syntaxUnits);

        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (currentSyntaxUnit instanceof Number) {
                i = calculateCombinedNumber(i, syntaxUnits);
            } else if (currentSyntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                    syntaxUnits.set(i, syntaxUnitInsideLogicalBlock);
                    i--;
                } else {
                    boolean decrementI = false;
                    combineAdjacentSyntaxUnits(syntaxContainer.getSyntaxUnits());
                    if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                        SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                        syntaxUnits.set(i, syntaxUnitInsideLogicalBlock);
                        decrementI = true;
                    }
                    if (i + 2 < syntaxUnits.size()) {
                        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 2);
                        if (nextOperationAsSyntaxUnit instanceof Operation operation && nextSyntaxUnit instanceof Number nextNumberAsSyntaxUnit) {
                            double nextNumber = Double.parseDouble(nextNumberAsSyntaxUnit.getValue());
                            if (nextNumber == 0 && operation.getValue().equals("*")) {
                                syntaxContainer.analyzeArithmeticErrors();
                                if (syntaxContainer.getArithmeticErrors().isEmpty()) {
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
                    }
                    if (decrementI) {
                        i--;
                    }
                }
            } else if (!(currentSyntaxUnit instanceof Operation)) {
                boolean decrementI = false;
                if (i + 2 < syntaxUnits.size()) {
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(i + 1);
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 2);
                    if (nextOperationAsSyntaxUnit instanceof Operation operation && nextSyntaxUnit instanceof Number nextNumberAsSyntaxUnit) {
                        double nextNumber = Double.parseDouble(nextNumberAsSyntaxUnit.getValue());
                        if (nextNumber == 0 && operation.getValue().matches("[*/]")) {
                            if (i - 2 >= 0) {
                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(i - 1);
                                if (previousOperationAsSyntaxUnit.getValue().matches("[*+\\-]")) {
                                    syntaxUnits.subList(i + 1, i + 3).clear();
                                    decrementI = true;
                                }
                            } else {
                                syntaxUnits.subList(i + 1, i + 3).clear();
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
    }

    private int calculateCombinedNumber(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) throws Exception {
        SyntaxUnit currentNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits);
        double currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());

        if (currentIndexInSyntaxUnits + 2 < syntaxUnits.size()) {
            SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
            if (nextSyntaxUnit instanceof Number nextNumberAsSyntaxUnit) {
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
                                    syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                    currentIndexInSyntaxUnits--;
                                }
                            } else if (currentIndexInSyntaxUnits - 2 >= 0) {
                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                                if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                    double result = calculateResult(operationValue, currentNumber, nextNumber);
                                    syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                                    syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                    currentIndexInSyntaxUnits--;
                                }
                            } else if (currentIndexInSyntaxUnits + 4 < syntaxUnits.size()) {
                                SyntaxUnit nextOperationAfterNextNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 3);
                                if (nextOperationAfterNextNumberAsSyntaxUnit.getValue().matches("[+\\-]")) {
                                    double result = calculateResult(operationValue, currentNumber, nextNumber);
                                    syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                                    syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                    currentIndexInSyntaxUnits--;
                                }
                            } else {
                                double result = calculateResult(operationValue, currentNumber, nextNumber);
                                syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                                syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                currentIndexInSyntaxUnits--;
                            }
                        }
                        case String operationValue when operationValue.equals("*") -> {
                            if (currentIndexInSyntaxUnits - 2 >= 0) {
                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                                if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-*]")) {
                                    double result = calculateResult(operationValue, currentNumber, nextNumber);
                                    syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                                    syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                    currentIndexInSyntaxUnits--;
                                }
                            } else {
                                double result = calculateResult(operationValue, currentNumber, nextNumber);
                                syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                                syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                currentIndexInSyntaxUnits--;
                            }
                        }
                        case String operationValue when operationValue.equals("/") -> {
                            if (currentIndexInSyntaxUnits - 2 >= 0) {
                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                                if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]") && nextNumber != 0) {
                                    double result = calculateResult(operationValue, currentNumber, nextNumber);
                                    syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                                    syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                    currentIndexInSyntaxUnits--;
                                }
                            } else if (nextNumber != 0) {
                                double result = calculateResult(operationValue, currentNumber, nextNumber);
                                syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 3).clear();
                                syntaxUnits.add(currentIndexInSyntaxUnits, new Number(0, convertDoubleToStringWithRounding(result)));
                                currentIndexInSyntaxUnits--;
                            }
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + operation.getValue());
                    }
                }
            } else if (nextSyntaxUnit instanceof SyntaxContainer syntaxContainer) {
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
                    SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
                    if (nextOperationAsSyntaxUnit instanceof Operation operation) {
                        if (currentNumber == 0 && operation.getValue().matches("[*/]")) {
                            nextSyntaxUnit.analyzeArithmeticErrors();
                            if (nextSyntaxUnit.getArithmeticErrors().isEmpty()) {
                                nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
                                if (!(operation.getValue().equals("/") && nextSyntaxUnit.getValue().matches("0|\\+0|-0"))) {
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
                    }

                    if (decrementCurrentIndexInSyntaxUnits) {
                        currentIndexInSyntaxUnits--;
                    }
                }
            } else {
                SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
                if (nextOperationAsSyntaxUnit instanceof Operation operation) {
                    if (currentNumber == 0 && operation.getValue().matches("[*/]")) {
                        if (currentIndexInSyntaxUnits - 2 >= 0) {
                            SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
                            if (previousOperationAsSyntaxUnit.getValue().matches("[*+\\-]")) {
                                syntaxUnits.subList(currentIndexInSyntaxUnits + 1, currentIndexInSyntaxUnits + 3).clear();
                                currentIndexInSyntaxUnits--;
                            }
                        } else {
                            syntaxUnits.subList(currentIndexInSyntaxUnits + 1, currentIndexInSyntaxUnits + 3).clear();
                            currentIndexInSyntaxUnits--;
                        }
                    }
                }
            }
        }

        return currentIndexInSyntaxUnits;
    }

    private List<SyntaxUnit> simplifySimpleUnits(List<SyntaxUnit> syntaxUnits) throws Exception {
        String expression = SyntaxUnitStructurePrinter.getExpressionAsString(syntaxUnits);
        // operations with 1
        expression = expression.replaceAll("(?<=[+\\-*])1\\*", "");   // 1*
        expression = expression.replaceAll("\\*1", "");         // *1
        expression = expression.replaceAll("/1", "");           // /1


        // operations with 0: 0 * or 0 /
        expression = expression.replaceAll("(?<=[+\\-*])0[*/]\\w+(?=[+\\-*/])", "0");                   // [+-*]0*variable or 0*number
        expression = expression.replaceAll("(?<=[+\\-*])0[*/]\\w+\\(\\)(?=[+\\-*/])", "0");             // [+-*]0*func()
        expression = expression.replaceAll("(?<=[+\\-*])0[*/]\\w+\\((\\w+,)*\\w+\\)(?=[+\\-*/])", "0"); // [+-*]0*func(5) or 0*func(a) or 0*func(a,4,b)

        // operations with 0: * 0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\*0(?=[+\\-*/])", "0");                    // [+-*]variable*0 or number*0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\(\\)\\*0(?=[+\\-*/])", "0");              // [+-*]func()*0
        expression = expression.replaceAll("(?<=[+\\-*])\\w+\\((\\w+,)*\\w+\\)\\*0(?=[+\\-*/])", "0");  // [+-*]func(5)*0 or func(a)*0 or func(a,4,b)*0

        // operations with 0: 0+ or 0- or +0 or -0
        expression = expression.replaceAll("[+\\-]0(?=[+\\-])", "");

        SyntaxUnit syntaxUnit = new ExpressionParser().convertExpressionToParsedSyntaxUnit(expression);
        syntaxUnits.clear();
        syntaxUnits.addAll(syntaxUnit.getSyntaxUnits());
        return syntaxUnits;
    }


    // +
    private int processPlusOperationWithSyntaxUnits(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) throws Exception {
        if (currentSyntaxUnitIndexInSyntaxUnitsList - 2 >= 0) {
            SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList - 1);

            if (!(previousOperationAsSyntaxUnit.getValue().matches("[*/]"))) {
                currentSyntaxUnitIndexInSyntaxUnitsList = processPlusOrMinusOperationsBasedOnNextOperationAsSyntaxUnit(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
            }
        } else {
            currentSyntaxUnitIndexInSyntaxUnitsList = processPlusOrMinusOperationsBasedOnNextOperationAsSyntaxUnit(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
        }

        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    // -
    private int processMinusOperationWithSyntaxUnits(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) throws Exception {
        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 1);

        if (currentSyntaxUnitIndexInSyntaxUnitsList - 2 >= 0) {
            SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList - 1);

            if (!(previousOperationAsSyntaxUnit.getValue().matches("[*/]") ||
                    (previousOperationAsSyntaxUnit.getValue().equals("-") && nextOperationAsSyntaxUnit.getValue().equals("-")))) {
                currentSyntaxUnitIndexInSyntaxUnitsList = processPlusOrMinusOperationsBasedOnNextOperationAsSyntaxUnit(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
            }
        } else {
            currentSyntaxUnitIndexInSyntaxUnitsList = processPlusOrMinusOperationsBasedOnNextOperationAsSyntaxUnit(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
        }

        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    private int processPlusOrMinusOperationsBasedOnNextOperationAsSyntaxUnit(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) throws Exception {
        if (currentSyntaxUnitIndexInSyntaxUnitsList + 4 < syntaxUnits.size()) {
            SyntaxUnit nextOperationAfterNextSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 3);
            if (!nextOperationAfterNextSyntaxUnit.getValue().matches("[*/]")) {
                currentSyntaxUnitIndexInSyntaxUnitsList = processPlusOrMinusOperation(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
            }
        } else {
            processPlusOrMinusOperation(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
        }

        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    private int processPlusOrMinusOperation(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) throws Exception {
        SyntaxUnit currentSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList);
        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 1);
        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 2);

        // todo to be implemented
        if (currentSyntaxUnit instanceof Number && nextSyntaxUnit instanceof Number) {
            double currentNumber = Double.parseDouble(currentSyntaxUnit.getValue());
            double nextNumber = Double.parseDouble(nextSyntaxUnit.getValue());
            double result = calculateResult(nextOperationAsSyntaxUnit.getValue(), currentNumber, nextNumber);
            syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String resultAsString = decimalFormat.format(result).replaceAll(",", ".");
            syntaxUnits.add(currentSyntaxUnitIndexInSyntaxUnitsList, new Number(0, resultAsString));
            currentSyntaxUnitIndexInSyntaxUnitsList--;
        } else if (currentSyntaxUnit instanceof Number) {
            double currentNumber = Double.parseDouble(currentSyntaxUnit.getValue());

            if (nextSyntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                    syntaxUnits.set(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnitInsideLogicalBlock);
                    currentSyntaxUnitIndexInSyntaxUnitsList--;
                } else if (syntaxContainer instanceof LogicalBlock logicalBlock) {
                    combineAdjacentSyntaxUnits(logicalBlock.getSyntaxUnits());
                    if (logicalBlock.getSyntaxUnits().size() == 1) {
                        currentSyntaxUnitIndexInSyntaxUnitsList--;
                    }
                } else {
                    combineAdjacentSyntaxUnits(syntaxContainer.getSyntaxUnits());
                }
            } else {
                if (currentNumber == 0) {
                    switch (nextOperationAsSyntaxUnit.getValue()) {
                        case "+" -> {
                            syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList, currentSyntaxUnitIndexInSyntaxUnitsList + 2).clear();
                            currentSyntaxUnitIndexInSyntaxUnitsList--;
                        }
                        case "-" -> {
                            syntaxUnits.remove(currentSyntaxUnitIndexInSyntaxUnitsList);
                            currentSyntaxUnitIndexInSyntaxUnitsList--;
                        }
                    }
                }
            }
        } else if (nextSyntaxUnit instanceof Number) {
            double nextNumber = Double.parseDouble(nextSyntaxUnit.getValue());
            if (nextNumber == 0) {
                syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList + 1, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();
                currentSyntaxUnitIndexInSyntaxUnitsList--;
            }
        }
        // ----

        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    // *
    private int processMultiplicationOperationWithSyntaxUnits(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) {
        if (currentSyntaxUnitIndexInSyntaxUnitsList - 2 >= 0) {
            SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList - 1);

            if (!previousOperationAsSyntaxUnit.getValue().equals("/")) {
                currentSyntaxUnitIndexInSyntaxUnitsList = processMultiplicationOperation(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
            }
        } else {
            currentSyntaxUnitIndexInSyntaxUnitsList = processMultiplicationOperation(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
        }
        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    private int processMultiplicationOperation(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) {
        SyntaxUnit currentSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList);
        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 1);
        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 2);

        // todo to be implemented
        if (currentSyntaxUnit instanceof Number && nextSyntaxUnit instanceof Number) {
            double currentNumber = Double.parseDouble(currentSyntaxUnit.getValue());
            double nextNumber = Double.parseDouble(nextSyntaxUnit.getValue());
            double result = calculateResult(nextOperationAsSyntaxUnit.getValue(), currentNumber, nextNumber);
            syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String resultAsString = decimalFormat.format(result).replaceAll(",", ".");
            syntaxUnits.add(currentSyntaxUnitIndexInSyntaxUnitsList, new Number(0, resultAsString));
            currentSyntaxUnitIndexInSyntaxUnitsList--;
        } else if (currentSyntaxUnit instanceof Number) {
            if (nextSyntaxUnit instanceof SyntaxContainer) {

            } else {
                double currentNumber = Double.parseDouble(currentSyntaxUnit.getValue());
                if (currentNumber == 0) {
                    syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList + 1, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();
                    currentSyntaxUnitIndexInSyntaxUnitsList--;
                } else if (currentNumber == 1) {
                    syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList, currentSyntaxUnitIndexInSyntaxUnitsList + 2).clear();
                    currentSyntaxUnitIndexInSyntaxUnitsList--;
                }
            }
        } else if (nextSyntaxUnit instanceof Number) {
            double nextNumber = Double.parseDouble(nextSyntaxUnit.getValue());
            if (nextNumber == 0) {
                syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList, currentSyntaxUnitIndexInSyntaxUnitsList + 2).clear();
                currentSyntaxUnitIndexInSyntaxUnitsList--;
            } else if (nextNumber == 1) {
                syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList + 1, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();
                currentSyntaxUnitIndexInSyntaxUnitsList--;
            }
        }
        // ----

        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    // /
    private int processDivisionOperationWithSyntaxUnits(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) throws Exception {
        if (currentSyntaxUnitIndexInSyntaxUnitsList - 2 >= 0) {
            SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList - 1);

            if (!previousOperationAsSyntaxUnit.getValue().matches("[*/]")) {
                currentSyntaxUnitIndexInSyntaxUnitsList = processDivisionOperation(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
            }
        } else {
            currentSyntaxUnitIndexInSyntaxUnitsList = processDivisionOperation(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnits);
        }
        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    private int processDivisionOperation(int currentSyntaxUnitIndexInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) throws Exception {
        SyntaxUnit currentSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList);
        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 1);
        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentSyntaxUnitIndexInSyntaxUnitsList + 2);

        // todo to be implemented
        if (currentSyntaxUnit instanceof Number && nextSyntaxUnit instanceof Number) {
            double currentNumber = Double.parseDouble(currentSyntaxUnit.getValue());
            double nextNumber = Double.parseDouble(nextSyntaxUnit.getValue());
            if (nextNumber != 0) {
                double result = calculateResult(nextOperationAsSyntaxUnit.getValue(), currentNumber, nextNumber);
                syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();

                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String resultAsString = decimalFormat.format(result).replaceAll(",", ".");
                syntaxUnits.add(currentSyntaxUnitIndexInSyntaxUnitsList, new Number(0, resultAsString));
                currentSyntaxUnitIndexInSyntaxUnitsList--;
            }
        } else if (currentSyntaxUnit instanceof Number) {
            if (nextSyntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                    syntaxUnits.set(currentSyntaxUnitIndexInSyntaxUnitsList, syntaxUnitInsideLogicalBlock);
                    currentSyntaxUnitIndexInSyntaxUnitsList--;
                } else if (syntaxContainer instanceof LogicalBlock logicalBlock) {
                    combineAdjacentSyntaxUnits(logicalBlock.getSyntaxUnits());
                    if (logicalBlock.getSyntaxUnits().size() == 1) {
                        currentSyntaxUnitIndexInSyntaxUnitsList--;
                    }
                } else {
                    combineAdjacentSyntaxUnits(syntaxContainer.getSyntaxUnits());
                }
            } else {
                double currentNumber = Double.parseDouble(currentSyntaxUnit.getValue());
                if (currentNumber == 0) {
                    syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList + 1, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();
                    currentSyntaxUnitIndexInSyntaxUnitsList--;
                } else if (currentNumber == 1) {
                    syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList, currentSyntaxUnitIndexInSyntaxUnitsList + 2).clear();
                    currentSyntaxUnitIndexInSyntaxUnitsList--;
                }
            }
        } else if (nextSyntaxUnit instanceof Number) {
            double nextNumber = Double.parseDouble(nextSyntaxUnit.getValue());
            if (nextNumber == 1) {
                syntaxUnits.subList(currentSyntaxUnitIndexInSyntaxUnitsList + 1, currentSyntaxUnitIndexInSyntaxUnitsList + 3).clear();
                currentSyntaxUnitIndexInSyntaxUnitsList--;
            }
        }
        // ----

        return currentSyntaxUnitIndexInSyntaxUnitsList;
    }

    private double calculateResult(String operation, double currentNumber, double nextNumber) {
        return switch (operation) {
            case "+" -> currentNumber + nextNumber;
            case "-" -> currentNumber - nextNumber;
            case "*" -> currentNumber * nextNumber;
            case "/" -> currentNumber / nextNumber;
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
    }

    private String convertDoubleToStringWithRounding(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(number).replaceAll(",", ".");
    }
}
