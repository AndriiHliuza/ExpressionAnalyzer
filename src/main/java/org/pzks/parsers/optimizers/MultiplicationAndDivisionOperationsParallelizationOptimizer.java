package org.pzks.parsers.optimizers;

import org.pzks.units.*;
import org.pzks.units.Number;

import java.util.List;

public class MultiplicationAndDivisionOperationsParallelizationOptimizer {

    public static void replaceDivisionWithMultiplication(List<SyntaxUnit> syntaxUnits) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);

            LogicalBlock logicalBlock = new LogicalBlock();
            logicalBlock.getSyntaxUnits().add(new Number(0, "1"));
            logicalBlock.getSyntaxUnits().add(new Operation(0, "/"));

            if (currentSyntaxUnit instanceof Operation && currentSyntaxUnit.getValue().equals("/")) {
                if (i + 1 < syntaxUnits.size()) {
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);

                    if (nextSyntaxUnit instanceof SyntaxContainer) {
                        replaceDivisionWithMultiplication(nextSyntaxUnit.getSyntaxUnits());
                    }

                    SyntaxUnit previousSyntaxUnit = syntaxUnits.get(i - 1);

                    if (previousSyntaxUnit instanceof Number && previousSyntaxUnit.getValue().matches("1|-1")) {
                        if (syntaxUnits.size() != 3) {
                            syntaxUnits.subList(i - 1, i + 2).clear();
                            logicalBlock.getSyntaxUnits().add(nextSyntaxUnit);
                            syntaxUnits.add(i - 1, logicalBlock);
                            i--;
                        }
                    } else {
                        currentSyntaxUnit.setValue("*");
                        logicalBlock.getSyntaxUnits().add(nextSyntaxUnit);
                        syntaxUnits.set(i + 1, logicalBlock);
                        i++;
                    }

                }
            } else if (currentSyntaxUnit instanceof SyntaxContainer) {
                replaceDivisionWithMultiplication(currentSyntaxUnit.getSyntaxUnits());
            }

        }
    }
}
