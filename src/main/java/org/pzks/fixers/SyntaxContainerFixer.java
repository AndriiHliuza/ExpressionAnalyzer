package org.pzks.fixers;

import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.RandomValuesGenerator;

import java.util.List;

public class SyntaxContainerFixer extends SyntaxUnitFixer {
    public SyntaxContainerFixer(int currentUnitPositionInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) {
        super(currentUnitPositionInSyntaxUnitsList, syntaxUnits);
    }

    @Override
    public void fix() {
        if (getCurrentSyntaxUnit() == null) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemoverFromSyntaxUnits(true);
            return;
        }

        SyntaxContainer syntaxContainer = (SyntaxContainer) getCurrentSyntaxUnit();
        boolean isContainerRemoved = checkIsContainerEmptyAndForRemoval(syntaxContainer);
        syntaxContainer.getDetails().putIfAbsent("closingBracket", ")");
        processCompatibilityWithPreviousSyntaxUnit(isContainerRemoved);
    }

    private boolean checkIsContainerEmptyAndForRemoval(SyntaxContainer syntaxContainer) {
        boolean isContainerRemoved = false;
        if (syntaxContainer.getSyntaxUnits().isEmpty()) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemoverFromSyntaxUnits(true);
            isContainerRemoved = true;
        } else {
            new ExpressionFixer(syntaxContainer.getSyntaxUnits()).fix();
            if (syntaxContainer.getSyntaxUnits().isEmpty()) {
                getSyntaxUnits().remove(getCurrentSyntaxUnit());
                setSyntaxUnitRemoverFromSyntaxUnits(true);
                isContainerRemoved = true;
            }
        }
        return isContainerRemoved;
    }

    private void processCompatibilityWithPreviousSyntaxUnit(boolean isContainerRemoved) {
        if (getPreviousSyntaxUnit() != null) {
            if (isContainerRemoved) {
                if (getPreviousSyntaxUnit() instanceof Operation) {
                    if (getSyntaxUnits().size() == getCurrentUnitPositionInSyntaxUnitsList()) {
                        getSyntaxUnits().add(
                                getCurrentUnitPositionInSyntaxUnitsList(),
                                new Variable(0, RandomValuesGenerator.generateVariableName())
                        );
                    } else if (!(getSyntaxUnits().get(getCurrentUnitPositionInSyntaxUnitsList()) instanceof Number) ||
                            !(getSyntaxUnits().get(getCurrentUnitPositionInSyntaxUnitsList()) instanceof Variable) ||
                            !(getSyntaxUnits().get(getCurrentUnitPositionInSyntaxUnitsList()) instanceof SyntaxContainer)) {
                        getSyntaxUnits().add(
                                getCurrentUnitPositionInSyntaxUnitsList(),
                                new Variable(0, RandomValuesGenerator.generateVariableName())
                        );
                    }
                }
            } else {
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

}

