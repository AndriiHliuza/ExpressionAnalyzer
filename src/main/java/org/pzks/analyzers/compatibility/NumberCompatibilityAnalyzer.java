package org.pzks.analyzers.compatibility;

import org.pzks.units.*;
import org.pzks.units.Number;

public class NumberCompatibilityAnalyzer extends VarNumCompatibilityAnalyzer {
    public NumberCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
    }

    @Override
    public boolean isCompatibleWithPreviousSyntaxUnit() {
        if (getCurrent() == null || getPrevious() == null || !(getCurrent() instanceof Number)) {
            return false;
        }
        return super.isCompatibleWithPreviousSyntaxUnit();
    }
}
