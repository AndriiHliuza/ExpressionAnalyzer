package org.pzks.utils;

import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class SyntaxUnitMetaDataPrinter {

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
            SyntaxUnitMetaDataPrinter.printTree(syntaxUnits, includeDetails);
        }
    }

    public static void printExpressionWithErrorsPointing(String expression, List<Integer> errorsPositions) {
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + expression);

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
}
