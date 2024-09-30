package org.pzks.parsers;

import org.pzks.fixers.ExpressionFixer;
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

        boolean isArithmeticErrorsPresent = false;
        if (isExpressionValid) {
            parsedSyntaxUnit = simplifyExpression(fixSyntaxUnits, printTreeOfFixedAndSimplifiedExpression, value, parsedSyntaxUnit);
            isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit, value, fixSyntaxUnits, printTreeOfFixedAndSimplifiedExpression);
        }

        if (!isArithmeticErrorsPresent) {
            parsedSyntaxUnit = simplifyExpressionByProcessingMultiplicationByZero(fixSyntaxUnits, printTreeOfFixedAndSimplifiedExpression, value, parsedSyntaxUnit);
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
                HeadlinePrinter.print("Errors", Color.RED);
                SyntaxUnitStructurePrinter.printExpressionWithErrorsPointing(expression, errorsPositions);
            } else {
                HeadlinePrinter.print("Success", Color.GREEN);
                System.out.println(Color.GREEN.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + expression);
                System.out.println(Color.GREEN.getAnsiValue() + "Expression is valid" + Color.DEFAULT.getAnsiValue());
            }
            errors.forEach(System.out::println);
        }

        return isExpressionValid;
    }

    private SyntaxUnit fixExpression(boolean fixExpression, String expression, SyntaxUnit parsedSyntaxUnit) throws Exception {
        if (fixExpression) {
            HeadlinePrinter.print("Corrections", Color.GREEN);
            new ExpressionFixer(parsedSyntaxUnit.getSyntaxUnits()).fix();
            System.out.println(Color.GREEN.getAnsiValue() + "Original expression: " + Color.DEFAULT.getAnsiValue() + expression);
            String fixedExpression = SyntaxUnitStructurePrinter.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());
            System.out.println(Color.GREEN.getAnsiValue() + "Corrected expression: " + Color.DEFAULT.getAnsiValue() + fixedExpression);
            System.out.println(Color.GREEN.getAnsiValue() + "Is corrected: " + Color.DEFAULT.getAnsiValue() + !expression.equals(fixedExpression));

            parsedSyntaxUnit = convertExpressionToParsedSyntaxUnit(fixedExpression);
        }
        return parsedSyntaxUnit;
    }

    private SyntaxUnit simplifyExpression(boolean isExpressionFixed, boolean printTreeOfFixedAndSimplifiedExpression, String fixedExpression, SyntaxUnit parsedSyntaxUnit) throws Exception {
        if (isExpressionFixed) {
            new ExpressionSimplifier(parsedSyntaxUnit.getSyntaxUnits()).simplify();
            String simplifiedExpression = SyntaxUnitStructurePrinter.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());

            parsedSyntaxUnit = convertExpressionToParsedSyntaxUnit(simplifiedExpression);
        }
        return parsedSyntaxUnit;
    }

    private boolean detectArithmeticErrors(SyntaxUnit syntaxUnit, String expression, boolean isExpressionFixed, boolean printTreeOfFixedAndSimplifiedExpression) {
        boolean isErrorsPresent = false;

        syntaxUnit.analyzeArithmeticErrors();

        List<SyntaxUnitErrorMessageBuilder> arithmeticErrors = syntaxUnit.getArithmeticErrors();
        if (!arithmeticErrors.isEmpty()) {

            if (isExpressionFixed) {
                HeadlinePrinter.print("Simplifications", Color.GREEN);
                String simplifiedExpression = SyntaxUnitStructurePrinter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
                System.out.println(Color.GREEN.getAnsiValue() + "Simplified expression: " + Color.DEFAULT.getAnsiValue() + simplifiedExpression);
                System.out.println(Color.GREEN.getAnsiValue() + "Is simplified: " + Color.DEFAULT.getAnsiValue() + !expression.equals(simplifiedExpression));

                if (printTreeOfFixedAndSimplifiedExpression) {
                    SyntaxUnitStructurePrinter.printTreeWithHeadline(true, false, syntaxUnit, "Corrected & Simplified expression tree");
                }

                List<Integer> errorsPositions = arithmeticErrors.stream()
                        .map(SyntaxUnitErrorMessageBuilder::getErrorPosition)
                        .toList();
                HeadlinePrinter.print("Arithmetic Errors", Color.RED);
                SyntaxUnitStructurePrinter.printExpressionWithErrorsPointing(simplifiedExpression, errorsPositions);
                arithmeticErrors.forEach(System.out::println);
                isErrorsPresent = true;
            }
        }

        return isErrorsPresent;
    }

    private SyntaxUnit simplifyExpressionByProcessingMultiplicationByZero(boolean isExpressionFixed, boolean printTreeOfFixedAndSimplifiedExpression, String expression, SyntaxUnit syntaxUnit) throws Exception {
        if (isExpressionFixed) {
            HeadlinePrinter.print("Simplifications", Color.GREEN);
            new ExpressionSimplifier(syntaxUnit.getSyntaxUnits()).simplifyByProcessingMultiplicationByZero();
            String simplifiedExpression = SyntaxUnitStructurePrinter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
            System.out.println(Color.GREEN.getAnsiValue() + "Simplified expression: " + Color.DEFAULT.getAnsiValue() + simplifiedExpression);
            System.out.println(Color.GREEN.getAnsiValue() + "Is simplified: " + Color.DEFAULT.getAnsiValue() + !expression.equals(simplifiedExpression));

            syntaxUnit = convertExpressionToParsedSyntaxUnit(simplifiedExpression);

            if (printTreeOfFixedAndSimplifiedExpression) {
                SyntaxUnitStructurePrinter.printTreeWithHeadline(true, false, syntaxUnit, "Corrected & Simplified expression tree");
            }
        }
        return syntaxUnit;

    }
}
