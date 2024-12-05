package org.pzks.parsers;

import org.pzks.fixers.ExpressionFixer;
import org.pzks.parsers.converters.ExpressionConverter;
import org.pzks.parsers.systems.dataflow.DataflowSystem;
import org.pzks.parsers.parallelization.ParallelExpressionTreeFactory;
import org.pzks.parsers.math.laws.AssociativePropertyBasedSyntaxUnitProcessor;
import org.pzks.parsers.math.laws.CommutativePropertyBasedSyntaxUnitProcessor;
import org.pzks.parsers.math.laws.units.SyntaxUnitExpression;
import org.pzks.parsers.optimizers.ExpressionOptimizer;
import org.pzks.parsers.systems.dataflow.SystemMetrics;
import org.pzks.settings.GlobalSettings;
import org.pzks.units.*;
import org.pzks.utils.*;
import org.pzks.settings.args.processor.BoolArg;
import org.pzks.settings.args.processor.PropertyArg;
import org.pzks.utils.trees.BinaryTreeNode;
import org.pzks.parsers.simplifiers.ExpressionSimplifier;
import org.pzks.utils.trees.NaryTreeNode;
import org.pzks.utils.trees.TreeSerializer;

import java.io.File;
import java.util.List;

public class ExpressionProcessor {

    public static void process(String value) throws Exception {
        if (value.isBlank()) {
            System.out.println("\n" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + "\"" + " ".repeat(value.length()) + "\"");
            System.out.println(Color.RED.getAnsiValue() + "Error: " + Color.DEFAULT.getAnsiValue() + "Can't proceed with calculations. There is no expression or it contains only white spaces!");
        } else {
            SyntaxUnit parsedSyntaxUnit = ExpressionConverter.convertExpressionToParsedSyntaxUnit(value);

            System.out.println("\n" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Expression: " + Color.DEFAULT.getAnsiValue() + value);
            HeadlinePrinter.printWithBackground("Expression analysis & Optimizations...");
            SyntaxUnitMetaDataPrinter.printTreeWithHeadline(GlobalSettings.CONFIGURATION.shouldShowVerboseOutput(), false, parsedSyntaxUnit, "Expression Tree");

            boolean isExpressionValid = calculateAndShowErrors(value, parsedSyntaxUnit);

            if (isExpressionValid) {
                parsedSyntaxUnit = simplifyExpression(parsedSyntaxUnit);
                boolean isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit);
                if (!isArithmeticErrorsPresent) {
                    String modifiedExpression = ExpressionConverter.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());
                    printSimplifiedExpression(parsedSyntaxUnit, value);
                    SyntaxUnitMetaDataPrinter.printTreeWithHeadline(GlobalSettings.CONFIGURATION.shouldShowVerboseOutput(), false, parsedSyntaxUnit, "Simplified expression tree");

                    isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit);

                    if (!isArithmeticErrorsPresent) {

                        if (GlobalSettings.CONFIGURATION.getOptimizationArg() == BoolArg.TRUE) {
                            parsedSyntaxUnit = optimizeSyntaxUnit(parsedSyntaxUnit);
                            modifiedExpression = ExpressionConverter.getExpressionAsString(parsedSyntaxUnit.getSyntaxUnits());
                            printOptimizedExpression(parsedSyntaxUnit, modifiedExpression);
                            SyntaxUnitMetaDataPrinter.printTreeWithHeadline(GlobalSettings.CONFIGURATION.shouldShowVerboseOutput(), false, parsedSyntaxUnit, "Optimized expression tree");
                        }

                        isArithmeticErrorsPresent = detectArithmeticErrors(parsedSyntaxUnit);

                        if (GlobalSettings.CONFIGURATION.getPropertyArgs().isEmpty()) {
                            if (!isArithmeticErrorsPresent) {
                                if (GlobalSettings.CONFIGURATION.shouldBuildParallelCalculationTree()) {
                                    buildParallelCalculationTree(parsedSyntaxUnit, "tree.json");
                                }
                            }
                        } else {
                            propertyProcessing(
                                    parsedSyntaxUnit,
                                    GlobalSettings.CONFIGURATION.getPropertyArgs(),
                                    isArithmeticErrorsPresent,
                                    modifiedExpression
                            );
                        }
                    }
                }
            } else if (GlobalSettings.CONFIGURATION.shouldFixExpression()) {
                fixExpression(value, parsedSyntaxUnit);
            }

