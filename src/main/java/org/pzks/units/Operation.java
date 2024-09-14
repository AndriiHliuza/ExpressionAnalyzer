package org.pzks.units;

public class Operation extends SyntaxUnit {

    public Operation(int index, String value) {
        super(index, value);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        return "Operation{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() +
                '}';
    }
}
