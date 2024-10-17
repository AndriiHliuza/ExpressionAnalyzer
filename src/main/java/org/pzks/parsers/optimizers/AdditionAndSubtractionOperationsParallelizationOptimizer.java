package org.pzks.parsers.optimizers;

import org.pzks.units.*;
import org.pzks.units.Number;

import java.util.List;

public class AdditionAndSubtractionOperationsParallelizationOptimizer {

    public static void replaceSubtractionWithAddition(List<SyntaxUnit> syntaxUnits) throws Exception {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit currentSyntaxUnit = syntaxUnits.get(i);

            LogicalBlock logicalBlock = new LogicalBlock();
            logicalBlock.getSyntaxUnits().add(new Number(0, "-1"));

            if (currentSyntaxUnit instanceof Operation && currentSyntaxUnit.getValue().equals("-")) {
                if (i + 1 < syntaxUnits.size()) {
                    SyntaxUnit nextSyntaxUnit = syntaxUnits.get(i + 1);

                    if (nextSyntaxUnit instanceof SyntaxContainer) {
                        replaceSubtractionWithAddition(nextSyntaxUnit.getSyntaxUnits());
                    }


                    if (syntaxUnits.size() == 2) {
                        syntaxUnits.set(i, new Number(0, "-1"));
                        syntaxUnits.add(i + 1, new Operation(0, "*"));
                    } else {
                        if (i == 0) {
                            currentSyntaxUnit.setValue("*");
                            syntaxUnits.addFirst(logicalBlock);
                        } else {
                            currentSyntaxUnit.setValue("+");
                            syntaxUnits.add(i + 1, logicalBlock);
                            syntaxUnits.add(i + 2, new Operation(0, "*"));
                        }
                    }
                }
            } else if (currentSyntaxUnit instanceof SyntaxContainer) {
                replaceSubtractionWithAddition(currentSyntaxUnit.getSyntaxUnits());
            }
        }
    }
}
