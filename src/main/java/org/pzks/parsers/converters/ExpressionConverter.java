package org.pzks.parsers.converters;

import org.pzks.parsers.CharSequenceNode;
import org.pzks.parsers.CharSequenceParser;
import org.pzks.parsers.LogicalUnitParser;
import org.pzks.parsers.UnknownSyntaxUnitsParser;
import org.pzks.units.FunctionParam;
import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;

import java.util.Arrays;
import java.util.List;

// todo code was moved from ExpressionProcessor. Code is still in expression parser but should be moved here and deleted from ExpressionProcessor
public class ExpressionConverter {
    public static SyntaxUnit convertExpressionToParsedSyntaxUnit(String expression) throws Exception {
        expression = expression.trim();
        List<String> logicalUnits = getLogicalUnits(expression);
        SyntaxUnit syntaxUnit = new SyntaxUnit(0, logicalUnits);
        SyntaxUnit parsedSyntaxUnit = syntaxUnit.parse();
        UnknownSyntaxUnitsParser unknownSyntaxUnitsParser = new UnknownSyntaxUnitsParser(parsedSyntaxUnit);
        parsedSyntaxUnit = unknownSyntaxUnitsParser.parse();
        return parsedSyntaxUnit;
    }

    public static String getExpressionAsString(List<SyntaxUnit> syntaxUnits) {
        StringBuilder expression = new StringBuilder();
        addSyntaxUnitsToString(syntaxUnits, expression);
        return expression.toString().replaceAll("\\s+", "");
    }

    private static void addSyntaxUnitsToString(List<SyntaxUnit> syntaxUnits, StringBuilder expression) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof SyntaxContainer syntaxContainer) {
                if (syntaxContainer instanceof FunctionParam functionParam) {
                    addSyntaxUnitsToString(functionParam.getSyntaxUnits(), expression);
                    if (i != syntaxUnits.size() - 1) {
                        expression.append(",");
                    }
                } else {
                    String openingBracket = syntaxContainer.getDetails().get("openingBracket");
                    String closingBracket = syntaxContainer.getDetails().get("closingBracket");
                    String functionName = "";
                    String name = syntaxContainer.getDetails().get("name");
                    if (name != null) {
                        functionName += name;
                    }
                    expression.append(functionName).append(openingBracket);
                    addSyntaxUnitsToString(syntaxContainer.getSyntaxUnits(), expression);
                    expression.append(closingBracket);
                }
            } else {
                expression.append(syntaxUnit.getValue());
            }
        }
    }

    private static List<String> getLogicalUnits(String expression) {
        List<String> units = Arrays.stream(expression.split("\\b")).toList();
        List<String> basicLogicalUnits = combineUnitsIntoBasicLogicalUnits(units);
        LogicalUnitParser logicalUnitParser = new LogicalUnitParser(basicLogicalUnits);
        return logicalUnitParser.parse();
    }

    private static List<String> combineUnitsIntoBasicLogicalUnits(List<String> units) {
        return units.stream()
                .flatMap(unit -> processCharSequence(unit).stream())
                .toList();
    }

    private static List<String> processCharSequence(String charSequence) {
        CharSequenceNode charSequenceNode = new CharSequenceNode(charSequence);
        CharSequenceParser charSequenceParser = new CharSequenceParser(charSequenceNode);

        return charSequenceParser
                .processAlphaNumericCharacters()
                .processSpecialCharacters()
                .getProcessedValue();
    }
}
