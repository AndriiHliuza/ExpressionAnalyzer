package org.pzks.settings;

import org.pzks.settings.args.processor.BoolArg;
import org.pzks.settings.args.processor.ProgramArgsProcessor;
import org.pzks.settings.args.processor.PropertyArg;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private final boolean showVerboseOutput;
    private final boolean shouldFixExpression;
    private final BoolArg optimizationArg;
    private final List<PropertyArg> propertyArgs;
    private final boolean buildParallelCalculationTree;
    private final boolean buildBinaryParallelCalculationTree;
    private final long numberOfGeneratedExpressionsLimit;
    private final boolean showWarnings;

    private final boolean enableDataflowSystemSimulation;
    private final boolean showOnlyDataflowSystemStatistics;

    public Configuration() {
        showVerboseOutput = false;
        shouldFixExpression = true;
        optimizationArg = BoolArg.TRUE;
        propertyArgs = new ArrayList<>();
        buildParallelCalculationTree = false;
        buildBinaryParallelCalculationTree = false;
        numberOfGeneratedExpressionsLimit = -1L;
        showWarnings = true;

        enableDataflowSystemSimulation = false;
        showOnlyDataflowSystemStatistics = false;
    }

    public Configuration(ProgramArgsProcessor programArgsProcessor) {
        showVerboseOutput = programArgsProcessor.shouldShowVerboseOutput();
        shouldFixExpression = programArgsProcessor.shouldFixExpressionIfErrorsPresent();
        optimizationArg = programArgsProcessor.getOptimizationArg();
        propertyArgs = programArgsProcessor.getPropertyArgs();
        buildParallelCalculationTree = programArgsProcessor.shouldBuildParallelCalculationTree();
        buildBinaryParallelCalculationTree = programArgsProcessor.shouldBuildBinaryParallelCalculationTree();
        if (programArgsProcessor.shouldRemoveLimitOfGeneratedExpressionsBasedOnProperty()) {
            numberOfGeneratedExpressionsLimit = Long.MAX_VALUE;
            GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT = Long.MAX_VALUE;
        } else {
            numberOfGeneratedExpressionsLimit = programArgsProcessor.getLimitNumberOfGeneratedExpressionsBasedOnProperty();
            if (numberOfGeneratedExpressionsLimit != -1L && GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT != numberOfGeneratedExpressionsLimit) {
                GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT = numberOfGeneratedExpressionsLimit;
            }
        }
        showWarnings = programArgsProcessor.shouldShowWarnings();
        enableDataflowSystemSimulation = programArgsProcessor.shouldEnableDataflowSystemSimulation();
        showOnlyDataflowSystemStatistics = programArgsProcessor.shouldShowOnlyDataflowSystemStatistics();
    }

    public Configuration(
            boolean showVerboseOutput,
            boolean shouldFixExpression,
            BoolArg optimizationArg,
            List<PropertyArg> propertyArgs,
            boolean buildParallelCalculationTree,
            boolean buildBinaryParallelCalculationTree,
            long numberOfGeneratedExceptionsLimit,
            boolean showWarnings,
            boolean enableDataflowSystemSimulation,
            boolean showOnlyDataflowSystemStatistics
    ) {
        this.showVerboseOutput = showVerboseOutput;
        this.shouldFixExpression = shouldFixExpression;
        this.optimizationArg = optimizationArg;
        this.propertyArgs = propertyArgs;
        this.buildParallelCalculationTree = buildParallelCalculationTree;
        this.buildBinaryParallelCalculationTree = buildBinaryParallelCalculationTree;
        this.numberOfGeneratedExpressionsLimit = numberOfGeneratedExceptionsLimit;
        if (numberOfGeneratedExpressionsLimit != -1L && GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT != numberOfGeneratedExceptionsLimit) {
            GlobalSettings.Property.NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT = numberOfGeneratedExceptionsLimit;
        }
        this.showWarnings = showWarnings;

        this.enableDataflowSystemSimulation = enableDataflowSystemSimulation;
        this.showOnlyDataflowSystemStatistics = showOnlyDataflowSystemStatistics;
    }

    public boolean shouldShowVerboseOutput() {
        return showVerboseOutput;
    }

    public boolean shouldFixExpression() {
        return shouldFixExpression;
    }

    public BoolArg getOptimizationArg() {
        return optimizationArg;
    }

    public List<PropertyArg> getPropertyArgs() {
        return propertyArgs;
    }

    public boolean shouldBuildParallelCalculationTree() {
        return buildParallelCalculationTree;
    }

    public boolean shouldBuildBinaryParallelCalculationTree() {
        return buildBinaryParallelCalculationTree;
    }

    public long getNumberOfGeneratedExpressionsLimit() {
        return numberOfGeneratedExpressionsLimit;
    }

    public boolean shouldShowWarnings() {
        return showWarnings;
    }

    public boolean shouldEnableDataflowSystemSimulation() {
        return enableDataflowSystemSimulation;
    }

    public boolean shouldShowOnlyDataflowSystemStatistics() {
        return showOnlyDataflowSystemStatistics;
    }

}
