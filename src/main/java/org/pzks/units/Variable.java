package org.pzks.units;

public class Variable extends SyntaxUnit {

    public Variable(int index, String value) {
        super(index, value);
    }

    @Override
    public String toString() {
        return "Variable{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        return "Variable{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
