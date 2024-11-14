package org.pzks.utils;

import org.pzks.utils.args.processor.BoolArg;
import org.pzks.utils.args.processor.ProgramArgsProcessor;
import org.pzks.utils.args.processor.PropertyArg;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private final boolean showVerboseOutput;
    private final boolean shouldFixExpressions;
    private final BoolArg optimizationArg;
    private final List<PropertyArg> propertyArgs;
    private final boolean buildParallelCalculationTree;
    private final boolean showWarnings;

    public Configuration() {
        showVerboseOutput = false;
        shouldFixExpressions = true;
        optimizationArg = BoolArg.TRUE;
        propertyArgs = new ArrayList<>();
        buildParallelCalculationTree = false;
        showWarnings = true;
    }

    public Configuration(ProgramArgsProcessor programArgsProcessor) {
        showVerboseOutput = programArgsProcessor.shouldShowVerboseOutput();
        shouldFixExpressions = programArgsProcessor.shouldFixExpressionIfErrorsPresent();
        optimizationArg = programArgsProcessor.getOptimizationArg();
        propertyArgs = programArgsProcessor.getPropertyArgs();
        buildParallelCalculationTree = programArgsProcessor.shouldBuildParallelCalculationTree();
        showWarnings = programArgsProcessor.shouldShowWarnings();
    }

    public Configuration(
            boolean showVerboseOutput,
            boolean shouldFixExpressions,
            BoolArg optimizationArg,
            List<PropertyArg> propertyArgs,
            boolean buildParallelCalculationTree,
            boolean showWarnings
    ) {
        this.showVerboseOutput = showVerboseOutput;
        this.shouldFixExpressions = shouldFixExpressions;
        this.optimizationArg = optimizationArg;
        this.propertyArgs = propertyArgs;
        this.buildParallelCalculationTree = buildParallelCalculationTree;
        this.showWarnings = showWarnings;
    }

    public boolean shouldShowVerboseOutput() {
        return showVerboseOutput;
    }

    public boolean shouldFixExpressions() {
        return shouldFixExpressions;
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

    public boolean shouldShowWarnings() {
        return showWarnings;
    }
}
