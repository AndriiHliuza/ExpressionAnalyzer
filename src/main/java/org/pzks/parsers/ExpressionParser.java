package org.pzks.parsers;

import org.pzks.fixers.ExpressionFixer;
import org.pzks.parsers.simplifiers.BasicExpressionSimplifier;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.SyntaxUnit;
import org.pzks.utils.Color;
import org.pzks.utils.HeadlinePrinter;
import org.pzks.utils.SyntaxUnitErrorMessageBuilder;
import org.pzks.utils.SyntaxUnitStructurePrinter;

import java.util.Arrays;
import java.util.List;

public class ExpressionParser {

    public void parse(
            String value,
            boolean printTreeOfSyntaxUnits,
            boolean showErrors,
            boolean fixSyntaxUnits,
            boolean printTreeOfFixedAndSimplifiedExpression,
            boolean buildParallelExpressionCalculationTree
    ) throws Exception {
        SyntaxUnit parsedSyntaxUnit = convertExpressionToParsedSyntaxUnit(value);

        System.out.println("\n" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + value);
        SyntaxUnitStructurePrinter.printTreeWithHeadline(printTreeOfSyntaxUnits, false, parsedSyntaxUnit, "Expression Tree");

        boolean isExpressionValid = calculateAndShowErrors(showErrors, value, parsedSyntaxUnit);
        parsedSyntaxUnit = fixExpression(fixSyntaxUnits, value, parsedSyntaxUnit);

        if (isExpressionValid) {
            parsedSyntaxUnit = simplifyExpression(parsedSyntaxUnit);
            boolean isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit, printTreeOfFixedAndSimplifiedExpression);
            if (!isArithmeticErrorsPresent) {
                printSimplifiedExpression(parsedSyntaxUnit, value, printTreeOfFixedAndSimplifiedExpression);
            }
        }

        System.out.println();
    }

    public SyntaxUnit convertExpressionToParsedSyntaxUnit(String expression) throws Exception {
        List<String> logicalUnits = getLogicalUnits(expression);
        SyntaxUnit syntaxUnit = new SyntaxUnit(0, logicalUnits);
        SyntaxUnit parsedSyntaxUnit = syntaxUnit.parse();
        UnknownSyntaxUnitsParser unknownSyntaxUnitsParser = new UnknownSyntaxUnitsParser(parsedSyntaxUnit);
        parsedSyntaxUnit = unknownSyntaxUnitsParser.parse();
        return parsedSyntaxUnit;
    }

    private List<String> getLogicalUnits(String expression) {
        List<String> units = Arrays.stream(expression.split("\\b")).toList();
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

    private boolean calculateAndShowErrors(boolean showErrors, String expression, SyntaxUnit parsedSyntaxUnit) {
        parsedSyntaxUnit.analyzeSyntaxErrors();
        List<SyntaxUnitErrorMessageBuilder> errors = parsedSyntaxUnit.getSyntaxUnitErrors();

        boolean isExpressionValid = errors.isEmpty();

        if (showErrors) {
            if (!isExpressionValid) {
                List<Integer> errorsPositions = errors.stream()
                        .map(SyntaxUnitErrorMessageBuilder::getErrorPosition)
                        .toList();
                HeadlinePrinter.print("Syntax analysis results [Errors]", Color.RED);
                SyntaxUnitStructurePrinter.printExpressionWithErrorsPointing(expression, errorsPositions);
            } else {
                HeadlinePrinter.print("Syntax analysis results [Success]", Color.GREEN);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + expression);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Status: " + Color.DEFAULT.getAnsiValue() + "valid");
            }
            errors.forEach(System.out::println);
        }

        return isExpressionValid;
    }

    private SyntaxUnit fixExpression(boolean fixExpression, String expression, SyntaxUnit parsedSyntaxUnit) throws Exception {
        if (fixExpression) {
            HeadlinePrinter.print("Syntax corrections", Color.GREEN);
            new ExpressionFixer(parsedSyntaxUnit.getSyntaxUnits()).fix();
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Original expression: " + Color.DEFAULT.getAnsiValue() + expression);
            String fixedExpression = SyntaxUnitStructurePrinter.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Corrected expression: " + Color.DEFAULT.getAnsiValue() + fixedExpression);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is corrected: " + Color.DEFAULT.getAnsiValue() + !expression.replaceAll("\\s+", "").equals(fixedExpression));

            parsedSyntaxUnit = convertExpressionToParsedSyntaxUnit(fixedExpression);
        }
        return parsedSyntaxUnit;
    }

    private SyntaxUnit simplifyExpression(SyntaxUnit parsedSyntaxUnit) throws Exception {
        new ExpressionSimplifier(parsedSyntaxUnit.getSyntaxUnits()).simplify();
        String simplifiedExpression = SyntaxUnitStructurePrinter.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());
        simplifiedExpression = new BasicExpressionSimplifier(simplifiedExpression).removeUnnecessaryZerosAfterDotInNumbers().getExpression();
        parsedSyntaxUnit = convertExpressionToParsedSyntaxUnit(simplifiedExpression);
        return parsedSyntaxUnit;
    }

    private boolean detectArithmeticErrors(SyntaxUnit syntaxUnit, boolean printTreeOfFixedAndSimplifiedExpression) {
        boolean isErrorsPresent = false;

        syntaxUnit.analyzeArithmeticErrors();
        List<SyntaxUnitErrorMessageBuilder> arithmeticErrors = syntaxUnit.getArithmeticErrors();

        if (!arithmeticErrors.isEmpty() && printTreeOfFixedAndSimplifiedExpression) {
            String expression = SyntaxUnitStructurePrinter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
            List<Integer> errorsPositions = arithmeticErrors.stream()
                    .map(SyntaxUnitErrorMessageBuilder::getErrorPosition)
                    .toList();
            HeadlinePrinter.print("Arithmetic Errors", Color.RED);
            SyntaxUnitStructurePrinter.printExpressionWithErrorsPointing(expression, errorsPositions);
            arithmeticErrors.forEach(System.out::println);
            isErrorsPresent = true;
        }

        return isErrorsPresent;
    }

    private void printSimplifiedExpression(SyntaxUnit syntaxUnit, String baseExpression, boolean printSimplifiedExpression) {
        if (printSimplifiedExpression) {
            HeadlinePrinter.print("Simplifications", Color.GREEN);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Original expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
            String simplifiedExpression = SyntaxUnitStructurePrinter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Simplified expression: " + Color.DEFAULT.getAnsiValue() + simplifiedExpression);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is simplified: " + Color.DEFAULT.getAnsiValue() + !baseExpression.replaceAll("\\s+", "").equals(simplifiedExpression));
        }
    }
}
