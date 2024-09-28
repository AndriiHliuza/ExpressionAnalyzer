package org.pzks.analyzers.compatibility;

import org.pzks.units.*;

public class FunctionParamCompatibilityAnalyzer extends SyntaxContainerCompatibilityAnalyzer {
    public FunctionParamCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
    }

    @Override
    public boolean isCompatibleWithPreviousSyntaxUnit() {
        boolean isCompatible = false;

        if (getCurrent() == null || !(getCurrent() instanceof FunctionParam)) {
            return false;
        }

        processBodyStatusInSyntaxContainer();

        if (getErrors().isEmpty()) {
            isCompatible = true;
        }

        return isCompatible;
    }
}
