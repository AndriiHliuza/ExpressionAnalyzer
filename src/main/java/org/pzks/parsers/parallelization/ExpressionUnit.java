package org.pzks.parsers.parallelization;

import org.pzks.units.SyntaxUnit;

import java.util.ArrayList;
import java.util.List;

public class ExpressionUnit implements Cloneable {
    private ExpressionUnit leftParentExpressionUnit;
    private ExpressionUnit rightParentExpressionUnit;
    private List<SyntaxUnit> syntaxUnits = new ArrayList<>();

    public List<SyntaxUnit> getSyntaxUnits() {
        return syntaxUnits;
    }

    public void setSyntaxUnits(List<SyntaxUnit> syntaxUnits) {
        this.syntaxUnits = syntaxUnits;
    }

    public ExpressionUnit getLeftParentExpressionUnit() {
        return leftParentExpressionUnit;
    }

    public void setLeftParentExpressionUnit(ExpressionUnit leftParentExpressionUnit) {
        this.leftParentExpressionUnit = leftParentExpressionUnit;
    }

    public ExpressionUnit getRightParentExpressionUnit() {
        return rightParentExpressionUnit;
    }

    public void setRightParentExpressionUnit(ExpressionUnit rightParentExpressionUnit) {
        this.rightParentExpressionUnit = rightParentExpressionUnit;
    }

    public void addSyntaxUnit(SyntaxUnit syntaxUnit) {
        syntaxUnits.add(syntaxUnit);
    }

    public void clear() {
        syntaxUnits.clear();
    }

    public SyntaxUnit getLastOperation() {
        return syntaxUnits.get(syntaxUnits.size() - 2);
    }

    @Override
    public String toString() {
        return "ExpressionUnit{" +
                "syntaxUnits=" + syntaxUnits +
                '}';
    }

    @Override
    protected ExpressionUnit clone() throws CloneNotSupportedException {
        ExpressionUnit clone = (ExpressionUnit) super.clone();
        clone.syntaxUnits = new ArrayList<>(syntaxUnits);
        return clone;
    }
}
