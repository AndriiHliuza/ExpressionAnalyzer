package org.pzks.units;

import javax.naming.OperationNotSupportedException;
import java.util.stream.Collectors;

public class UnknownSyntaxUnitSequence extends SyntaxContainer {

    public UnknownSyntaxUnitSequence(int index, String value) {
        super(index, value);
    }

    @Override
    public void processDetails() throws Exception {
        throw new OperationNotSupportedException("There are no details to process in the sequence on unknown units");
    }

    @Override
    public String toString() {
        return "UnknownSyntaxUnitSequence{" +
                "index='" + getIndex() + '\'' +
                ", syntaxUnits=" + getSyntaxUnits() +
                ", value='" + getValue() + '\'' +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        String value = getSyntaxUnits().stream()
                .map(SyntaxUnit::getValue)
                .collect(Collectors.joining());
        return "UnknownSyntaxUnitSequence{" +
                "index='" + getIndex() + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public String name() {
        return "Unknown characters";
    }
}
