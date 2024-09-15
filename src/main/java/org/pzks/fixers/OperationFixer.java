package org.pzks.fixers;

import org.pzks.units.*;
import org.pzks.utils.RandomValuesGenerator;

import java.util.List;

public class OperationFixer extends SyntaxUnitFixer {
    public OperationFixer(int currentUnitPositionInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) {
        super(currentUnitPositionInSyntaxUnitsList, syntaxUnits);
    }

    @Override
    public void fix() {
        if (getCurrentSyntaxUnit() == null) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemoverFromSyntaxUnits(true);
            return;
        }

        if (getCurrentUnitPositionInSyntaxUnitsList() == 0) {
            getSyntaxUnits().addFirst(
                    new Variable(0, RandomValuesGenerator.generateVariableName())
            );
            setNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit(true);
        }

        if (getCurrentUnitPositionInSyntaxUnitsList() == getSyntaxUnits().size() - 1) {
            getSyntaxUnits().add(
                    new Variable(0, RandomValuesGenerator.generateVariableName())
            );
            setNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit(true);
        }

        if (getPreviousSyntaxUnit() != null && getPreviousSyntaxUnit() instanceof Operation) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemoverFromSyntaxUnits(true);
        }
    }
}