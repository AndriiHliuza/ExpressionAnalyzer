package org.pzks.parsers;

import org.pzks.fixers.ExpressionFixer;
import org.pzks.parsers.math.laws.CommutativePropertyBasedSyntaxSyntaxUnitsProcessor;
import org.pzks.parsers.optimizers.ExpressionParallelizationOptimizer;
import org.pzks.parsers.parallelization.ParallelExpressionTreeBuilder;
import org.pzks.utils.*;
import org.pzks.utils.args.processor.ProgramKeyArg;
import org.pzks.utils.args.processor.PropertyArg;
import org.pzks.utils.trees.TreeNode;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.units.FunctionParam;
import org.pzks.units.SyntaxContainer;
import org.pzks.units.SyntaxUnit;
import org.pzks.utils.trees.TreeSerializer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ExpressionParser {

    public static void parse(
            String value,
            boolean verbose,
            boolean fixExpression,
            List<PropertyArg> propertyArgs,
            boolean buildParallelCalculationTree
    ) throws Exception {
        if (value.isBlank()) {
            System.out.println("\n" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + "\"" + " ".repeat(value.length()) + "\"");
            System.out.println(Color.RED.getAnsiValue() + "Error: " + Color.DEFAULT.getAnsiValue() + "Can't proceed with calculations. There is no expression or it contains only white spaces!");
        } else {
            SyntaxUnit parsedSyntaxUnit = convertExpressionToParsedSyntaxUnit(value);

            System.out.println("\n" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + value);
            SyntaxUnitMetaDataPrinter.printTreeWithHeadline(verbose, false, parsedSyntaxUnit, "Expression Tree");

            boolean isExpressionValid = calculateAndShowErrors(value, parsedSyntaxUnit);

            if (isExpressionValid) {
                parsedSyntaxUnit = simplifyExpression(parsedSyntaxUnit);
                boolean isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit);
                if (!isArithmeticErrorsPresent) {
                    String simplifiedExpression = getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());
                    printSimplifiedExpression(parsedSyntaxUnit, value);
                    SyntaxUnitMetaDataPrinter.printTreeWithHeadline(verbose, false, parsedSyntaxUnit, "Simplified expression tree");

                    isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit);

                    if (!isArithmeticErrorsPresent) {
                        parsedSyntaxUnit = optimizeSyntaxUnit(parsedSyntaxUnit);
                        String optimizedExpression = getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());
                        printOptimizedExpression(parsedSyntaxUnit, simplifiedExpression);
                        SyntaxUnitMetaDataPrinter.printTreeWithHeadline(verbose, false, parsedSyntaxUnit, "Optimized expression tree");

                        isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit);

                        if (propertyArgs.isEmpty()) {
                            if (!isArithmeticErrorsPresent) {
                                if (buildParallelCalculationTree) {
                                    buildParallelCalculationTree(parsedSyntaxUnit, "tree.json");
                                }
                            }
                        } else {
                            propertyProcessing(
                                    parsedSyntaxUnit,
                                    propertyArgs,
                                    isArithmeticErrorsPresent,
                                    buildParallelCalculationTree,
                                    optimizedExpression
                            );
                        }
                    }
                }
            } else if (fixExpression) {
                fixExpression(value, parsedSyntaxUnit);
            }

            System.out.println();
        }
    }

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

    private static boolean calculateAndShowErrors(String expression, SyntaxUnit parsedSyntaxUnit) {
        parsedSyntaxUnit.analyzeSyntaxErrors();
        List<SyntaxUnitErrorMessageBuilder> errors = parsedSyntaxUnit.getSyntaxUnitErrors();

        boolean isExpressionValid = errors.isEmpty();

        if (!isExpressionValid) {
            List<Integer> errorsPositions = errors.stream()
                    .map(SyntaxUnitErrorMessageBuilder::getErrorPosition)
                    .toList();
            HeadlinePrinter.print("Syntax analysis results", Color.RED);
            SyntaxUnitMetaDataPrinter.printExpressionWithErrorsPointing(expression, errorsPositions);
        } else {
            HeadlinePrinter.print("Syntax analysis results", Color.GREEN);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + expression);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Status: " + Color.DEFAULT.getAnsiValue() + "valid");
        }
        errors.forEach(System.out::println);


        return isExpressionValid;
    }

    private static void fixExpression(String expression, SyntaxUnit parsedSyntaxUnit) throws Exception {
        ExpressionFixer expressionFixer = new ExpressionFixer(parsedSyntaxUnit);
        SyntaxUnit fixedSyntaxUnit = expressionFixer.getFixedSyntaxUnit();
        String fixedExpression = getExpressionAsString(fixedSyntaxUnit.getSyntaxUnits());

        if (!fixedExpression.equals(expression.replaceAll("\\s*", ""))) {
            HeadlinePrinter.print("Syntax corrections", Color.GREEN);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Original expression: " + Color.DEFAULT.getAnsiValue() + expression);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Corrected expression: " + Color.DEFAULT.getAnsiValue() + fixedExpression);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is corrected: " + Color.DEFAULT.getAnsiValue() + !expression.replaceAll("\\s+", "").equals(fixedExpression));
        }
    }

    private static SyntaxUnit simplifyExpression(SyntaxUnit parsedSyntaxUnit) throws Exception {
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifier(parsedSyntaxUnit);
        return expressionSimplifier.getSimplifiedSyntaxUnit();
    }

    private static boolean detectArithmeticErrors(SyntaxUnit syntaxUnit) {
        boolean isErrorsPresent = false;

        syntaxUnit.analyzeArithmeticErrors();
        List<SyntaxUnitErrorMessageBuilder> arithmeticErrors = syntaxUnit.getArithmeticErrors();

        if (!arithmeticErrors.isEmpty()) {
            String expression = getExpressionAsString(syntaxUnit.getSyntaxUnits());
            List<Integer> errorsPositions = arithmeticErrors.stream()
                    .map(SyntaxUnitErrorMessageBuilder::getErrorPosition)
                    .toList();
            HeadlinePrinter.print("Arithmetic Errors", Color.RED);
            SyntaxUnitMetaDataPrinter.printExpressionWithErrorsPointing(expression, errorsPositions);
            arithmeticErrors.forEach(System.out::println);
            isErrorsPresent = true;
        }

        return isErrorsPresent;
    }

    private static void printSimplifiedExpression(SyntaxUnit syntaxUnit, String baseExpression) {
        HeadlinePrinter.print("Simplifications", Color.GREEN);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Original expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
        String simplifiedExpression = getExpressionAsString(syntaxUnit.getSyntaxUnits());
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Simplified expression: " + Color.DEFAULT.getAnsiValue() + simplifiedExpression);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is simplified: " + Color.DEFAULT.getAnsiValue() + !baseExpression.replaceAll("\\s+", "").equals(simplifiedExpression));
    }


    private static SyntaxUnit optimizeSyntaxUnit(SyntaxUnit syntaxUnit) throws Exception {
        ExpressionParallelizationOptimizer expressionParallelizationOptimizer = new ExpressionParallelizationOptimizer(syntaxUnit);
        return expressionParallelizationOptimizer.getFullyOptimizedSyntaxUnit();
    }

    private static void printOptimizedExpression(SyntaxUnit syntaxUnit, String baseExpression) {
        HeadlinePrinter.print("Optimizations", Color.GREEN);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Simplified expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
        String optimizedExpression = getExpressionAsString(syntaxUnit.getSyntaxUnits());
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Optimized expression: " + Color.DEFAULT.getAnsiValue() + optimizedExpression);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is optimized: " + Color.DEFAULT.getAnsiValue() + !baseExpression.replaceAll("\\s+", "").equals(optimizedExpression));
    }

    private static void buildParallelCalculationTree(SyntaxUnit syntaxUnit, String fileName) throws Exception {
        ParallelExpressionTreeBuilder treeBuilder = new ParallelExpressionTreeBuilder(syntaxUnit);
        List<String> warnings = treeBuilder.getWarnings();
        if (warnings.isEmpty()) {
            TreeNode rootNode = treeBuilder.getRootNode();

            HeadlinePrinter.print("Tree building info", Color.GREEN);
            boolean isSuccessfullySaved = TreeSerializer.safeToCurrentDirectory(rootNode, fileName);
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Tree was successfully build.");

            File currentDirectory = new File(".");
            String savedFileLocation = currentDirectory.getAbsolutePath().substring(0, currentDirectory.getAbsolutePath().length() - 1) + fileName;

            String messageUponSaving = isSuccessfullySaved ? "Tree was saved to '" + savedFileLocation + "'" : "Oops, something went wrong. File wasn't saved.";
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + messageUponSaving);
        } else {
            System.out.println("\n" + Color.YELLOW.getAnsiValue() + "Warning: " + Color.DEFAULT.getAnsiValue() + "The provided expression is not supported for building the parallel tree!");
            int maxWarningLength = warnings.stream()
                    .mapToInt(String::length)
                    .max()
                    .orElse(20);
            maxWarningLength = Math.max(maxWarningLength, 29);

            System.out.println("-".repeat(10) + Color.YELLOW.getAnsiValue() + "Warning details" + Color.DEFAULT.getAnsiValue() + "-".repeat(maxWarningLength + 6 - 25));
            for (String warning : warnings) {
                int warningLength = warning.length();
                int numberOfSpacesToAddTOTheOutput = maxWarningLength - warningLength;
                System.out.println("| - " + warning + " ".repeat(numberOfSpacesToAddTOTheOutput) + " |");
            }
            System.out.println("-".repeat(maxWarningLength + 6));
        }

    }

    private static SyntaxUnit calculateCommutativePropertyBasedSyntaxUnit(PropertyArg propertyArg, SyntaxUnit syntaxUnit, String baseExpression) throws Exception {
        return switch (propertyArg) {
            case COMMUTATIVE -> {
                CommutativePropertyBasedSyntaxSyntaxUnitsProcessor commutativePropertyBasedSyntaxSyntaxUnitsProcessor = new CommutativePropertyBasedSyntaxSyntaxUnitsProcessor(syntaxUnit);
                syntaxUnit = commutativePropertyBasedSyntaxSyntaxUnitsProcessor.getProcessedSyntaxUnit();
                HeadlinePrinter.print("Commutative property", Color.GREEN);

                String modifiedExpression = getExpressionAsString(syntaxUnit.getSyntaxUnits());
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Optimized expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Modified expression: " + Color.DEFAULT.getAnsiValue() + modifiedExpression);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is modified: " + Color.DEFAULT.getAnsiValue() + !baseExpression.replaceAll("\\s+", "").equals(modifiedExpression));
                yield convertExpressionToParsedSyntaxUnit(modifiedExpression);
            }
            case ASSOCIATIVE -> {
                HeadlinePrinter.print("Associative property", Color.GREEN);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Optimized expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Modified expression: " + Color.DEFAULT.getAnsiValue() + "To be implemented...");
                yield syntaxUnit;
            }
            case DEFAULT -> {
                HeadlinePrinter.print("Default property", Color.GREEN);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Optimized expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Status: " + Color.DEFAULT.getAnsiValue() + "Nothing to modify");
                yield syntaxUnit;
            }
            case null -> null;
        };
    }

    private static void propertyProcessing(
            SyntaxUnit syntaxUnit,
            List<PropertyArg> propertyArgs,
            boolean isArithmeticErrorsPresent,
            boolean buildParallelCalculationTree,
            String optimizedExpression
    ) throws Exception {
        System.out.println("\n" + Font.BOLD.getAnsiValue() +
                Color.BRIGHT_BLUE_GRAY_BACKGROUND.getAnsiValue() +
                Color.DARK_BLUE.getAnsiValue() +
                " ".repeat(20) +
                "Property processing" +
                " ".repeat(20) +
                Color.DEFAULT.getAnsiValue() +
                Font.DEFAULT.getAnsiValue()
        );
        for (PropertyArg propertyArg : propertyArgs) {
            switch (propertyArg) {
                case DEFAULT -> {
                    SyntaxUnit syntaxUnitToModifyAccordingToPropertyValue = convertExpressionToParsedSyntaxUnit(getExpressionAsString(syntaxUnit.getSyntaxUnits()));
                    if (!isArithmeticErrorsPresent) {
                        syntaxUnitToModifyAccordingToPropertyValue = calculateCommutativePropertyBasedSyntaxUnit(propertyArg, syntaxUnitToModifyAccordingToPropertyValue, optimizedExpression);
                        if (buildParallelCalculationTree) {
                            buildParallelCalculationTree(syntaxUnitToModifyAccordingToPropertyValue, "tree.json");
                        }
                    }
                }
                case COMMUTATIVE -> {
                    SyntaxUnit syntaxUnitToModifyAccordingToPropertyValue = convertExpressionToParsedSyntaxUnit(getExpressionAsString(syntaxUnit.getSyntaxUnits()));
                    if (!isArithmeticErrorsPresent) {
                        syntaxUnitToModifyAccordingToPropertyValue = calculateCommutativePropertyBasedSyntaxUnit(propertyArg, syntaxUnitToModifyAccordingToPropertyValue, optimizedExpression);
                        if (buildParallelCalculationTree) {
                            buildParallelCalculationTree(syntaxUnitToModifyAccordingToPropertyValue, "commutative-tree.json");
                        }
                    }
                }
                case ASSOCIATIVE -> {
                    SyntaxUnit syntaxUnitToModifyAccordingToPropertyValue = convertExpressionToParsedSyntaxUnit(getExpressionAsString(syntaxUnit.getSyntaxUnits()));
                    if (!isArithmeticErrorsPresent) {
                        syntaxUnitToModifyAccordingToPropertyValue = calculateCommutativePropertyBasedSyntaxUnit(propertyArg, syntaxUnitToModifyAccordingToPropertyValue, optimizedExpression);
                        if (buildParallelCalculationTree) {
                            buildParallelCalculationTree(syntaxUnitToModifyAccordingToPropertyValue, "associative-tree.json");
                        }
                    }
                }
            }
        }
    }
}
