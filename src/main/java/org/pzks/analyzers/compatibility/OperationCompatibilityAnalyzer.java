package org.pzks.analyzers.compatibility;

import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;

import java.util.List;

public class OperationCompatibilityAnalyzer extends PlainSyntaxUnitCompatibilityAnalyzer {
    private final int currentSyntaxUnitPosition;
    private final List<SyntaxUnit> syntaxUnits;

    public OperationCompatibilityAnalyzer(
            SyntaxUnit previous,
            SyntaxUnit current,
            int currentSyntaxUnitPosition,
            List<SyntaxUnit> syntaxUnits
    ) {
        super(previous, current);
        this.currentSyntaxUnitPosition = currentSyntaxUnitPosition;
        this.syntaxUnits = syntaxUnits;
    }

    @Override
    public boolean isCompatibleWithPreviousSyntaxUnit() {
        boolean isCompatible = false;

        if (getCurrent() == null || getPrevious() == null || !(getCurrent() instanceof Operation)) {
            return false;
        } else if (getPrevious() instanceof Number ||
                getPrevious() instanceof Variable ||
                getPrevious() instanceof Function ||
                getPrevious() instanceof LogicalBlock ||
                getPrevious() instanceof UnknownSyntaxUnitSequence ||
                getPrevious() instanceof UnknownSyntaxUnit ||
                getPrevious() instanceof Space) {
            isCompatible = true;
        } else {
            processNegativeCompatibilityWithPreviousSyntaxUnit();
        }

        isCompatible = processOperationAsLastSyntaxUnitInSyntaxContainerOrExpression(isCompatible);

        return isCompatible;
    }

    private boolean processOperationAsLastSyntaxUnitInSyntaxContainerOrExpression(boolean isCompatible) {
        int syntaxUnitPosition = getCurrent().getIndex();
        String syntaxUnitValue = getCurrent().getValue();
        String syntaxUnitName = getCurrent().name();

        int nextSyntaxUnitPosition = currentSyntaxUnitPosition + 1;
        if (currentSyntaxUnitPosition == syntaxUnits.size() - 1) {
            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxUnitPosition,
                    "Unexpected " + syntaxUnitName.toLowerCase() + " '" + syntaxUnitValue + "'",
                    syntaxUnitName + " can not be the last value in the expression or block"));
            isCompatible = false;
        } else if (syntaxUnits.get(nextSyntaxUnitPosition) instanceof Space &&
                nextSyntaxUnitPosition == syntaxUnits.size() - 1) {
            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxUnitPosition,
                    "Unexpected " + syntaxUnitName.toLowerCase() + " '" + syntaxUnitValue + "'",
                    syntaxUnitName + " can not be the last value in the expression or block"));
            isCompatible = false;
        }
        return isCompatible;
    }
}
