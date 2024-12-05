package org.pzks.parsers.systems.dataflow;

import org.pzks.utils.trees.NaryTreeNode;

import java.util.ArrayList;
import java.util.List;

public class SystemMetrics {
    private int sequentialExecutionTime;
    private int executionTime; // parallel execution time
    private double speedupCoefficient;
    private double efficiencyCoefficient;

    private double firstProcessorEfficiencyCoefficient;
    private double secondProcessorEfficiencyCoefficient;

    private List<SystemProcessor> systemProcessors;
    private NaryTreeNode rootNode;

    public SystemMetrics(List<SystemProcessor> systemProcessors, NaryTreeNode rootNode) {
        this.systemProcessors = systemProcessors;
        this.rootNode = rootNode;
        calculate();
    }

    private void calculate() {
        calculateSequentialExecutionTime();
        calculateParallelExecutionTime();
        calculateSpeedupCoefficient();
        calculateEfficiencyCoefficient();

        calculateEfficiencyCoefficientForFirstProcessor();
        calculateEfficiencyCoefficientForSecondProcessor();
    }

    private void calculateSequentialExecutionTime() {
        int sequentialTime = 0;

        List<SystemOperation> systemOperations = getAllSystemOperations();
        for (SystemOperation systemOperation : systemOperations) {
            sequentialTime += systemOperation.getSystemOperationType().getNumberOfTimeUnitsPerOperation() + 1;
        }

        sequentialExecutionTime = sequentialTime;
    }

    private void calculateParallelExecutionTime() {
        int parallelExecutionTime = 0;
        for (SystemProcessor systemProcessor : systemProcessors) {
            if (systemProcessor.size() > parallelExecutionTime) {
                parallelExecutionTime = systemProcessor.size();
            }
        }

        executionTime = parallelExecutionTime;
    }

    private void calculateSpeedupCoefficient() {
        speedupCoefficient = (double) sequentialExecutionTime / executionTime;
    }

    private void calculateEfficiencyCoefficient() {
        efficiencyCoefficient = speedupCoefficient / 2;
    }

    private void calculateEfficiencyCoefficientForFirstProcessor() {
        SystemProcessor systemProcessor = systemProcessors.getFirst();
        int totalClockCycles = systemProcessor.size();
        int numberOfUsedClockCycles = 0;
        for (SystemOperation systemOperation : systemProcessor) {
            if (systemOperation != null) {
                numberOfUsedClockCycles++;
            }
        }

        firstProcessorEfficiencyCoefficient = (double) numberOfUsedClockCycles / totalClockCycles;
    }

    private void calculateEfficiencyCoefficientForSecondProcessor() {
        SystemProcessor systemProcessor = systemProcessors.getLast();
        int totalClockCycles = systemProcessor.size();
        int numberOfUsedClockCycles = 0;
        for (SystemOperation systemOperation : systemProcessor) {
            if (systemOperation != null) {
                numberOfUsedClockCycles++;
            }
        }

        secondProcessorEfficiencyCoefficient = (double) numberOfUsedClockCycles / totalClockCycles;
    }

    private List<SystemOperation> getAllSystemOperations() {
        List<SystemOperation> systemOperations = new ArrayList<>();
        addSystemOperations(rootNode, systemOperations);
        return systemOperations;
    }

    private void addSystemOperations(NaryTreeNode naryTreeNode, List<SystemOperation> systemOperations) {
        if (naryTreeNode != null) {
            SystemOperationType systemOperationType = SystemOperationRecognizer.recognize(naryTreeNode);
            SystemOperation systemOperation = new SystemOperation(naryTreeNode, systemOperationType);
            systemOperations.add(systemOperation);

            List<NaryTreeNode> children = naryTreeNode.getChildren();
            if (children != null && !children.isEmpty()) {
                for (NaryTreeNode child : children) {
                    addSystemOperations(child, systemOperations);
                }
            }
        }
    }

    public int getSequentialExecutionTime() {
        return sequentialExecutionTime;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public double getSpeedupCoefficient() {
        return speedupCoefficient;
    }

    public double getEfficiencyCoefficient() {
        return efficiencyCoefficient;
    }

    public double getFirstProcessorEfficiencyCoefficient() {
        return firstProcessorEfficiencyCoefficient;
    }

    public double getSecondProcessorEfficiencyCoefficient() {
        return secondProcessorEfficiencyCoefficient;
    }
}
