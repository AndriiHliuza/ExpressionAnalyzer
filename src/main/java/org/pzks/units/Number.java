package org.pzks.units;

public class Number extends SyntaxUnit {
    public Number(int index, String value) {
        super(index, value);
    }

    @Override
    public String toString() {
        return "Number{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        return "Number{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() +
                '}';
    }
}
