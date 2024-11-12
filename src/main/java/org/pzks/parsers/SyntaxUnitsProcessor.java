package org.pzks.parsers;

import org.pzks.units.LogicalBlock;
import org.pzks.units.Number;
import org.pzks.units.SyntaxUnit;

import java.util.List;

public class SyntaxUnitsProcessor {
    public static void takeOutNumberFromLogicalBlocksWithOneElement(List<SyntaxUnit> syntaxUnits) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof LogicalBlock) {
                if (syntaxUnit.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInLogicalBlock = syntaxUnit.getSyntaxUnits().getFirst();
                    if (syntaxUnitInLogicalBlock instanceof Number number) {
                        syntaxUnits.set(i, number);
                    } else {
                        takeOutNumberFromLogicalBlocksWithOneElement(syntaxUnitInLogicalBlock.getSyntaxUnits());
                    }
                } else {
                    takeOutNumberFromLogicalBlocksWithOneElement(syntaxUnit.getSyntaxUnits());
                }
            }
        }
    }
}
