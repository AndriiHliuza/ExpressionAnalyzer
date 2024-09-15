package org.pzks.units;

public class Space extends SyntaxUnit {
    public Space(int index, String value) {
        super(index, value);
    }

    @Override
    public String toString() {
        return "Space{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        return "Space{" +
                "index='" + getIndex() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
