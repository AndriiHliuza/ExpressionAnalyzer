package org.pzks.fixers;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.*;
import org.pzks.units.Number;

import java.util.List;

public class ExpressionFixer {
    private final List<SyntaxUnit> syntaxUnits;

    public ExpressionFixer(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        fix();
    }

    public ExpressionFixer(List<SyntaxUnit> syntaxUnits) throws Exception {
        this.syntaxUnits = syntaxUnits;
        fix();
    }

    public SyntaxUnit getFixedSyntaxUnit() throws Exception {
        return ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits));
    }

    private void fix() throws Exception {
        removeUnknownSyntaxUnitsOrSpaces(syntaxUnits);
        createValidExpression();
    }

    private void removeUnknownSyntaxUnitsOrSpaces(List<SyntaxUnit> syntaxUnits) {
        syntaxUnits.removeIf(
                syntaxUnit -> syntaxUnit instanceof UnknownSyntaxUnitSequence ||
                        syntaxUnit instanceof UnknownSyntaxUnit ||
                        syntaxUnit instanceof Space
        );

        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            if (syntaxUnit instanceof SyntaxContainer) {
                removeUnknownSyntaxUnitsOrSpaces(syntaxUnit.getSyntaxUnits());
            }
        }
    }

    private void createValidExpression() throws Exception {
        for (int i = 0; i < syntaxUnits.size(); i++) {

            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            SyntaxUnitFixer syntaxUnitFixer = null;

            if (syntaxUnit instanceof Variable || syntaxUnit instanceof Number) {
                syntaxUnitFixer = new VarNumFixer(i, syntaxUnits);
                syntaxUnitFixer.fix();
            } else if (syntaxUnit instanceof Operation) {
                syntaxUnitFixer = new OperationFixer(i, syntaxUnits);
                syntaxUnitFixer.fix();
            } else if (syntaxUnit instanceof SyntaxContainer) {
                syntaxUnitFixer = new SyntaxContainerFixer(i, syntaxUnits);
                syntaxUnitFixer.fix();
            }  else {
                syntaxUnits.remove(syntaxUnit);
                i--;
            }

            if (syntaxUnitFixer != null && syntaxUnitFixer.isNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit()) {
                i++;
            } else if (syntaxUnitFixer != null && syntaxUnitFixer.isSyntaxUnitRemovedFromSyntaxUnits()) {
                i--;
            }
        }
    }

}
