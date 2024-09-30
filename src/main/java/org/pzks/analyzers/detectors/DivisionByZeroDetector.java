package org.pzks.analyzers.detectors;

import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;

import java.util.ArrayList;
import java.util.List;

public class DivisionByZeroDetector {
    private final List<SyntaxUnit> syntaxUnits;
    private final List<SyntaxUnitErrorMessageBuilder> errors = new ArrayList<>();

    public DivisionByZeroDetector(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
        detectDivisionByZero(this.syntaxUnits);
    }

    public List<SyntaxUnit> getSyntaxUnits() {
        return syntaxUnits;
    }

    public List<SyntaxUnitErrorMessageBuilder> getErrors() {
        return errors;
    }

    public void detectDivisionByZero(List<SyntaxUnit> syntaxUnits) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof Number && i != 0) {
                double currentNumber = Double.parseDouble(syntaxUnit.getValue());
                SyntaxUnit previousSyntaxUnit = syntaxUnits.get(i - 1);
                if (previousSyntaxUnit instanceof Operation && previousSyntaxUnit.getValue().equals("/") && currentNumber == 0) {
                    errors.add(new SyntaxUnitErrorMessageBuilder(previousSyntaxUnit.getIndex(), "Division by zero"));
                }
            } else if (syntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof LogicalBlock logicalBlock && logicalBlock.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInsideLogicalBlock = logicalBlock.getSyntaxUnits().getFirst();
                    if (syntaxUnitInsideLogicalBlock instanceof Number) {
                        double currentNumber = Double.parseDouble(syntaxUnit.getValue());
                        SyntaxUnit previousSyntaxUnit = syntaxUnits.get(i - 1);
                        if (previousSyntaxUnit instanceof Operation && previousSyntaxUnit.getValue().equals("/") && currentNumber == 0) {
                            errors.add(new SyntaxUnitErrorMessageBuilder(previousSyntaxUnit.getIndex(), "Division by zero"));
                        }
                    }
                } else {
                    detectDivisionByZero(syntaxUnit.getSyntaxUnits());
                }
            }
        }
    }
}
