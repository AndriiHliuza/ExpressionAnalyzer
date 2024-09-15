package org.pzks.fixers;

import org.pzks.units.SyntaxUnit;

import java.util.List;

public abstract class SyntaxUnitFixer {
    private final int currentUnitPositionInSyntaxUnitsList;
    private final SyntaxUnit currentSyntaxUnit;
    private SyntaxUnit previousSyntaxUnit;
    private final List<SyntaxUnit> syntaxUnits;
    private boolean isNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit;
    private boolean isSyntaxUnitRemoverFromSyntaxUnits;

    public SyntaxUnitFixer(int currentUnitPositionInSyntaxUnitsList, List<SyntaxUnit> syntaxUnits) {
        this.currentUnitPositionInSyntaxUnitsList = currentUnitPositionInSyntaxUnitsList;
        this.syntaxUnits = syntaxUnits;
        this.currentSyntaxUnit = syntaxUnits.get(currentUnitPositionInSyntaxUnitsList);

        if (currentUnitPositionInSyntaxUnitsList > 0) {
            this.previousSyntaxUnit = syntaxUnits.get(currentUnitPositionInSyntaxUnitsList - 1);
        }

    }

    public int getCurrentUnitPositionInSyntaxUnitsList() {
        return currentUnitPositionInSyntaxUnitsList;
    }

    public SyntaxUnit getCurrentSyntaxUnit() {
        return currentSyntaxUnit;
    }

    public SyntaxUnit getPreviousSyntaxUnit() {
        return previousSyntaxUnit;
    }

    public List<SyntaxUnit> getSyntaxUnits() {
        return syntaxUnits;
    }

    public boolean isNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit() {
        return isNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit;
    }

    public void setNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit(boolean newSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit) {
        isNewSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit = newSyntaxUnitAddedBetweenTheCurrentAndThePreviousSyntaxUnit;
    }

    public boolean isSyntaxUnitRemoverFromSyntaxUnits() {
        return isSyntaxUnitRemoverFromSyntaxUnits;
    }

    public void setSyntaxUnitRemoverFromSyntaxUnits(boolean syntaxUnitRemoverFromSyntaxUnits) {
        isSyntaxUnitRemoverFromSyntaxUnits = syntaxUnitRemoverFromSyntaxUnits;
    }

    public abstract void fix();
}
