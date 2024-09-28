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
                getPrevious() instanceof Space ||
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

    public void processBodyStatusInSyntaxContainer() {
        int syntaxUnitPosition = getCurrent().getIndex();
        String syntaxUnitValue = getCurrent().getValue();
        String syntaxUnitName = getCurrent().name();
        if (!getCurrent().getSyntaxUnits().isEmpty()) {
            if (getCurrent().getSyntaxUnits().size() == 1) {
                SyntaxUnit syntaxUnitInsideSyntaxContainer = getCurrent().getSyntaxUnits().getFirst();

                if (syntaxUnitInsideSyntaxContainer instanceof Space) {
                    processEmptyBodyErrorsForSyntaxContainer(syntaxUnitPosition, syntaxUnitName, syntaxUnitValue);
                } else if (syntaxUnitInsideSyntaxContainer instanceof FunctionParam functionParam) {
                    if ((functionParam.getSyntaxUnits().size() == 1
                            && !(functionParam.getSyntaxUnits().getFirst() instanceof Space)) ||
                            functionParam.getSyntaxUnits().size() > 1) {
                        getCurrent().analyze();
                        getErrors().addAll(getCurrent().getSyntaxUnitErrors());
                    }
                } else {
                    getCurrent().analyze();
                    getErrors().addAll(getCurrent().getSyntaxUnitErrors());
                }
            } else {
                getCurrent().analyze();
                getErrors().addAll(getCurrent().getSyntaxUnitErrors());
            }
        } else {
            processEmptyBodyErrorsForSyntaxContainer(syntaxUnitPosition, syntaxUnitName, syntaxUnitValue);
        }
    }

    private void processEmptyBodyErrorsForSyntaxContainer(int syntaxUnitPosition, String syntaxUnitName, String syntaxUnitValue) {
        if (getCurrent() instanceof LogicalBlock) {
            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxUnitPosition,
                    syntaxUnitName + " '" + syntaxUnitValue + "' is empty",
                    syntaxUnitName + " must have at least 1 value"
            ));
        } else if (getCurrent() instanceof FunctionParam) {
            getErrors().add(new SyntaxUnitErrorMessageBuilder(
                    syntaxUnitPosition,
                    "Function parameter is missing"
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
                    "Missing closing bracket for " + syntaxUnitName.toLowerCase() + " '" + syntaxUnitValue));
        }
    }
}
