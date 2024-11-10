package org.pzks.parsers.math.laws;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class AssociativePropertyBasedSyntaxUnitProcessor {
    private List<SyntaxUnit> syntaxUnits;

    public AssociativePropertyBasedSyntaxUnitProcessor(SyntaxUnit syntaxUnit) {
        this.syntaxUnits = syntaxUnit.getSyntaxUnits();
        process(syntaxUnits);
    }

    private void process(List<SyntaxUnit> syntaxUnits) {

    }

    public List<SyntaxUnit> getProcessedSyntaxUnits() throws Exception {
        return new ArrayList<>();
    }
}
