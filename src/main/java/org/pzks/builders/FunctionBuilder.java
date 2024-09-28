package org.pzks.builders;

import org.pzks.units.Function;
import org.pzks.units.SyntaxUnit;
import org.pzks.units.Variable;

import java.util.ArrayList;
import java.util.List;

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
        int i = 1;
        nextLogicalUnit = getLogicalUnits().get(getCurrentLogicalUnitIndexInLogicalUnits() + i);
        int nextUnitIndexIfNextUnitMatchesSpace = getCurrentLogicalUnitIndexInLogicalUnits() + i + 1;
        if (nextLogicalUnit.matches("\\s+") && nextUnitIndexIfNextUnitMatchesSpace != getLogicalUnits().size()) {
            i++;
            nextLogicalUnit = getLogicalUnits().get(getCurrentLogicalUnitIndexInLogicalUnits() + i);
        }

        int indexInOriginalList = getCurrentLogicalUnitIndexInLogicalUnits();
        if (nextLogicalUnit.matches("\\(")) {
            List<String> functionUnits = new ArrayList<>();
            functionUnits.add(getCurrentLogicalUnit() + String.join("", getLogicalUnits().subList(
                    getCurrentLogicalUnitIndexInLogicalUnits() + 1,
                    getCurrentLogicalUnitIndexInLogicalUnits() + i
            )));
            functionUnits.add(nextLogicalUnit);
            indexInOriginalList = addUnitsToStructure(functionUnits, getCurrentLogicalUnitIndexInLogicalUnits(), i + 1);

            if (functionUnits.size() == 2 && String.join("", functionUnits).matches("\\w+\\s*\\(")) {
                indexInOriginalList += 2;
            }

            getSyntaxUnits().add(new Function(getSyntaxUnitIndex(), functionUnits));
        } else {
            getSyntaxUnits().add(new Variable(getSyntaxUnitIndex(), getCurrentLogicalUnit()));
        }
        return indexInOriginalList;
    }
}
