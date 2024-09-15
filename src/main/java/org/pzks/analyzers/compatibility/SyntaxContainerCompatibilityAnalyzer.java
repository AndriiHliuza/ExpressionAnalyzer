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

        if (getCurrent() == null || !(getCurrent() instanceof SyntaxContainer)) {
            return false;
        } else if (getPrevious() instanceof Operation ||
                getPrevious() instanceof UnknownSyntaxUnitSequence ||
                getPrevious() instanceof UnknownSyntaxUnit ||
                getPrevious() == null) {
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
        String syntaxUnitName = getCurrent().name();

        String previousSyntaxUnitValue = getPrevious().getValue();
        String previousSyntaxUnitName = getPrevious().name();

        getErrors().add(new SyntaxUnitErrorMessageBuilder(
                syntaxUnitPosition,
                "Unexpected " + syntaxUnitName.toLowerCase() + " '" + syntaxUnitValue + "'",
                syntaxUnitName + " can not be placed right after the " + previousSyntaxUnitName.toLowerCase() + " '" + previousSyntaxUnitValue + "'"
        ));
        processBodyStatusInSyntaxContainer();
    }

    private void processBodyStatusInSyntaxContainer() {
        int syntaxUnitPosition = getCurrent().getIndex();
        String syntaxUnitValue = getCurrent().getValue();
        String syntaxUnitName = getCurrent().name();

        if (!getCurrent().getSyntaxUnits().isEmpty()) {
            if (getCurrent().getSyntaxUnits().size() == 1 &&
                    getCurrent().getSyntaxUnits().getFirst() instanceof Space) {
                getErrors().add(new SyntaxUnitErrorMessageBuilder(
                        syntaxUnitPosition,
                        syntaxUnitName + " '" + syntaxUnitValue + "' is empty",
                        syntaxUnitName + " must have at least 1 value"
                ));
            } else {
                getCurrent().analyze();
                getErrors().addAll(getCurrent().getSyntaxUnitErrors());
            }
        } else {
            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxUnitPosition,
                    syntaxUnitName + " '" + syntaxUnitValue + "' is empty",
                    syntaxUnitName + " must have at least 1 value"
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
            String syntaxUnitName = getCurrent().name();

            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxContainerClosingBracketPosition,
                    "Missing closing bracket for " + syntaxUnitName.toLowerCase() + " '" + syntaxUnitValue + "' \n"));
        }
    }
}
