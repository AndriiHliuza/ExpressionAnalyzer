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
            setSyntaxUnitRemovedFromSyntaxUnits(true);
            return;
        }

        if (getCurrentUnitPositionInSyntaxUnitsList() == 0) {
            if (getCurrentUnitPositionInSyntaxUnitsList() < getSyntaxUnits().size() - 1) {
                SyntaxUnit nextSyntaxUnit = getSyntaxUnits().get(getCurrentUnitPositionInSyntaxUnitsList() + 1);
                if (!((nextSyntaxUnit instanceof Variable || nextSyntaxUnit instanceof SyntaxContainer) && getCurrentSyntaxUnit().getValue().matches("[+\\-]"))) {
                    getSyntaxUnits().addFirst(
                            new Variable(0, RandomValuesGenerator.generateVariableName())
                    );
                    setNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit(true);
                }
            } else {
                getSyntaxUnits().addFirst(
                        new Variable(0, RandomValuesGenerator.generateVariableName())
                );
                setNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit(true);
            }
        }

        if (getCurrentUnitPositionInSyntaxUnitsList() == getSyntaxUnits().size() - 1) {
            getSyntaxUnits().add(
                    new Variable(0, RandomValuesGenerator.generateVariableName())
            );
            setNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit(true);
        }

        if (getPreviousSyntaxUnit() != null && getPreviousSyntaxUnit() instanceof Operation) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemovedFromSyntaxUnits(true);
        }
    }
}
