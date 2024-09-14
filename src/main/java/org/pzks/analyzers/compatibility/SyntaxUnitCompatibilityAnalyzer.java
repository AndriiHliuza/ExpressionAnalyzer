package org.pzks.analyzers.compatibility;

import org.pzks.units.SyntaxUnit;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class SyntaxUnitCompatibilityAnalyzer {
    private SyntaxUnit previous;
    private SyntaxUnit current;
    private final List<SyntaxUnitErrorMessageBuilder> errors = new ArrayList<>();

    public SyntaxUnitCompatibilityAnalyzer(SyntaxUnit previous, SyntaxUnit current) {
        this.previous = previous;
        this.current = current;
    }

    public abstract boolean isCompatibleWithPreviousSyntaxUnit();

    public SyntaxUnit getPrevious() {
        return previous;
    }

    public SyntaxUnit getCurrent() {
        return current;
    }

    public void setPrevious(SyntaxUnit previous) {
        this.previous = previous;
    }

    public void setCurrent(SyntaxUnit current) {
        this.current = current;
    }

    public List<SyntaxUnitErrorMessageBuilder> getErrors() {
        return errors;
    }
}
