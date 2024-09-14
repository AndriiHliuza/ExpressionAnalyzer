package org.pzks.analyzers.compatibility;

import org.pzks.units.*;
import org.pzks.units.Number;

public class OperationCompatibilityAnalyzer extends PlainSyntaxUnitCompatibilityAnalyzer {
    public OperationCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
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
                        getPrevious() instanceof UnknownSyntaxUnit) {
            isCompatible = true;
        } else {
            processNegativeCompatibilityWithPreviousSyntaxUnit();
        }
        return isCompatible;
    }
}
