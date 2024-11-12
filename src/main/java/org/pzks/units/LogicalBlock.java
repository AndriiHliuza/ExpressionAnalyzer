package org.pzks.units;

import org.pzks.parsers.ExpressionParser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LogicalBlock extends SyntaxContainer {

    public LogicalBlock() {
        getDetails().put("openingBracket", "(");
        getDetails().put("closingBracket", ")");
    }

    public LogicalBlock(int index, List<String> units) throws Exception {
        super(index, units, true);
    }

    @Override
    public void processDetails() {
        List<String> logicalUnits = getLogicalUnits();
        String joinedBlockUnits = String.join("", logicalUnits);

        String openingBracket = "";
        String closingBracket = "";

        int numberOfOpeningParenthesis = Collections.frequency(logicalUnits, "(");
        int numberOfClosingParenthesis = Collections.frequency(logicalUnits, ")");

        if (joinedBlockUnits.matches("^\\(.*\\)$")) {
            openingBracket = logicalUnits.getFirst();
            closingBracket = logicalUnits.getLast();
        } else if (joinedBlockUnits.matches("^\\(.*")) {
            openingBracket = logicalUnits.getFirst();
        }

        getDetails().put("openingBracket", openingBracket.matches("\\(") ? openingBracket : null);

        if (numberOfOpeningParenthesis == numberOfClosingParenthesis && closingBracket.matches("\\)")) {
            getDetails().put("closingBracket", closingBracket);
        } else {
            getDetails().put("closingbracket", null);
        }

        if (logicalUnits.size() == 2 && getDetails().get("closingBracket") == null) {
            getBodyUnits().add(logicalUnits.getLast());
        } else if (logicalUnits.size() > 2 && getDetails().get("closingBracket") == null) {
            getBodyUnits().addAll(logicalUnits.subList(1, logicalUnits.size()));
        } else if (logicalUnits.size() > 2 && getDetails().get("closingBracket").matches("\\)")) {
            getBodyUnits().addAll(logicalUnits.subList(1, logicalUnits.size() - 1));
        }

        setLogicalUnits(getBodyUnits());

        String value = openingBracket + String.join("", getLogicalUnits()) + closingBracket;
        setValue(value);
    }

    @Override
    public String toString() {
        return "LogicalBlock{" +
                "index='" + getIndex() + '\'' +
                ", logicalUnits=" + getLogicalUnits() + '\'' +
                ", value='" + getDetails() + '\'' +
                ", syntaxUnits=" + getSyntaxUnits() +
                '}';
    }

    @Override
    public String treeUnitRepresentation() {
        String openingBracket = getDetails().get("openingBracket");
        String closingBracket = getDetails().get("closingBracket");
        String body = getSyntaxUnits().stream()
                .map(SyntaxUnit::getValue)
                .collect(Collectors.joining());

        String value = openingBracket + body;

        if (closingBracket != null) {
            value += closingBracket;
        }
        return "LogicalBlock{" +
                "index='" + getIndex() + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public String getValue() {
        return getDetails().get("openingBracket") + ExpressionParser.getExpressionAsString(getSyntaxUnits()) + getDetails().get("closingBracket");
    }
}
