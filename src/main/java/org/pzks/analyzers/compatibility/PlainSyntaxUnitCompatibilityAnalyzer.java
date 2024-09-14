package org.pzks.analyzers.compatibility;

import org.pzks.units.SyntaxUnit;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;

public abstract class PlainSyntaxUnitCompatibilityAnalyzer extends SyntaxContainerCompatibilityAnalyzer {
    public PlainSyntaxUnitCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
    }

    public void processNegativeCompatibilityWithPreviousSyntaxUnit() {
        int syntaxUnitPosition = getCurrent().getIndex();
        String syntaxUnitValue = getCurrent().getValue();
        String syntaxUnitClassName = getCurrent().getClass().getSimpleName();

        String previousSyntaxUnitValue = getPrevious().getValue();
        String previousSyntaxUnitClassName = getPrevious().getClass().getSimpleName();


        getErrors().add(new SyntaxUnitErrorMessageBuilder(
                syntaxUnitPosition,
                "Unexpected " + syntaxUnitClassName.toLowerCase() + " '" + syntaxUnitValue + "'",
                syntaxUnitClassName + " can not be placed right after the " + previousSyntaxUnitClassName.toLowerCase() + " '" + previousSyntaxUnitValue + "'"));
    }
}
