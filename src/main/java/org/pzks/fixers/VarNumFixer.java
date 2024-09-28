package org.pzks.fixers;

import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.RandomValuesGenerator;

import java.util.List;

public class VarNumFixer extends SyntaxUnitFixer {

    public VarNumFixer(int currentUnitPositionInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) {
        super(currentUnitPositionInSyntaxUnitsList, syntaxUnits);
    }

    @Override
    public void fix() {
        if (getCurrentSyntaxUnit() == null) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemovedFromSyntaxUnits(true);
            return;
        }

        if (getPreviousSyntaxUnit() != null) {
            if (getPreviousSyntaxUnit() instanceof Number ||
            getPreviousSyntaxUnit() instanceof Variable ||
            getPreviousSyntaxUnit() instanceof SyntaxContainer) {
                getSyntaxUnits().add(
                        getCurrentUnitPositionInSyntaxUnitsList(),
                        new Operation(0, RandomValuesGenerator.generateOperation())
                );
                setNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit(true);
            }
        }
    }
}
