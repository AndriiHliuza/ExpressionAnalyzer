package org.pzks.units;

public class UnknownSyntaxUnit extends SyntaxUnit {

    public UnknownSyntaxUnit(int index, String value) {
        super(index, value);
    }

    @Override
    public String toString() {
        return "UnKnownSyntaxUnit{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        return "UnknownSyntaxUnit{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }

    @Override
    public String name() {
        return "Unknown character";
    }
}
