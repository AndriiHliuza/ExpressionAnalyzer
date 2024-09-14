package org.pzks.analyzers.compatibility;

import org.pzks.units.*;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;

public class SyntaxContainerCompatibilityAnalyzer extends SyntaxUnitCompatibilityAnalyzer {
    public SyntaxContainerCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        super(previous, current);
    }

    @Override
    public boolean isCompatibleWithPreviousSyntaxUnit() {
        boolean isCompatible = false;

        if (getCurrent() == null || getPrevious() == null || !(getCurrent() instanceof SyntaxContainer)) {
            return false;
        } else if ((getPrevious() instanceof Operation ||
                getPrevious() instanceof UnknownSyntaxUnitSequence ||
                getPrevious() instanceof UnknownSyntaxUnit) && !getCurrent().getSyntaxUnits().isEmpty()) {
            processBodyStatusInSyntaxContainer();
        } else {
            processNegativeCompatibilityWithPreviousSyntaxUnit();
        }

        processClosingBracketStatus();

        if (getErrors().isEmpty()) {
            isCompatible = true;
        }

        return isCompatible;
    }

    private void processNegativeCompatibilityWithPreviousSyntaxUnit() {
        int syntaxUnitPosition = getCurrent().getIndex();
        String syntaxUnitValue = getCurrent().getValue();
        String syntaxUnitClassName = getCurrent().getClass().getSimpleName();

        String previousSyntaxUnitValue = getPrevious().getValue();
        String previousSyntaxUnitClassName = getPrevious().getClass().getSimpleName();

        getErrors().add(new SyntaxUnitErrorMessageBuilder(
                syntaxUnitPosition,
                "Unexpected " + syntaxUnitClassName.toLowerCase() + " '" + syntaxUnitValue + "'",
                syntaxUnitClassName + " can not be placed right after the " + previousSyntaxUnitClassName.toLowerCase() + " '" + previousSyntaxUnitValue + "'"
        ));
        processBodyStatusInSyntaxContainer();
    }

    private void processBodyStatusInSyntaxContainer() {
        if (!getCurrent().getSyntaxUnits().isEmpty()) {
            getCurrent().analyze();
            getErrors().addAll(getCurrent().getSyntaxUnitErrors());
        } else {
            int syntaxUnitPosition = getCurrent().getIndex();
            String syntaxUnitValue = getCurrent().getValue();
            String syntaxUnitClassName = getCurrent().getClass().getSimpleName();

            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxUnitPosition,
                    syntaxUnitClassName + " '" + syntaxUnitValue + "' is empty",
                    syntaxUnitClassName + " must have at least 1 value"
            ));
        }
    }

    private void processClosingBracketStatus() {
        String syntaxContainerName = ((SyntaxContainer) getCurrent()).getDetails().get("name");
        String syntaxContainerOpeningBracket = ((SyntaxContainer) getCurrent()).getDetails().get("openingBracket");
        String syntaxContainerClosingBracket = ((SyntaxContainer) getCurrent()).getDetails().get("closingBracket");

        if (syntaxContainerClosingBracket == null) {
            int syntaxContainerNameLength = 0;
            if (syntaxContainerName != null) {
                syntaxContainerNameLength = syntaxContainerName.length();
            }
            int syntaxContainerOpeningBracketLength = syntaxContainerOpeningBracket.length();
            int syntaxContainerBodyLength = String.join("", getCurrent().getLogicalUnits()).length();

            int syntaxContainerClosingBracketPosition = getCurrent().getIndex() + syntaxContainerNameLength + syntaxContainerOpeningBracketLength + syntaxContainerBodyLength;
            String syntaxUnitValue = getCurrent().getValue();
            String syntaxUnitClassName = getCurrent().getClass().getSimpleName();

            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxContainerClosingBracketPosition,
                    "Missing closing bracket for " + syntaxUnitClassName.toLowerCase() + " '" + syntaxUnitValue + "' \n"));
        }
    }
}
