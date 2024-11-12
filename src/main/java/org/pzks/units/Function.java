package org.pzks.units;

import org.pzks.parsers.ExpressionParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Function extends SyntaxContainer {

    public Function(int index, List<String> units) throws Exception {
        super(index, units, true);
    }

    @Override
    public SyntaxUnit parse() throws Exception {
        List<List<String>> functionParams = new ArrayList<>();
        List<String> functionParameterValues = new ArrayList<>();
        for (int i = 0; i < getBodyUnits().size(); i++) {
            String functionLogicalUnit = getBodyUnits().get(i);
            if (functionLogicalUnit.equals(",")) {
                int numberOfOpeningParenthesis = Collections.frequency(functionParameterValues, "(");
                int numberOfClosingParenthesis = Collections.frequency(functionParameterValues, ")");

                if (numberOfOpeningParenthesis == numberOfClosingParenthesis) {
                    functionParams.add(new ArrayList<>(functionParameterValues));
                    functionParameterValues.clear();
                } else {
                    functionParameterValues.add(functionLogicalUnit);
                }

                if (i == getBodyUnits().size() - 1) {
                    functionParams.add(new ArrayList<>());
                }
            } else if (i == getBodyUnits().size() - 1) {
                functionParameterValues.add(functionLogicalUnit);
                functionParams.add(new ArrayList<>(functionParameterValues));
                functionParameterValues.clear();
            } else {
                functionParameterValues.add(functionLogicalUnit);
            }
        }

        for (int i = 0; i < functionParams.size(); i++) {
            int offset = functionParams.subList(0, i).stream()
                    .flatMap(List::stream)
                    .mapToInt(String::length)
                    .sum();

            getSyntaxUnits().add(new FunctionParam(getSyntaxUnitIndex() + offset + i, functionParams.get(i)));
        }

        return this;
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

        if (joinedFunctionUnits.matches("^\\w+\\s*\\(.*\\)$")) {
            functionName = logicalUnits.getFirst();
            openingBracket = logicalUnits.get(1);
            closingBracket = logicalUnits.getLast();
        } else if (joinedFunctionUnits.matches("^\\w+\\s*\\(.*")) {
            functionName = logicalUnits.getFirst();
            openingBracket = logicalUnits.get(1);
        }

        getDetails().put("name", functionName.matches("\\w+\\s*") ? functionName : null);
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
        String body = String.join("", getBodyUnits());
        String value = name + openingBracket + body;

        if (closingBracket != null) {
            value += closingBracket;
        }

        return "Function{" +
                "index='" + getIndex() + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public String getValue() {
        return getDetails().get("name") + getDetails().get("openingBracket") + ExpressionParser.getExpressionAsString(getSyntaxUnits()) + getDetails().get("closingBracket");
    }
}
