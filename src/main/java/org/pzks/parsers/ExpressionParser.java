package org.pzks.parsers;

import org.pzks.units.SyntaxUnit;
import org.pzks.utils.Color;
import org.pzks.utils.HeadlinePrinter;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;
import org.pzks.utils.SyntaxUnitTreePrinter;

import java.util.Arrays;
import java.util.List;

public class ExpressionParser {

    public void parse(String value, boolean printTreeOfSyntaxUnits, boolean showErrors) throws Exception {
        List<String> logicalUnits = getLogicalUnits(value);
        System.out.println(logicalUnits);

        SyntaxUnit syntaxUnit = new SyntaxUnit(0, logicalUnits);
        SyntaxUnit parsedSyntaxUnit = syntaxUnit.parse();
        UnknownSyntaxUnitsParser unknownSyntaxUnitsParser = new UnknownSyntaxUnitsParser(parsedSyntaxUnit);
        parsedSyntaxUnit = unknownSyntaxUnitsParser.parse();

        if (printTreeOfSyntaxUnits) {
            HeadlinePrinter.print("Expression Tree", Color.CYAN);
            List<SyntaxUnit> syntaxUnits = parsedSyntaxUnit.getSyntaxUnits();
            new SyntaxUnitTreePrinter().print(syntaxUnits);
        }

        if (showErrors) {
            parsedSyntaxUnit.analyze();
            List<SyntaxUnitErrorMessageBuilder> errors = parsedSyntaxUnit.getSyntaxUnitErrors();
            if (!errors.isEmpty()) {
                HeadlinePrinter.print("Errors", Color.RED);
                System.out.println(Color.RED.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + value + "\n");
            } else {
                HeadlinePrinter.print("Success", Color.GREEN);
                System.out.println(Color.GREEN.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + value);
                System.out.println(Color.GREEN.getAnsiValue() + "Expression is valid" + Color.DEFAULT.getAnsiValue());
            }
            errors.forEach(System.out::println);
        }
    }

    private List<String> getLogicalUnits(String expression) {
        List<String> units = Arrays.stream(expression.split("\\b")).toList();
        System.out.println(units);
        List<String> basicLogicalUnits = combineUnitsIntoBasicLogicalUnits(units);
        LogicalUnitParser logicalUnitParser = new LogicalUnitParser(basicLogicalUnits);
        return logicalUnitParser.parse();
    }

    private List<String> combineUnitsIntoBasicLogicalUnits(List<String> units) {
        return units.stream()
                .flatMap(unit -> processCharSequence(unit).stream())
                .toList();
    }

    private List<String> processCharSequence(String charSequence) {
        CharSequenceNode charSequenceNode = new CharSequenceNode(charSequence);
        CharSequenceParser charSequenceParser = new CharSequenceParser(charSequenceNode);

        return charSequenceParser
                .processAlphaNumericCharacters()
                .processSpecialCharacters()
                .getProcessedValue();
    }
}
