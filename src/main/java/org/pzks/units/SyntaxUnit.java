package org.pzks.units;

import org.pzks.analyzers.compatibility.*;
import org.pzks.utils.BasicExpressionUnitRecognizer;
import org.pzks.analyzers.SyntaxAnalyzer;
import org.pzks.builders.SyntaxUnitBuilderFactory;
import org.pzks.parsers.SyntaxUnitParser;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyntaxUnit implements SyntaxUnitParser, SyntaxAnalyzer {
    private int index;
    private List<String> logicalUnits;
    private String value;
    private List<SyntaxUnit> syntaxUnits = new ArrayList<>();
    private final List<SyntaxUnitErrorMessageBuilder> syntaxUnitErrors = new ArrayList<>();
    private final BasicExpressionUnitRecognizer basicExpressionUnitRecognizer = new BasicExpressionUnitRecognizer();
    private final SyntaxUnitBuilderFactory syntaxUnitBuilderFactory = new SyntaxUnitBuilderFactory();

    public SyntaxUnit(int index, List<String> units) {
        this.index = index;
        logicalUnits = units;
    }

    public SyntaxUnit(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getLogicalUnits() {
        return logicalUnits;
    }

    public void setLogicalUnits(List<String> logicalUnits) {
        this.logicalUnits = logicalUnits;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<SyntaxUnit> getSyntaxUnits() {
        return syntaxUnits;
    }

    public void setSyntaxUnits(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
    }

    public List<SyntaxUnitErrorMessageBuilder> getSyntaxUnitErrors() {
        return syntaxUnitErrors;
    }

    @Override
    public String toString() {
        return "SyntaxUnit{" +
                "index='" + index + '\'' +
                ", logicalUnits=" + logicalUnits + '\'' +
                ", value='" + value + '\'' +
                ", syntaxUnits=" + syntaxUnits +
                '}';
    }

    public String treeUnitRepresentation() {
        return "SyntaxUnit{" +
                "index='" + index + '\'' +
                ", value='" + value +
                '}';
    }

    public String name() {
        return getClass().getSimpleName();
    }

    @Override
    public SyntaxUnit parse() throws Exception {
        int syntaxUnitIndex = getSyntaxUnitIndex();

        for (int i = 0; i < logicalUnits.size(); i++) {
            String logicalUnit = logicalUnits.get(i);
            if (basicExpressionUnitRecognizer.isValidAlphaNumericNaming(logicalUnit)) {
                if (i < (logicalUnits.size() - 1)) {
                    i = syntaxUnitBuilderFactory
                            .getFunctionBuilder(syntaxUnits, logicalUnits, logicalUnit, i, syntaxUnitIndex)
                            .build();
                } else {
                    syntaxUnits.add(new Variable(syntaxUnitIndex, logicalUnit));
                }
            } else if (basicExpressionUnitRecognizer.isValidAlphaNumericNaming(logicalUnit)) {
                syntaxUnits.add(new Variable(syntaxUnitIndex, logicalUnit));
            } else if (basicExpressionUnitRecognizer.isOperation(logicalUnit)) {
                syntaxUnits.add(new Operation(syntaxUnitIndex, logicalUnit));
            } else if (basicExpressionUnitRecognizer.isFloatNumber(logicalUnit)) {
                syntaxUnits.add(new Number(syntaxUnitIndex, logicalUnit));
            } else if (logicalUnit.matches("\\(")) {
                i = syntaxUnitBuilderFactory
                        .getLogicalBlockBuilder(syntaxUnits, logicalUnits, logicalUnit, i, syntaxUnitIndex)
                        .build();
            } else if (logicalUnit.matches("\\s+")) {
                syntaxUnits.add(new Space(syntaxUnitIndex, logicalUnit));
            } else {
                syntaxUnits.add(new UnknownSyntaxUnit(syntaxUnitIndex, logicalUnit));
            }

            SyntaxUnit lastSyntaxUnit = syntaxUnits.getLast();
            syntaxUnitIndex = processNextSyntaxUnitIndex(syntaxUnitIndex, lastSyntaxUnit);
        }
        return this;
    }

    public int getSyntaxUnitIndex() {
        int currentSyntaxUnitIndex = getIndex();
        if (this instanceof Function || this instanceof LogicalBlock) {
            SyntaxContainer syntaxContainer = (SyntaxContainer) this;
            Map<String, String> syntaxContainerDetails = syntaxContainer.getDetails();
            String name = "";
            String openingBracket = syntaxContainerDetails.get("openingBracket");
            if (this instanceof Function) {
                name = syntaxContainerDetails.get("name");
            }
            currentSyntaxUnitIndex += name.length() + openingBracket.length();
        }
        return currentSyntaxUnitIndex;
    }

    private int processNextSyntaxUnitIndex(int currentSyntaxUnitIndex, SyntaxUnit currentSyntaxUnit) {
        int nextSyntaxUnitIndex = currentSyntaxUnitIndex;
        if (currentSyntaxUnit instanceof SyntaxContainer syntaxContainer) {
            String syntaxContainerJoinedLogicalUnits = String.join("", syntaxContainer.getLogicalUnits());
            int syntaxContainerDetailsLength = syntaxContainer.getDetails()
                    .values()
                    .stream()
                    .mapToInt(value -> value == null ? 0 : value.length())
                    .sum();
            nextSyntaxUnitIndex += syntaxContainerJoinedLogicalUnits.length() + syntaxContainerDetailsLength;
        } else {
            nextSyntaxUnitIndex += currentSyntaxUnit.value.length();
        }
        return nextSyntaxUnitIndex;
    }

    @Override
    public void analyze() {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            boolean isCompatibleWithPreviousSyntaxUnit = false;

            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if ((syntaxUnit instanceof Operation ||
                    syntaxUnit instanceof UnknownSyntaxUnitSequence ||
                    syntaxUnit instanceof UnknownSyntaxUnit) && i == 0) {
                processInvalidFirstSyntaxUnitInsideAnotherSyntaxUnit(syntaxUnit);
            } else {
                SyntaxUnitCompatibilityAnalyzer syntaxUnitCompatibilityAnalyzer = null;
                SyntaxUnit previousSyntaxUnit = getPreviousSyntaxUnit(i, syntaxUnit);

                if (syntaxUnit instanceof Variable) {
                    syntaxUnitCompatibilityAnalyzer = new VariableCompatibilityAnalyzer(previousSyntaxUnit, syntaxUnit);
                    isCompatibleWithPreviousSyntaxUnit = syntaxUnitCompatibilityAnalyzer.isCompatibleWithPreviousSyntaxUnit();
                } else if (syntaxUnit instanceof Number) {
                    syntaxUnitCompatibilityAnalyzer = new NumberCompatibilityAnalyzer(previousSyntaxUnit, syntaxUnit);
                    isCompatibleWithPreviousSyntaxUnit = syntaxUnitCompatibilityAnalyzer.isCompatibleWithPreviousSyntaxUnit();
                } else if (syntaxUnit instanceof Operation) {
                    syntaxUnitCompatibilityAnalyzer = new OperationCompatibilityAnalyzer(previousSyntaxUnit, syntaxUnit, i, syntaxUnits);
                    isCompatibleWithPreviousSyntaxUnit = syntaxUnitCompatibilityAnalyzer.isCompatibleWithPreviousSyntaxUnit();
                } else if (syntaxUnit instanceof FunctionParam) {
                    syntaxUnitCompatibilityAnalyzer = new FunctionParamCompatibilityAnalyzer(previousSyntaxUnit, syntaxUnit);
                    isCompatibleWithPreviousSyntaxUnit = syntaxUnitCompatibilityAnalyzer.isCompatibleWithPreviousSyntaxUnit();
                } else if (syntaxUnit instanceof UnknownSyntaxUnitSequence ||
                        syntaxUnit instanceof UnknownSyntaxUnit) {
                    processUnknownValues(syntaxUnit);
                } else if (syntaxUnit instanceof SyntaxContainer) {
                    syntaxUnitCompatibilityAnalyzer = new SyntaxContainerCompatibilityAnalyzer(previousSyntaxUnit, syntaxUnit);
                    isCompatibleWithPreviousSyntaxUnit = syntaxUnitCompatibilityAnalyzer.isCompatibleWithPreviousSyntaxUnit();
                } else if (syntaxUnit instanceof Space) {
                    continue;
                } else {
                    processUnknownValues(syntaxUnit);
                }

                if (syntaxUnitCompatibilityAnalyzer != null &&
                        !isCompatibleWithPreviousSyntaxUnit &&
                        !syntaxUnitCompatibilityAnalyzer.getErrors().isEmpty()
                ) {
                    List<SyntaxUnitErrorMessageBuilder> processedErrors = syntaxUnitCompatibilityAnalyzer.getErrors();
                    syntaxUnitErrors.addAll(processedErrors);
                }
            }
        }
    }

    private SyntaxUnit getPreviousSyntaxUnit(int currentSyntaxUnitPositionInList, SyntaxUnit currentSyntaxUnit) {
        SyntaxUnit previousSyntaxUnit = null;
        if (currentSyntaxUnitPositionInList > 0) {
            previousSyntaxUnit = syntaxUnits.get(currentSyntaxUnitPositionInList - 1);
        }

        if (previousSyntaxUnit instanceof Space) {
            if (currentSyntaxUnitPositionInList > 1) {
                previousSyntaxUnit = syntaxUnits.get(currentSyntaxUnitPositionInList - 2);
            } else if (currentSyntaxUnit instanceof Operation ||
                    currentSyntaxUnit instanceof UnknownSyntaxUnitSequence ||
                    currentSyntaxUnit instanceof UnknownSyntaxUnit) {
                processInvalidFirstSyntaxUnitInsideAnotherSyntaxUnit(currentSyntaxUnit);
            }
        }
        return previousSyntaxUnit;
    }


    private void processInvalidFirstSyntaxUnitInsideAnotherSyntaxUnit(SyntaxUnit syntaxUnit) {
        int syntaxUnitPosition = syntaxUnit.getIndex();
        String syntaxUnitValue = syntaxUnit.getValue();
        syntaxUnitErrors.add(new SyntaxUnitErrorMessageBuilder(
                syntaxUnitPosition,
                "Unexpected value: '" + syntaxUnitValue + "'",
                "The provided character can not be placed at the beginning of the expression or block"
        ));
    }

    private void processUnknownValues(SyntaxUnit syntaxUnit) {
        int syntaxUnitPosition = syntaxUnit.getIndex();
        String syntaxUnitValue = syntaxUnit.getValue();
        syntaxUnitErrors.add(new SyntaxUnitErrorMessageBuilder(
                syntaxUnitPosition,
                "Unexpected value '" + syntaxUnitValue + "'",
                "Unknown value detected. Value " + syntaxUnitValue + " can not be recognized"));
    }
}
