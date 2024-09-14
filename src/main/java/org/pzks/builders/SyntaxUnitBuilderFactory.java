package org.pzks.builders;

import org.pzks.units.SyntaxUnit;

import java.util.List;

public class SyntaxUnitBuilderFactory {
    public SyntaxUnitBuilder getFunctionBuilder(
            List<SyntaxUnit> syntaxUnits,
            List<String> logicalUnits,
            String currentLogicalUnit,
            int currentLogicalUnitIndexInLogicalUnits,
            int syntaxUnitIndex
    ) {
        return new FunctionBuilder(
                syntaxUnits,
                logicalUnits,
                currentLogicalUnit,
                currentLogicalUnitIndexInLogicalUnits,
                syntaxUnitIndex
        );
    }

    public SyntaxUnitBuilder getLogicalBlockBuilder(
            List<SyntaxUnit> syntaxUnits,
            List<String> logicalUnits,
            String currentLogicalUnit,
            int currentLogicalUnitIndexInLogicalUnits,
            int syntaxUnitIndex
    ) {
        return new LogicalBlockBuilder(
                syntaxUnits,
                logicalUnits,
                currentLogicalUnit,
                currentLogicalUnitIndexInLogicalUnits,
                syntaxUnitIndex
        );
    }
}
