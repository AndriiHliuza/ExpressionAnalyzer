package org.pzks.analyzers.compatibility;

import org.pzks.units.*;

public abstract class VarNumCompatibilityAnalyzer extends PlainSyntaxUnitCompatibilityAnalyzer {
    public VarNumCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
    }

    @Override
    public boolean isCompatibleWithPreviousSyntaxUnit() {
        boolean isCompatible = false;

        if (getPrevious() instanceof Operation ||
                getPrevious() instanceof UnknownSyntaxUnitSequence ||
                getPrevious() instanceof UnknownSyntaxUnit) {
            isCompatible = true;
        } else {
            processNegativeCompatibilityWithPreviousSyntaxUnit();
        }
        return isCompatible;
    }
}
