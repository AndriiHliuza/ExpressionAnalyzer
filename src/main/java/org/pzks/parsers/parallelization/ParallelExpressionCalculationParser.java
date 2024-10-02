package org.pzks.parsers.parallelization;

import org.pzks.units.*;
import org.pzks.units.Number;

import java.util.ArrayList;
import java.util.List;

public class ParallelExpressionCalculationParser {
    private final SyntaxUnit rootSyntaxUnit;

    public ParallelExpressionCalculationParser(SyntaxUnit rootSyntaxUnit) {
        this.rootSyntaxUnit = rootSyntaxUnit;
    }

    public void buildParallelTree() throws Exception{
        List<SyntaxUnit> syntaxUnits = rootSyntaxUnit.getSyntaxUnits();
        buildCalculationSteps(syntaxUnits);

    }

    public List<CalculationStep> buildCalculationSteps(List<SyntaxUnit> syntaxUnits) throws Exception {
        List<ExpressionUnit> expressionUnitsForStep = new ArrayList<>();
        addExpressionUnitForStep(expressionUnitsForStep, syntaxUnits);
        expressionUnitsForStep.forEach(System.out::println);
        return null;
    }

    public void addExpressionUnitForStep(List<ExpressionUnit> expressionUnitsForStep, List<SyntaxUnit> syntaxUnits) throws Exception {
        ExpressionUnit expressionUnit = new ExpressionUnit();
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof FunctionParam functionParam) {
                List<SyntaxUnit> functionParamSyntaxUnits = functionParam.getSyntaxUnits();
                addExpressionUnitForStep(expressionUnitsForStep, functionParamSyntaxUnits);
            } else {
                if (i % 2 == 1 && syntaxUnit instanceof Operation operation) {
                    SyntaxUnit previousSyntaxUnit = syntaxUnits.get(i - 1);
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);
                    if (nextSyntaxUnit instanceof SyntaxContainer syntaxContainer) {
//                        if (syntaxContainer.getSyntaxUnits().isEmpty() || syntaxContainer.getSyntaxUnits().size() == 1) {
//                            ExpressionUnit syntaxCo
//                        }
                        List<SyntaxUnit> syntaxContainerSyntaxUnits = syntaxContainer.getSyntaxUnits();
                        addExpressionUnitForStep(expressionUnitsForStep, syntaxContainerSyntaxUnits);
                    } else {
                        if (expressionUnit.getSyntaxUnits().isEmpty()) {
                            expressionUnit.addSyntaxUnit(previousSyntaxUnit);
                            expressionUnit.addSyntaxUnit(operation);
                            expressionUnit.addSyntaxUnit(nextSyntaxUnit);
                        } else {
                            SyntaxUnit previousOperationInExpressionUnit = expressionUnit.getLastOperation();

                            if ((operation.getValue().equals("/") || operation.getValue().equals("-")) &&
                                    operation.getValue().equals(previousOperationInExpressionUnit.getValue())) {
                                expressionUnit.addSyntaxUnit(operation);
                                expressionUnit.addSyntaxUnit(nextSyntaxUnit);
                            } else {
                                expressionUnitsForStep.add(expressionUnit.clone());
                                expressionUnit.clear();
                            }
                        }
                    }
                }
            }
        }

    }
}
