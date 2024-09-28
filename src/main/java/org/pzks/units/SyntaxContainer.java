package org.pzks.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SyntaxContainer extends SyntaxUnit {
    private HashMap<String, String> details = new HashMap<>();
    private List<String> bodyUnits = new ArrayList<>();

    public SyntaxContainer(int index, List<String> units, boolean shouldProcessDetails) throws Exception {
        super(index, units);
        if (shouldProcessDetails) {
            processDetails();
        }
        if (!getLogicalUnits().isEmpty()) {
            parse();
        }
    }

    public SyntaxContainer(int index, String value) {
        super(index, value);
    }

    public HashMap<String, String> getDetails() {
        return details;
    }

    public List<String> getBodyUnits() {
        return bodyUnits;
    }

    public abstract void processDetails() throws Exception;
}
