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

    private void combineAdjacentSyntaxUnits(List<SyntaxUnit> syntaxUnits) throws Exception {
        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);
        simplifySimpleUnits(syntaxUnits);

        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);
            if (currentSyntaxUnit instanceof Number) {
                i = combineAdjacentSyntaxUnitIfCurrentUnitIsNumber(i, syntaxUnits);
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
                                currentSyntaxUnit = syntaxUnits.get(i);
                                currentSyntaxUnit.analyzeArithmeticErrors();
                                if (currentSyntaxUnit.getArithmeticErrors().isEmpty()) {
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

    private int combineAdjacentSyntaxUnitIfCurrentUnitIsNumber(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) throws Exception {
        if (currentIndexInSyntaxUnits + 2 < syntaxUnits.size()) {
            SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
            if (nextSyntaxUnit instanceof Number) {
                currentIndexInSyntaxUnits = combineNumbers(currentIndexInSyntaxUnits, syntaxUnits);
            } else if (nextSyntaxUnit instanceof SyntaxContainer syntaxContainer) {
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

        return currentIndexInSyntaxUnits;
    }

    private int combineNumberWithContainer(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) throws Exception {
        SyntaxUnit currentNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits);
        double currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());

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
//            SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
//            if (nextOperationAsSyntaxUnit instanceof Operation operation) {
//                if (currentNumber == 0) {
//                    switch (operation.getValue()) {
//                        case String operationValue when operationValue.matches("[*/]") -> {
//                            syntaxContainer = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
//                            syntaxContainer.analyzeArithmeticErrors();
//                            if (syntaxContainer.getArithmeticErrors().isEmpty()) {
//                                if (!(operation.getValue().equals("/") && syntaxContainer.getValue().matches("0|\\+0|-0"))) {
//                                    if (currentIndexInSyntaxUnits - 2 >= 0) {
//                                        SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
//                                        if (previousOperationAsSyntaxUnit.getValue().matches("[*+\\-]")) {
//                                            syntaxUnits.subList(currentIndexInSyntaxUnits + 1, currentIndexInSyntaxUnits + 3).clear();
//                                            decrementCurrentIndexInSyntaxUnits = true;
//                                        }
//                                    } else {
//                                        syntaxUnits.subList(currentIndexInSyntaxUnits + 1, currentIndexInSyntaxUnits + 3).clear();
//                                        decrementCurrentIndexInSyntaxUnits = true;
//                                    }
//                                }
//                            }
//                        }
//                        case String operationValue when operationValue.equals("+") -> {
//                            if (currentIndexInSyntaxUnits - 2 >= 0) {
//                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
//                                if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
//                                    syntaxUnits.subList(currentIndexInSyntaxUnits - 1, currentIndexInSyntaxUnits + 1).clear();
//                                    decrementCurrentIndexInSyntaxUnits = true;
//                                }
//                            } else {
//                                syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 2).clear();
//                                decrementCurrentIndexInSyntaxUnits = true;
//                            }
//                        }
//                        case String operationValue when operationValue.equals("-") -> {
//                            if (currentIndexInSyntaxUnits - 2 >= 0) {
//                                SyntaxUnit previousOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits - 1);
//                                if (previousOperationAsSyntaxUnit.getValue().matches("[+\\-]")) {
//                                    syntaxUnits.subList(currentIndexInSyntaxUnits - 1, currentIndexInSyntaxUnits + 1).clear();
//                                    decrementCurrentIndexInSyntaxUnits = true;
//                                }
//                            } else {
//                                syntaxUnits.subList(currentIndexInSyntaxUnits, currentIndexInSyntaxUnits + 1).clear();
//                                decrementCurrentIndexInSyntaxUnits = true;
//                            }
//                        }
//                        default -> throw new IllegalStateException("Unexpected value: " + operation.getValue());
//                    }
//                }
//            }
//
            if (decrementCurrentIndexInSyntaxUnits) {
                currentIndexInSyntaxUnits--;
            }
        }
        return currentIndexInSyntaxUnits;
    }

    private int combineNumberWithSyntaxUnit(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits) {
        SyntaxUnit currentNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits);
        double currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());

        SyntaxUnit nextSyntaxUnit;

        boolean decrementCurrentIndexInSyntaxUnits = false;
        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);
        if (nextOperationAsSyntaxUnit instanceof Operation operation) {
            if (currentNumber == 0) {
                switch (operation.getValue()) {
                    case String operationValue when operationValue.matches("[*/]") -> {
                        nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
                        nextSyntaxUnit.analyzeArithmeticErrors();
                        if (nextSyntaxUnit.getArithmeticErrors().isEmpty()) {
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

    private int combineZeroNumberWithSyntaxContainerOrSyntaxUnit(int currentIndexInSyntaxUnits, List<SyntaxUnit> syntaxUnits, boolean decrementCurrentIndexInSyntaxUnits) {
        SyntaxUnit currentNumberAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits);
        double currentNumber = Double.parseDouble(currentNumberAsSyntaxUnit.getValue());

        SyntaxUnit nextOperationAsSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 1);

        SyntaxUnit nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);

        if (nextOperationAsSyntaxUnit instanceof Operation operation) {
            if (currentNumber == 0) {
                switch (operation.getValue()) {
                    case String operationValue when operationValue.matches("[*/]") -> {
                        nextSyntaxUnit = syntaxUnits.get(currentIndexInSyntaxUnits + 2);
                        nextSyntaxUnit.analyzeArithmeticErrors();
                        if (nextSyntaxUnit.getArithmeticErrors().isEmpty()) {
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

        return currentIndexInSyntaxUnits;
    }

    // simple units simplifications
    private void simplifySimpleUnits(List<SyntaxUnit> syntaxUnits) throws Exception {
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

    private String convertDoubleToStringWithRounding(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(number).replaceAll(",", ".");
    }
}
