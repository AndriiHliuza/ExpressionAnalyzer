package org.pzks.utils;

import org.pzks.units.FunctionParam;
import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class SyntaxUnitStructurePrinter {

    private static final StringBuilder spaceBuilder = new StringBuilder();

    public static void printTree(List<SyntaxUnit> syntaxUnits, boolean printWithDetails) {
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            String syntaxUnitStringRepresentation = printWithDetails ? syntaxUnit.toString() : syntaxUnit.treeUnitRepresentation();
            System.out.println(spaceBuilder + "- " + syntaxUnitStringRepresentation);
            if (!syntaxUnit.getSyntaxUnits().isEmpty()) {
                List<SyntaxUnit> internalSyntaxUnits = syntaxUnit.getSyntaxUnits();
                spaceBuilder.append("     ");
                printTree(internalSyntaxUnits, printWithDetails);
                spaceBuilder.setLength(spaceBuilder.length() - 5);
            }
        }
    }

    public static void printTreeWithHeadline(boolean printTree, boolean includeDetails, SyntaxUnit parsedSyntaxUnit, String headline) {
        if (printTree) {
            HeadlinePrinter.print(headline, Color.CYAN);
            List<SyntaxUnit> syntaxUnits = parsedSyntaxUnit.getSyntaxUnits();
            SyntaxUnitStructurePrinter.printTree(syntaxUnits, includeDetails);
        }
    }

    public static String getExpressionAsString(List<SyntaxUnit> syntaxUnits) {
        StringBuilder expression = new StringBuilder();
        addSyntaxUnitsToString(syntaxUnits, expression);
        return expression.toString().replaceAll("\\s+", "");
    }

    public static void printExpressionWithErrorsPointing(String expression, List<Integer> errorsPositions) {
        System.out.println(Color.RED.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + expression);

        List<StringBuilder> errorLines = new ArrayList<>();
        errorLines.add(new StringBuilder(" ".repeat(expression.length() + 1)));
        for (Integer errorPosition : errorsPositions) {
            boolean placed = false;
            for (StringBuilder line : errorLines) {
                if (line.charAt(errorPosition) == ' ') {
                    line.setCharAt(errorPosition, '^');
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                StringBuilder newLine = new StringBuilder(" ".repeat(expression.length() + 1));
                newLine.setCharAt(errorPosition, '^');
                errorLines.add(newLine);
            }
        }

        for (StringBuilder errorLine : errorLines) {
            if (!errorLine.toString().trim().isEmpty()) {
                System.out.println(Color.RED.getAnsiValue() + " ".repeat(12) + errorLine + Color.DEFAULT.getAnsiValue());
            }
        }
        System.out.println();
    }

    private static void addSyntaxUnitsToString(List<SyntaxUnit> syntaxUnits, StringBuilder expression) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof FunctionParam functionParam) {
                    addSyntaxUnitsToString(functionParam.getSyntaxUnits(), expression);
                    if (i != syntaxUnits.size() - 1) {
                        expression.append(",");
                    }
                } else {
                    String openingBracket = syntaxContainer.getDetails().get("openingBracket");
                    String closingBracket = syntaxContainer.getDetails().get("closingBracket");
                    String functionName = "";
                    String name = syntaxContainer.getDetails().get("name");
                    if (name != null) {
                        functionName += name;
                    }
                    expression.append(functionName).append(openingBracket);
                    addSyntaxUnitsToString(syntaxContainer.getSyntaxUnits(), expression);
                    expression.append(closingBracket);
                }
            } else {
                expression.append(syntaxUnit.getValue());
            }
        }
    }
}
