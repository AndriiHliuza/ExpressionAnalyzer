package org.pzks.builders;

import org.pzks.units.LogicalBlock;
import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class LogicalBlockBuilder extends SyntaxUnitBuilder {

    public LogicalBlockBuilder(
            List<SyntaxUnit> syntaxUnits,
            List<String> logicalUnits,
            String currentLogicalUnit,
            int currentLogicalUnitIndexInLogicalUnits,
            int syntaxUnitIndex
    ) {
        super(syntaxUnits, logicalUnits, currentLogicalUnit, currentLogicalUnitIndexInLogicalUnits, syntaxUnitIndex);
    }

    @Override
    public int build() throws Exception {
        List<String> blockUnits = new ArrayList<>();
        blockUnits.add(getCurrentLogicalUnit());
        int indexInOriginalList = addUnitsToStructure(blockUnits, getCurrentLogicalUnitIndexInLogicalUnits(), 1);
        getSyntaxUnits().add(new LogicalBlock(getSyntaxUnitIndex(), blockUnits));
        return indexInOriginalList;
    }
}
