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
    public void fix() throws Exception {
        if (getCurrentSyntaxUnit() == null) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemovedFromSyntaxUnits(true);
            return;
        }

        SyntaxContainer syntaxContainer = (SyntaxContainer) getCurrentSyntaxUnit();
        boolean isContainerRemoved = checkIsContainerEmptyAndForRemoval(syntaxContainer);
        if (!(syntaxContainer instanceof FunctionParam)) {
            syntaxContainer.getDetails().putIfAbsent("closingBracket", ")");
        }
        processCompatibilityWithPreviousSyntaxUnit(isContainerRemoved);
    }

    private boolean checkIsContainerEmptyAndForRemoval(SyntaxContainer syntaxContainer) throws Exception {
        boolean isContainerRemoved = false;
        if (syntaxContainer.getSyntaxUnits().isEmpty() && syntaxContainer instanceof LogicalBlock) {
            getSyntaxUnits().remove(getCurrentSyntaxUnit());
            setSyntaxUnitRemovedFromSyntaxUnits(true);
            isContainerRemoved = true;
        } else if (syntaxContainer instanceof FunctionParam functionParam) {
            if (functionParam.getSyntaxUnits().isEmpty() ||
                    (functionParam.getSyntaxUnits().size() == 1 && functionParam.getSyntaxUnits().getFirst() instanceof Space)) {
                getSyntaxUnits().remove(getCurrentSyntaxUnit());
                setSyntaxUnitRemovedFromSyntaxUnits(true);
                isContainerRemoved = true;
            } else {
                ExpressionFixer expressionFixer = new ExpressionFixer(functionParam);
                functionParam.setSyntaxUnits(expressionFixer.getFixedSyntaxUnit().getSyntaxUnits());
                if (functionParam.getSyntaxUnits().isEmpty()) {
                    getSyntaxUnits().remove(getCurrentSyntaxUnit());
                    setSyntaxUnitRemovedFromSyntaxUnits(true);
                    isContainerRemoved = true;
                }
            }
        } else {
            ExpressionFixer expressionFixer = new ExpressionFixer(syntaxContainer);
            syntaxContainer.setSyntaxUnits(expressionFixer.getFixedSyntaxUnit().getSyntaxUnits());
            if (syntaxContainer.getSyntaxUnits().isEmpty() && syntaxContainer instanceof LogicalBlock) {
                getSyntaxUnits().remove(getCurrentSyntaxUnit());
                setSyntaxUnitRemovedFromSyntaxUnits(true);
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
                    if (!(getPreviousSyntaxUnit() instanceof FunctionParam)) {
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

}

