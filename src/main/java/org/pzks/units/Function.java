package org.pzks.units;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Function extends SyntaxContainer {

    public Function(int index, List<String> units) throws Exception {
        super(index, units);
    }

    @Override
    public void processDetails() {
        List<String> logicalUnits = getLogicalUnits();
        String joinedFunctionUnits = String.join("", logicalUnits);

        String functionName = "";
        String openingBracket = "";
        String closingBracket = "";

        int numberOfOpeningParenthesis = Collections.frequency(logicalUnits, "(");
        int numberOfClosingParenthesis = Collections.frequency(logicalUnits, ")");

        if (joinedFunctionUnits.matches("^\\w+\\(.*\\)$")) {
            functionName = logicalUnits.getFirst();
            openingBracket = logicalUnits.get(1);
            closingBracket = logicalUnits.getLast();
        } else if (joinedFunctionUnits.matches("^\\w+\\(.*")) {
            functionName = logicalUnits.getFirst();
            openingBracket = logicalUnits.get(1);
        }

        getDetails().put("name", functionName.matches("\\w+") ? functionName : null);
        getDetails().put("openingBracket", openingBracket.matches("\\(") ? openingBracket : null);

        if (numberOfOpeningParenthesis == numberOfClosingParenthesis && closingBracket.matches("\\)")) {
            getDetails().put("closingBracket", closingBracket);
        } else {
            getDetails().put("closingBracket", null);
        }

        if (logicalUnits.size() == 3 && getDetails().get("closingBracket") == null) {
            getBodyUnits().add(logicalUnits.getLast());
        } else if (logicalUnits.size() > 3 && getDetails().get("closingBracket") == null) {
            getBodyUnits().addAll(logicalUnits.subList(2, logicalUnits.size()));
        } else if (logicalUnits.size() > 3 && getDetails().get("closingBracket").matches("\\)")) {
            getBodyUnits().addAll(logicalUnits.subList(2, logicalUnits.size() - 1));
        }

        setLogicalUnits(getBodyUnits());

        String value = functionName + openingBracket + String.join("", getLogicalUnits()) + closingBracket;
        setValue(value);
    }

    @Override
    public String toString() {
        return "Function{" +
                "index='" + getIndex() + '\'' +
                ", logicalUnits=" + getLogicalUnits() + '\'' +
                ", value='" + getDetails() + '\'' +
                ", syntaxUnits=" + getSyntaxUnits() +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        String name = getDetails().get("name");
        String openingBracket = getDetails().get("openingBracket");
        String closingBracket = getDetails().get("closingBracket");
        String body = getSyntaxUnits().stream()
                .map(SyntaxUnit::getValue)
                .collect(Collectors.joining());

        String value = name + openingBracket + body;

        if (closingBracket != null) {
            value += closingBracket;
        }

        return "Function{" +
                "index='" + getIndex() + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
