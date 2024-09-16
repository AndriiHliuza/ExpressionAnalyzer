package org.pzks.builders;

import org.pzks.units.Function;
import org.pzks.units.SyntaxUnit;
import org.pzks.units.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionBuilder extends SyntaxUnitBuilder {

    public FunctionBuilder(
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
        String nextLogicalUnit;
        nextLogicalUnit = getLogicalUnits().get(getCurrentLogicalUnitIndexInLogicalUnits() + 1);
        int indexInOriginalList = getCurrentLogicalUnitIndexInLogicalUnits();
        if (nextLogicalUnit.matches("\\(")) {
            List<String> functionUnits = new ArrayList<>();
            functionUnits.add(getCurrentLogicalUnit());
            functionUnits.add(nextLogicalUnit);
            indexInOriginalList = addUnitsToStructure(functionUnits, getCurrentLogicalUnitIndexInLogicalUnits(), 2);

            if (functionUnits.size() == 2 && String.join("", functionUnits).matches("\\w+\\(")) {
                indexInOriginalList += 2;
            }

            getSyntaxUnits().add(new Function(getSyntaxUnitIndex(), functionUnits));
        } else {
            getSyntaxUnits().add(new Variable(getSyntaxUnitIndex(), getCurrentLogicalUnit()));
        }
        return indexInOriginalList;
    }
}
