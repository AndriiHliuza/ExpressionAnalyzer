package org.pzks.processing.parallelization;

import org.pzks.units.Number;
import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;
import org.pzks.units.Variable;
import org.pzks.utils.SyntaxUnitStructurePrinter;

import java.util.List;

public class ParallelProcessor {
    private List<SyntaxUnit> syntaxUnits;
    private String expression;

    public ParallelProcessor(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
        expression = SyntaxUnitStructurePrinter.getExpressionAsString(syntaxUnits);
    }

    public List<SyntaxUnit> getSyntaxUnits() {
        return syntaxUnits;
    }

    public void setSyntaxUnits(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void process() {
        splitToSteps(syntaxUnits);
    }

    public void splitToSteps(List<SyntaxUnit> syntaxUnits) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof Number) {
                if (i - 2 >= 0) {

                }
            } else if (syntaxUnit instanceof Variable) {

            } else if (syntaxUnit instanceof SyntaxContainer) {

            }
        }
    }
}