            System.out.println();
        }
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
        String fixedExpression = ExpressionConverter.getExpressionAsString(fixedSyntaxUnit.getSyntaxUnits());

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
            String expression = ExpressionConverter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
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
        String simplifiedExpression = ExpressionConverter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Simplified expression: " + Color.DEFAULT.getAnsiValue() + simplifiedExpression);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is simplified: " + Color.DEFAULT.getAnsiValue() + !baseExpression.replaceAll("\\s+", "").equals(simplifiedExpression));
    }


    private static SyntaxUnit optimizeSyntaxUnit(SyntaxUnit syntaxUnit) throws Exception {
        ExpressionOptimizer expressionOptimizer = new ExpressionOptimizer(syntaxUnit);
        return expressionOptimizer.getOptimizedSyntaxUnit();
    }

    private static void printOptimizedExpression(SyntaxUnit syntaxUnit, String baseExpression) {
        HeadlinePrinter.print("Optimizations", Color.GREEN);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Simplified expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
        String optimizedExpression = ExpressionConverter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Optimized expression: " + Color.DEFAULT.getAnsiValue() + optimizedExpression);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is optimized: " + Color.DEFAULT.getAnsiValue() + !baseExpression.replaceAll("\\s+", "").equals(optimizedExpression));
    }

    private static void buildParallelCalculationTree(SyntaxUnit syntaxUnit, String fileName) throws Exception {
        ParallelExpressionTreeFactory parallelExpressionTreeFactory = new ParallelExpressionTreeFactory(syntaxUnit);
        List<String> warnings = parallelExpressionTreeFactory.getWarnings();
        if (GlobalSettings.CONFIGURATION.shouldBuildBinaryParallelCalculationTree()) {
            if (warnings.isEmpty()) {
                BinaryTreeNode binaryTreeRootNode = parallelExpressionTreeFactory.getBinaryTreeRootNode();

                HeadlinePrinter.print("Tree building info", Color.GREEN);
                boolean isSuccessfullySaved = TreeSerializer.safeBinaryTreeToCurrentDirectory(binaryTreeRootNode, fileName);
                displayTreeBuildingStatus(fileName, isSuccessfullySaved);

                simulateDataflow(parallelExpressionTreeFactory, fileName);
            } else {
                if (GlobalSettings.CONFIGURATION.shouldShowWarnings()) {
                    HeadlinePrinter.print("Tree building info", Color.GREEN);
                    System.out.println(Color.YELLOW.getAnsiValue() + "Warning: " + Color.DEFAULT.getAnsiValue() + "The provided expression is not supported for building the parallel tree!");
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
        } else {
            NaryTreeNode naryTreeRootNode = parallelExpressionTreeFactory.getNaryTreeRootNode();

            HeadlinePrinter.print("Tree building info", Color.GREEN);
            boolean isSuccessfullySaved = TreeSerializer.safeNaryTreeToCurrentDirectory(naryTreeRootNode, fileName);
            if (!SyntaxUnitsValidationUtil.isFunctionsAbsent(syntaxUnit.getSyntaxUnits())) {
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Functions detected when building parallel calculation tree.\n");
            }
            displayTreeBuildingStatus(fileName, isSuccessfullySaved);

            simulateDataflow(parallelExpressionTreeFactory, fileName);
        }
    }

    private static void displayTreeBuildingStatus(String fileName, boolean isSuccessfullySaved) {
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Tree was successfully build.");

        File currentDirectory = new File(".");
        String savedFileLocation = currentDirectory.getAbsolutePath().substring(0, currentDirectory.getAbsolutePath().length() - 1) + fileName;

        String messageUponSaving = isSuccessfullySaved ? "Tree was saved to '" + savedFileLocation + "'" : "Oops, something went wrong. File wasn't saved.";
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + messageUponSaving);
    }

    private static void simulateDataflow(ParallelExpressionTreeFactory parallelExpressionTreeFactory, String fileName) throws CloneNotSupportedException {
        if (GlobalSettings.CONFIGURATION.shouldEnableDataflowSystemSimulation()) {
            HeadlinePrinter.print("Dataflow simulation", Color.GREEN);

            NaryTreeNode operationsTreeRootNode = parallelExpressionTreeFactory.getOperationsTreeRootNode();
            DataflowSystem dataflowSystem = new DataflowSystem(operationsTreeRootNode);
            operationsTreeRootNode = dataflowSystem.getNaryTreeNode();

            if (operationsTreeRootNode != null) {
                boolean isOperationTreeSuccessfullySaved = TreeSerializer.safeNaryTreeToCurrentDirectory(operationsTreeRootNode, "operation-" + fileName);
                displayTreeBuildingStatus("operation-" + fileName, isOperationTreeSuccessfullySaved);

                System.out.println("""
                    
                    -------------------------------
                    | System characteristics      |
                    -------------------------------
                    | System type: Dataflow       |
                    | Number of processors: 2     |
                    | Number of memory banks: 1   |
                    -------------------------------
                    | Clock time:                 |
                    | +    -> 2                   |
                    | -    -> 3                   |
                    | *    -> 4                   |
                    | /    -> 8                   |
                    | func -> 9                   |
                    -------------------------------
                    """);

                if (!GlobalSettings.CONFIGURATION.shouldShowOnlyDataflowSystemStatistics()) {
                    dataflowSystem.displayDiagram();
                    System.out.println();
                }

                SystemMetrics systemMetrics = dataflowSystem.getSystemMetrics();
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "------Statistics---->" + Color.DEFAULT.getAnsiValue());
                String sequentialExecutionTimeInfo = String.format("%s| Sequential execution time (time units):%s %d", Color.BRIGHT_MAGENTA.getAnsiValue(), Color.DEFAULT.getAnsiValue(), systemMetrics.getSequentialExecutionTime());
                String executionTimeInfo = String.format("%s| Execution time (time units):%s %d", Color.BRIGHT_MAGENTA.getAnsiValue(), Color.DEFAULT.getAnsiValue(), systemMetrics.getExecutionTime());
                String speedupCoefficientInfo = String.format("%s| Speedup (coefficient):%s %.2f", Color.BRIGHT_MAGENTA.getAnsiValue(), Color.DEFAULT.getAnsiValue(), systemMetrics.getSpeedupCoefficient());
                String efficiencyCoefficientInfo = String.format("%s| Efficiency (coefficient):%s %.2f", Color.BRIGHT_MAGENTA.getAnsiValue(), Color.DEFAULT.getAnsiValue(), systemMetrics.getEfficiencyCoefficient());
                System.out.println(sequentialExecutionTimeInfo);
                System.out.println(executionTimeInfo);
                System.out.println(speedupCoefficientInfo);
                System.out.println(efficiencyCoefficientInfo);
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "-".repeat(20) + ">" + Color.DEFAULT.getAnsiValue());

                System.out.println("\n" + Color.BRIGHT_MAGENTA.getAnsiValue() + "Status: " + Color.GREEN.getAnsiValue() + "Success" + Color.DEFAULT.getAnsiValue());
            } else {
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Expression does not contain enough data to build tree of operations.");
                System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Status: " + Color.RED.getAnsiValue() + "Failed" + Color.DEFAULT.getAnsiValue());
            }

        }
    }

    private static void propertyProcessing(
            SyntaxUnit syntaxUnit,
            List<PropertyArg> propertyArgs,
            boolean isArithmeticErrorsPresent,
            String expressionBeforeProcessingProperty
    ) throws Exception {
        HeadlinePrinter.printWithBackground("Property processing...");
        for (PropertyArg propertyArg : propertyArgs) {
            switch (propertyArg) {
                case DEFAULT -> {
                    SyntaxUnit syntaxUnitToModifyAccordingToPropertyValue = ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(syntaxUnit.getSyntaxUnits()));
                    if (!isArithmeticErrorsPresent) {
                        HeadlinePrinter.print("Default property", Color.GREEN);
                        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Provided expression: " + Color.DEFAULT.getAnsiValue() + expressionBeforeProcessingProperty);
                        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Status: " + Color.DEFAULT.getAnsiValue() + "Nothing to modify");
                        if (GlobalSettings.CONFIGURATION.shouldBuildParallelCalculationTree()) {
                            buildParallelCalculationTree(syntaxUnitToModifyAccordingToPropertyValue, "tree.json");
                        }
                    }
                }
                case COMMUTATIVE -> {
                    SyntaxUnit syntaxUnitToModifyAccordingToPropertyValue = ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(syntaxUnit.getSyntaxUnits()));
                    if (!isArithmeticErrorsPresent) {
                        syntaxUnitToModifyAccordingToPropertyValue = calculateCommutativePropertyBasedSyntaxUnit(syntaxUnitToModifyAccordingToPropertyValue, expressionBeforeProcessingProperty);
                        if (GlobalSettings.CONFIGURATION.shouldBuildParallelCalculationTree()) {
                            buildParallelCalculationTree(syntaxUnitToModifyAccordingToPropertyValue, "commutative-tree.json");
                        }
                    }
                }
                case ASSOCIATIVE -> {
                    SyntaxUnit syntaxUnitToModifyAccordingToPropertyValue = ExpressionConverter.convertExpressionToParsedSyntaxUnit(ExpressionConverter.getExpressionAsString(syntaxUnit.getSyntaxUnits()));
                    if (!isArithmeticErrorsPresent) {
                        if (GlobalSettings.CONFIGURATION.getNumberOfGeneratedExpressionsLimit() == Long.MAX_VALUE) {
                            System.out.println("\n" + Color.YELLOW.getAnsiValue() + "Warning: " + Color.DEFAULT.getAnsiValue() + "Removing limit for number of generated expressions may result in large CPU and RAM consumption!");

                            if (!ProgramExecutionUtil.confirmAndProceed()) {
                                return;
                            }
                        }

                        calculateAssociativePropertyBasedSyntaxUnits(syntaxUnitToModifyAccordingToPropertyValue, expressionBeforeProcessingProperty);

                        if (GlobalSettings.CONFIGURATION.shouldShowWarnings()) {
                            HeadlinePrinter.print("Tree building info", Color.GREEN);
                            System.out.println(Color.YELLOW.getAnsiValue() + "Warning: " + Color.DEFAULT.getAnsiValue() +
                                    "Tree building is not supported for associative property " +
                                    "due to potentially large number of expressions!");

                            System.out.println("-".repeat(10) + Color.YELLOW.getAnsiValue() + "Warning details" + Color.DEFAULT.getAnsiValue() + "-".repeat(70));
                            System.out.println("| Please run program again with generated expression if you want to build tree for it." + " ".repeat(8) + "|");
                            System.out.println("-".repeat(95));
                        }
                    }
                }
            }
        }
    }

    private static SyntaxUnit calculateCommutativePropertyBasedSyntaxUnit(SyntaxUnit syntaxUnit, String baseExpression) throws Exception {
        CommutativePropertyBasedSyntaxUnitProcessor commutativePropertyBasedSyntaxUnitsProcessor = new CommutativePropertyBasedSyntaxUnitProcessor(syntaxUnit);
        syntaxUnit = commutativePropertyBasedSyntaxUnitsProcessor.getProcessedSyntaxUnit();
        HeadlinePrinter.print("Commutative property", Color.GREEN);

        String modifiedExpression = ExpressionConverter.getExpressionAsString(syntaxUnit.getSyntaxUnits());
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Provided expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Modified expression: " + Color.DEFAULT.getAnsiValue() + modifiedExpression);
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Is modified: " + Color.DEFAULT.getAnsiValue() + !baseExpression.replaceAll("\\s+", "").equals(modifiedExpression));
        return ExpressionConverter.convertExpressionToParsedSyntaxUnit(modifiedExpression);
    }

    private static void calculateAssociativePropertyBasedSyntaxUnits(SyntaxUnit syntaxUnit, String baseExpression) throws Exception {
        HeadlinePrinter.print("Associative property", Color.GREEN);
        if (GlobalSettings.CONFIGURATION.getNumberOfGeneratedExpressionsLimit() == -1L) {
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Not specified limit for number of generated expressions.");
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Falling back to default limit of ~" + GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT + " expressions.");
        } else if (GlobalSettings.CONFIGURATION.getNumberOfGeneratedExpressionsLimit() == Long.MAX_VALUE) {
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Limit for number of generated expressions was removed.");
        } else if (GlobalSettings.CONFIGURATION.getNumberOfGeneratedExpressionsLimit() != GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT) {
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "New limit for number of generated expressions set to ~" + GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT + " expressions.");
        }

        AssociativePropertyBasedSyntaxUnitProcessor associativePropertyBasedSyntaxUnitProcessor = new AssociativePropertyBasedSyntaxUnitProcessor(syntaxUnit);
        SyntaxUnitExpression generatedAfterAssociativePropertySyntaxUnitExpression = associativePropertyBasedSyntaxUnitProcessor.getSyntaxUnitExpression();
        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Provided expression: " + Color.DEFAULT.getAnsiValue() + baseExpression);

        if (generatedAfterAssociativePropertySyntaxUnitExpression != null && !generatedAfterAssociativePropertySyntaxUnitExpression.getSyntaxUnitExpressions().isEmpty()) {
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Log: " + Color.DEFAULT.getAnsiValue() + "Generated expressions were saved to ./associative-expressions.txt\n");
            generatedAfterAssociativePropertySyntaxUnitExpression.saveTreeOfDependentSyntaxUnitExpressionsToFile("associative-expressions.txt");
        } else {
            System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Status: " + Color.DEFAULT.getAnsiValue() + "No modifications were made.");
        }

        System.out.println(Color.BRIGHT_MAGENTA.getAnsiValue() + "Total number of generated expressions: " + Color.DEFAULT.getAnsiValue() + associativePropertyBasedSyntaxUnitProcessor.getNumberOfGeneratedExpressions() + "\n");

    }
}
