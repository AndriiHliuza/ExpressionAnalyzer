package org.pzks.units;

import org.pzks.parsers.ExpressionParser;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionParam extends SyntaxContainer {
    public FunctionParam(int index, List<String> units) throws Exception {
        super(index, units, false);
    }

    @Override
    public void processDetails() throws Exception {
        throw new OperationNotSupportedException("There are no details to process in the sequence on unknown units");
    }

    @Override
    public String toString() {
        return "FunctionParam{" +
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
        return "FunctionParam{" +
                "index='" + getIndex() + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public String name() {
        return "Function parameter";
    }

    @Override
    public String getValue() {
        return ExpressionParser.getExpressionAsString(getSyntaxUnits());
    }
}
