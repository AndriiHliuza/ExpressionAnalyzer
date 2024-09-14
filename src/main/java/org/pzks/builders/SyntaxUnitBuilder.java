package org.pzks.builders;

import org.pzks.units.SyntaxUnit;

import java.util.List;

public abstract class SyntaxUnitBuilder {
    private final List<SyntaxUnit> syntaxUnits;
    private final List<String> logicalUnits;
    private final String currentLogicalUnit;
    private final int currentLogicalUnitIndexInLogicalUnits;
    private final int syntaxUnitIndex;

    public SyntaxUnitBuilder(
            List<SyntaxUnit> syntaxUnits,
            List<String> logicalUnits,
            String currentLogicalUnit,
            int currentLogicalUnitIndexInLogicalUnits,
            int syntaxUnitIndex
    ) {
        this.syntaxUnits = syntaxUnits;
        this.logicalUnits = logicalUnits;
        this.currentLogicalUnit = currentLogicalUnit;
        this.currentLogicalUnitIndexInLogicalUnits = currentLogicalUnitIndexInLogicalUnits;
        this.syntaxUnitIndex = syntaxUnitIndex;
    }

    public List<SyntaxUnit> getSyntaxUnits() {
        return syntaxUnits;
    }

    public List<String> getLogicalUnits() {
        return logicalUnits;
    }

    public String getCurrentLogicalUnit() {
        return currentLogicalUnit;
    }

    public int getCurrentLogicalUnitIndexInLogicalUnits() {
        return currentLogicalUnitIndexInLogicalUnits;
    }

    public int getSyntaxUnitIndex() {
        return syntaxUnitIndex;
    }

    public abstract int build() throws Exception;

    public int addUnitsToStructure(List<String> units, int logicalUnitsCurrentIndex, int offset) {
        int numberOfOpeningBrackets = 0;
        int numberOfClosingBrackets = 0;

        int indexInOriginalList = logicalUnitsCurrentIndex;
        for (int j = logicalUnitsCurrentIndex + offset; j < logicalUnits.size(); j++) {
            String logicalUnitInsideBlock = logicalUnits.get(j);
            if (logicalUnitInsideBlock.matches("\\(")) {
                numberOfOpeningBrackets++;
                units.add(logicalUnitInsideBlock);
            } else if (logicalUnitInsideBlock.matches("\\)")) {
                if (numberOfOpeningBrackets == numberOfClosingBrackets) {
                    units.add(logicalUnitInsideBlock);
                    indexInOriginalList = j;
                    break;
                } else {
                    numberOfClosingBrackets++;
                    units.add(logicalUnitInsideBlock);
                }
            } else {
                units.add(logicalUnitInsideBlock);
            }
            indexInOriginalList = j;
        }
        return indexInOriginalList;
    }
}
