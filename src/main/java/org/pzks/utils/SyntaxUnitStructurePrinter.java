package org.pzks.utils;

import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;

import java.util.List;

public class SyntaxUnitStructurePrinter {

    private static final StringBuilder detailedTreeSpaceBuilder = new StringBuilder();
    private static final StringBuilder spaceBuilder = new StringBuilder();

    public static void printTreeWithDetails(List<SyntaxUnit> syntaxUnits) {
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            System.out.println(detailedTreeSpaceBuilder + "- " + syntaxUnit);
            if (!syntaxUnit.getSyntaxUnits().isEmpty()) {
                List<SyntaxUnit> internalSyntaxUnits = syntaxUnit.getSyntaxUnits();
                detailedTreeSpaceBuilder.append("     ");
                printTreeWithDetails(internalSyntaxUnits);
                detailedTreeSpaceBuilder.setLength(detailedTreeSpaceBuilder.length() - 5);
            }
        }
    }

    public static void printTree(List<SyntaxUnit> syntaxUnits) {
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            System.out.println(spaceBuilder + "- " + syntaxUnit.treeUnitRepresentation());
            if (!syntaxUnit.getSyntaxUnits().isEmpty()) {
                List<SyntaxUnit> internalSyntaxUnits = syntaxUnit.getSyntaxUnits();
                spaceBuilder.append("     ");
                printTree(internalSyntaxUnits);
                spaceBuilder.setLength(spaceBuilder.length() - 5);
            }
        }
    }

    public static void printAsString(List<SyntaxUnit> syntaxUnits) {
        StringBuilder expression = new StringBuilder();
        addSyntaxUnitsToString(syntaxUnits, expression);
        System.out.println(expression);
    }

    private static void addSyntaxUnitsToString(List<SyntaxUnit> syntaxUnits, StringBuilder expression) {
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            if (syntaxUnit instanceof SyntaxContainer syntaxContainer) {
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
            } else {
                expression.append(syntaxUnit.getValue());
            }
        }
    }
}
