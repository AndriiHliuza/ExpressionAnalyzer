package org.pzks.analyzers.compatibility;

import org.pzks.units.SyntaxUnit;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;

public abstract class PlainSyntaxUnitCompatibilityAnalyzer extends SyntaxUnitCompatibilityAnalyzer {
    public PlainSyntaxUnitCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
    }

    public void processNegativeCompatibilityWithPreviousSyntaxUnit() {
        int syntaxUnitPosition = getCurrent().getIndex();
        String syntaxUnitValue = getCurrent().getValue();
        String syntaxUnitName = getCurrent().name();

        String previousSyntaxUnitValue = getPrevious().getValue();
        String previousSyntaxUnitName = getPrevious().name();

        getErrors().add(new SyntaxUnitErrorMessageBuilder(
                syntaxUnitPosition,
                "Unexpected " + syntaxUnitName.toLowerCase() + " '" + syntaxUnitValue + "'",
                syntaxUnitName + " can not be placed right after the " + previousSyntaxUnitName.toLowerCase() + " '" + previousSyntaxUnitValue + "'"));
    }
}
