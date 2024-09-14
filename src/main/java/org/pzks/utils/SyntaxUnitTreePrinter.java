package org.pzks.utils;

import org.pzks.units.SyntaxUnit;

import java.util.List;

public class SyntaxUnitTreePrinter {

    private final StringBuilder detailedTreeSpaceBuilder = new StringBuilder();
    private final StringBuilder spaceBuilder = new StringBuilder();

    public void detailedPrint(List<SyntaxUnit> syntaxUnits) {
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            System.out.println(detailedTreeSpaceBuilder + "- " + syntaxUnit);
            if (!syntaxUnit.getSyntaxUnits().isEmpty()) {
                List<SyntaxUnit> internalSyntaxUnits = syntaxUnit.getSyntaxUnits();
                detailedTreeSpaceBuilder.append("     ");
                detailedPrint(internalSyntaxUnits);
                detailedTreeSpaceBuilder.setLength(detailedTreeSpaceBuilder.length() - 5);
            }
        }
    }

    public void print(List<SyntaxUnit> syntaxUnits) {
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            System.out.println(spaceBuilder + "- " + syntaxUnit.treeUnitRepresentation());
            if (!syntaxUnit.getSyntaxUnits().isEmpty()) {
                List<SyntaxUnit> internalSyntaxUnits = syntaxUnit.getSyntaxUnits();
                spaceBuilder.append("     ");
                print(internalSyntaxUnits);
                spaceBuilder.setLength(spaceBuilder.length() - 5);
            }
        }
    }
}
