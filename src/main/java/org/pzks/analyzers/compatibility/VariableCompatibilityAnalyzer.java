package org.pzks.analyzers.compatibility;

import org.pzks.units.*;

public class VariableCompatibilityAnalyzer extends VarNumCompatibilityAnalyzer {
    public VariableCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
    }

    @Override
    public boolean isCompatibleWithPreviousSyntaxUnit() {
        if (getCurrent() == null || getPrevious() == null || !(getCurrent() instanceof Variable)) {
            return false;
        }
        return super.isCompatibleWithPreviousSyntaxUnit();
    }
}
