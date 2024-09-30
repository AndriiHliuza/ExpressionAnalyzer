package org.pzks.parsers;

import org.pzks.units.*;
import org.pzks.units.Number;

import java.text.DecimalFormat;
import java.util.List;

public class ExpressionSimplifier {
    private List<SyntaxUnit> syntaxUnits;

    public ExpressionSimplifier(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
    }

    public void simplify() {
        removeUnnecessaryBracketsInLogicalBlocks(syntaxUnits);
        combineAdjacentNumbers(syntaxUnits, List.of("*", "/"));
        combineAdjacentNumbers(syntaxUnits, List.of("*", "/", "+", "-"));

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

    private void combineAdjacentNumbers(List<SyntaxUnit> syntaxUnits, List<String> operationsToUse) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof Number && i != 0) {
                i = calculateCombinedNumber(syntaxUnit, i, syntaxUnits, operationsToUse);
            } else if (syntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                    if (syntaxUnitInsideLogicalBlock instanceof Number) {
                        if (i != 0) {
                            i = calculateCombinedNumber(syntaxUnitInsideLogicalBlock, i, syntaxUnits, operationsToUse);
                        } else {
                            syntaxUnits.set(i, new Number(0, syntaxUnitInsideLogicalBlock.getValue()));
                        }
                    }
                } else {
                    combineAdjacentNumbers(syntaxContainer.getSyntaxUnits(), operationsToUse);
                    if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                        i--;
                    }
                }
            }
        }
    }

    private int calculateCombinedNumber(SyntaxUnit currentNumberSyntaxUnit, int currentPosition, List<SyntaxUnit> syntaxUnits, List<String> operationsToUse) {
        double currentNumber = Double.parseDouble(currentNumberSyntaxUnit.getValue());
        SyntaxUnit previousSyntaxUnitBeforeOperation = syntaxUnits.get(currentPosition - 2);
        if (previousSyntaxUnitBeforeOperation instanceof Number) {
            double previousNumber = Double.parseDouble(previousSyntaxUnitBeforeOperation.getValue());
            SyntaxUnit operationSyntaxUnit = syntaxUnits.get(currentPosition - 1);
            if (operationSyntaxUnit instanceof Operation && operationsToUse.contains(operationSyntaxUnit.getValue())) {

                if (!(currentNumberSyntaxUnit.getValue().equals("0") && operationSyntaxUnit.getValue().equals("/"))) {
                    if (currentPosition - 3 >= 0) {
                        SyntaxUnit operationBeforePreviousSyntaxUnit = syntaxUnits.get(currentPosition - 3);

                        if (operationBeforePreviousSyntaxUnit.getValue().equals("+") ||
                                (operationBeforePreviousSyntaxUnit.getValue().equals("-") && operationSyntaxUnit.getValue().matches("[*/]")) ||
                                (operationBeforePreviousSyntaxUnit.getValue().equals("*") && operationSyntaxUnit.getValue().equals("*"))) {
                            currentPosition = calculateAndReplaceNumbers(syntaxUnits, currentPosition, operationSyntaxUnit, previousNumber, currentNumber);
                        }
                    } else if (currentPosition + 1 < syntaxUnits.size()) {
                        SyntaxUnit nextOperationSyntaxUnit = syntaxUnits.get(currentPosition + 1);
                        if (!(nextOperationSyntaxUnit.getValue().matches("[*/]") && operationSyntaxUnit.getValue().matches("[+-]"))) {
                            currentPosition = calculateAndReplaceNumbers(syntaxUnits, currentPosition, operationSyntaxUnit, previousNumber, currentNumber);
                        }
                    } else {
                        currentPosition = calculateAndReplaceNumbers(syntaxUnits, currentPosition, operationSyntaxUnit, previousNumber, currentNumber);
                    }
                }
            }
        }

        return currentPosition;
    }

    private static int calculateAndReplaceNumbers(List<SyntaxUnit> syntaxUnits, int currentPosition, SyntaxUnit operationSyntaxUnit, double previousNumber, double currentNumber) {
        double resultedNumber = getCalculatedNumber(operationSyntaxUnit.getValue(), previousNumber, currentNumber);
        syntaxUnits.subList(currentPosition - 2, currentPosition + 1).clear();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String resultedNumberToSet = decimalFormat.format(resultedNumber).replaceAll(",", ".");
        syntaxUnits.add(currentPosition - 2, new Number(0, resultedNumberToSet));
        currentPosition--;
        return currentPosition;
    }


    private static double getCalculatedNumber(String operation, double previousNumber, double currentNumber) {
        return switch (operation) {
            case "+" -> previousNumber + currentNumber;
            case "-" -> previousNumber - currentNumber;
            case "*" -> previousNumber * currentNumber;
            case "/" -> previousNumber / currentNumber;
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
    }
}
