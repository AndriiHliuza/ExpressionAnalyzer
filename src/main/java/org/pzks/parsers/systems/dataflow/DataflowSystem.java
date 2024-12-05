package org.pzks.parsers.systems.dataflow;

import org.pzks.utils.Color;
import org.pzks.utils.trees.NaryTreeNode;
import org.pzks.utils.trees.NaryTreeParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataflowSystem {
    private NaryTreeNode naryTreeNode;
    private List<List<NaryTreeNode>> levelsOfTreeNodes;

    private NaryTreeParser naryTreeParser;

    private final List<SystemProcessor> systemProcessors = List.of(
            new SystemProcessor(1),
            new SystemProcessor(2)
    );
    private final MemoryBank memoryBank = new MemoryBank();

    private SystemMetrics systemMetrics;

    public DataflowSystem(NaryTreeNode naryTreeNode) throws CloneNotSupportedException {
        if (!(naryTreeNode == null || naryTreeNode.getValue() == null || naryTreeNode.getChildren() == null || naryTreeNode.getChildren().isEmpty())) {
            this.naryTreeNode = naryTreeNode;

            preProcess();
            process();

            systemMetrics = new SystemMetrics(systemProcessors, naryTreeNode);

            postProcess();

        }
    }

    public void displayDiagram() {
        SystemProcessor firstProcessor = systemProcessors.getFirst();
        SystemProcessor secondProcessor = systemProcessors.getLast();

        int maxSize = Math.max(memoryBank.size(), Math.max(firstProcessor.size(), secondProcessor.size()));

        int numberOfClockCyclesColumnLength = Math.max(String.valueOf(maxSize).length(), "Cef".length());

        int firstProcessorMaxColumnWidth = getMaxWidthOfColumnOfSystemOperationsList(firstProcessor);
        int secondProcessorMaxColumnWidth = getMaxWidthOfColumnOfSystemOperationsList(secondProcessor);
        int memoryBankMaxColumnWidth = getMaxWidthOfColumnOfSystemOperationsList(memoryBank);

        int maxColumnWidth = Math.max(memoryBankMaxColumnWidth, Math.max(firstProcessorMaxColumnWidth, secondProcessorMaxColumnWidth));

        String frame = "-".repeat(numberOfClockCyclesColumnLength + 4) +
                " ".repeat(5) +
                "-".repeat(maxColumnWidth * 2 + 7) +
                " ".repeat(10) +
                "-".repeat(maxColumnWidth + 4);
        System.out.println(frame);
        System.out.printf("| %-" + numberOfClockCyclesColumnLength + "s |" +
                        " ".repeat(5) +
                        "| %-" + maxColumnWidth + "s | %-" + maxColumnWidth + "s |" +
                        " ".repeat(10) +
                        "| %-" + maxColumnWidth + "s |\n",
                "T",
                "P1",
                "P2",
                "B"
        );
        System.out.println(frame + "\n");
        System.out.println(frame);

        for (int i = 0; i < maxSize; i++) {
            int clockCycleColumn = (i + 1);
            String firstProcessorColumn = i < firstProcessor.size() && firstProcessor.get(i) != null ? firstProcessor.get(i).toString() : "";
            String secondProcessorColumn = i < secondProcessor.size() && secondProcessor.get(i) != null ? secondProcessor.get(i).toString() : "";
            String memoryBankColumn = i < memoryBank.size() && memoryBank.get(i) != null ? memoryBank.get(i).toString() : "";

            String firstProcessorColumnColor;
            String secondProcessorColumnColor;
            String memoryBankColumnColor;

            firstProcessorColumnColor = getOperationColor(firstProcessorColumn);

            secondProcessorColumnColor = getOperationColor(secondProcessorColumn);

            if (memoryBankColumn.contains("R")) {
                memoryBankColumnColor = Color.BLUE.getAnsiValue();
            } else if (memoryBankColumn.contains("W")) {
                memoryBankColumnColor = Color.RED.getAnsiValue();
            } else {
                memoryBankColumnColor = Color.DEFAULT.getAnsiValue();
            }


            System.out.printf("| %-" + numberOfClockCyclesColumnLength + "s |" +
                            " ".repeat(5) +
                            "| %s%-" + maxColumnWidth + "s%s | %s%-" + maxColumnWidth + "s%s |" +
                            " ".repeat(10) +
                            "| %s%-" + maxColumnWidth + "s%s | \n",
                    clockCycleColumn,
                    firstProcessorColumnColor,
                    firstProcessorColumn,
                    Color.DEFAULT.getAnsiValue(),
                    secondProcessorColumnColor,
                    secondProcessorColumn,
                    Color.DEFAULT.getAnsiValue(),
                    memoryBankColumnColor,
                    memoryBankColumn,
                    Color.DEFAULT.getAnsiValue()
            );
            System.out.println(frame);
        }

        System.out.println();
        String efficiencyCoefficientRowDataFrame = "-".repeat(numberOfClockCyclesColumnLength + 4) +
                " ".repeat(5) +
                "-".repeat(maxColumnWidth * 2 + 7);
        System.out.println(efficiencyCoefficientRowDataFrame);
        System.out.printf("| %s%-" + numberOfClockCyclesColumnLength + "s%s |" +
                        " ".repeat(5) +
                        "| %s%-" + maxColumnWidth + "s%s | %s%-" + maxColumnWidth + "s%s |\n",
                Color.YELLOW.getAnsiValue(),
                "Cef",
                Color.DEFAULT.getAnsiValue(),
                Color.YELLOW.getAnsiValue(),
                String.format("%.2f", systemMetrics.getFirstProcessorEfficiencyCoefficient()),
                Color.DEFAULT.getAnsiValue(),
                Color.YELLOW.getAnsiValue(),
                String.format("%.2f", systemMetrics.getSecondProcessorEfficiencyCoefficient()),
                Color.DEFAULT.getAnsiValue()
        );
        System.out.println(efficiencyCoefficientRowDataFrame);
    }

    private String getOperationColor(String secondProcessorColumn) {
        String secondProcessorColumnColor;
        if (secondProcessorColumn.contains("R")) {
            secondProcessorColumnColor = Color.BLUE.getAnsiValue();
        } else if (secondProcessorColumn.contains("W")) {
            secondProcessorColumnColor = Color.RED.getAnsiValue();
        } else if (!secondProcessorColumn.isBlank()) {
            secondProcessorColumnColor = Color.GREEN.getAnsiValue();
        } else {
            secondProcessorColumnColor = Color.DEFAULT.getAnsiValue();
        }
        return secondProcessorColumnColor;
    }

    private static int getMaxWidthOfColumnOfSystemOperationsList(List<SystemOperation> systemOperations) {
        int maxWidth = 2; // Minimum width for header
        for (SystemOperation systemOperation : systemOperations) {
            if (systemOperation != null) {
                maxWidth = Math.max(maxWidth, systemOperation.toString().length());
            }
        }
        return maxWidth;
    }

    private void preProcess() {
        naryTreeParser = new NaryTreeParser(naryTreeNode);
        naryTreeNode = naryTreeParser.getRootNode();
        levelsOfTreeNodes = naryTreeParser.getLevelsOfTreeNodes();
    }

    private void process() throws CloneNotSupportedException {
        for (List<NaryTreeNode> naryTreeNodesOnLevel : levelsOfTreeNodes) {
            for (NaryTreeNode naryTreeNode : naryTreeNodesOnLevel) {
                List<NaryTreeNode> children = naryTreeNode.getChildren();

                if (children == null || children.isEmpty()) {
                    SystemOperation readOperation = new SystemOperation(naryTreeNode, SystemOperationType.READ);
                    List<SystemOperation> systemOperations = generateSystemOperationsForProvidedTreeNode(naryTreeNode);
                    SystemOperation writeOperation = new SystemOperation(naryTreeNode, SystemOperationType.WRITE);

                    SystemProcessor systemProcessor = findSystemProcessorWithTheLeastNumberOfOccupiedClockCycle();
                    writeFirstGeneratedSystemOperationsToMemoryAndProcessor(systemProcessor, readOperation, systemOperations, writeOperation);
                } else {
                    SystemProcessor systemProcessorToUse = getSystemProcessorThatProvidesTheLeastNumberOfClockCyclesForCurrentOperation(children);

                    if (systemProcessorToUse == null) {
                        systemProcessorToUse = findSystemProcessorWithTheBiggestNumberOfSpecifiedChildren(children);

                        if (systemProcessorToUse == null) {
                            systemProcessorToUse = findSystemProcessorWithTheLeastNumberOfOccupiedClockCycle();
                        }
                    }

                    SystemProcessor systemProcessorToReadOperationsResultsFrom = findSystemProcessorToReadOperationsResultsFrom(systemProcessorToUse);
                    List<NaryTreeNode> childrenToRead = findChildrenToRead(systemProcessorToReadOperationsResultsFrom, children);
                    List<SystemOperation> readOperations = childrenToRead.stream()
                            .map(treeNode -> new SystemOperation(treeNode, SystemOperationType.READ))
                            .toList();
                    List<SystemOperation> systemOperations = generateSystemOperationsForProvidedTreeNode(naryTreeNode);
                    SystemOperation writeOperation = new SystemOperation(naryTreeNode, SystemOperationType.WRITE);

                    int indexOfClockCycleToStartFrom = Math.max(systemProcessorToUse.size(), findIndexOfClockCycleAfterWhichLoadingNewTaskIsPossible(children));

                    writeFollowingGeneratedSystemOperationsToMemoryAndProcessorBased(
                            indexOfClockCycleToStartFrom,
                            systemProcessorToUse,
                            readOperations,
                            systemOperations,
                            writeOperation
                    );

                }

                removeTrailingNullsFromSystemProcessorsAndMemoryBank();
            }
        }
    }

    private void postProcess() {
        naryTreeParser.addNumberInfoToValueInTreeNodes();
    }

    private List<SystemOperation> generateSystemOperationsForProvidedTreeNode(NaryTreeNode naryTreeNode) {
        SystemOperationType systemOperationType = SystemOperationRecognizer.recognize(naryTreeNode);
        return Collections.nCopies(systemOperationType.getNumberOfTimeUnitsPerOperation(), new SystemOperation(naryTreeNode, systemOperationType));
    }

    private SystemProcessor findSystemProcessorWithTheLeastNumberOfOccupiedClockCycle() {
        SystemProcessor systemProcessorWithTheLeastNumberOfUsedClockCycles;

        int numberOfOccupiedClockCyclesInFirstProcessor = 0;
        for (SystemOperation systemOperation : systemProcessors.getFirst()) {
            if (systemOperation != null) {
                numberOfOccupiedClockCyclesInFirstProcessor++;
            }
        }

        int numberOfOccupiedClockCyclesInSecondProcessor = 0;
        for (SystemOperation systemOperation : systemProcessors.getLast()) {
            if (systemOperation != null) {
                numberOfOccupiedClockCyclesInSecondProcessor++;
            }
        }

        if (numberOfOccupiedClockCyclesInFirstProcessor < numberOfOccupiedClockCyclesInSecondProcessor) {
            systemProcessorWithTheLeastNumberOfUsedClockCycles = systemProcessors.getFirst();
        } else if (numberOfOccupiedClockCyclesInSecondProcessor < numberOfOccupiedClockCyclesInFirstProcessor) {
            systemProcessorWithTheLeastNumberOfUsedClockCycles = systemProcessors.getLast();
        } else {
            SystemProcessor systemProcessorToTotalLeastNumberOfOccupiedClockCycles = systemProcessors.getFirst();

            for (SystemProcessor systemProcessor : systemProcessors) {
                if (systemProcessor.size() < systemProcessorToTotalLeastNumberOfOccupiedClockCycles.size()) {
                    systemProcessorToTotalLeastNumberOfOccupiedClockCycles = systemProcessor;
                }
            }
            systemProcessorWithTheLeastNumberOfUsedClockCycles = systemProcessorToTotalLeastNumberOfOccupiedClockCycles;
        }
        return systemProcessorWithTheLeastNumberOfUsedClockCycles;
    }

    private void writeFirstGeneratedSystemOperationsToMemoryAndProcessor(
            SystemProcessor systemProcessor,
            SystemOperation readOperation,
            List<SystemOperation> systemOperations,
            SystemOperation writeOperation
    ) {
        int indexOfClockCycleToUseInMemoryBank = systemProcessor.size();
        while (!isMemoryBankClockCycleFree(indexOfClockCycleToUseInMemoryBank)) {
            indexOfClockCycleToUseInMemoryBank++;
        }

        int numberOfExtraClockCyclesToOccupy = indexOfClockCycleToUseInMemoryBank - systemProcessor.size();
        systemProcessor.addAll(Collections.nCopies(numberOfExtraClockCyclesToOccupy, null));
        systemProcessor.add(readOperation);
        systemProcessor.addAll(systemOperations);

        if (indexOfClockCycleToUseInMemoryBank >= memoryBank.size()) {
            memoryBank.add(readOperation);
        } else if (memoryBank.get(indexOfClockCycleToUseInMemoryBank) == null) {
            memoryBank.set(indexOfClockCycleToUseInMemoryBank, readOperation);
        }


        indexOfClockCycleToUseInMemoryBank = systemProcessor.size();
        while (!isMemoryBankClockCycleFree(indexOfClockCycleToUseInMemoryBank)) {
            indexOfClockCycleToUseInMemoryBank++;
        }

        numberOfExtraClockCyclesToOccupy = indexOfClockCycleToUseInMemoryBank - systemProcessor.size();
        systemProcessor.addAll(Collections.nCopies(numberOfExtraClockCyclesToOccupy, null));
        systemProcessor.add(writeOperation);

        if (indexOfClockCycleToUseInMemoryBank >= memoryBank.size()) {
            while (memoryBank.size() != indexOfClockCycleToUseInMemoryBank) {
                memoryBank.add(null);
            }
            memoryBank.add(writeOperation);
        } else if (memoryBank.get(indexOfClockCycleToUseInMemoryBank) == null) {
            memoryBank.set(indexOfClockCycleToUseInMemoryBank, writeOperation);
        }

    }


    private int countTreeNodesInSystemProcessor(SystemProcessor systemProcessor, List<NaryTreeNode> treeNodes) {
        int count = 0;
        List<NaryTreeNode> alreadyCountedChildren = new ArrayList<>();
        for (SystemOperation systemOperation : systemProcessor) {
            if (systemOperation != null) {
                NaryTreeNode naryTreeNode = systemOperation.getNaryTreeNode();
                boolean treeNodesContainsOperation = treeNodes.stream().anyMatch(child -> child.getNumber() == naryTreeNode.getNumber());
                boolean isInAlreadyCountedChildren = alreadyCountedChildren.stream().anyMatch(child -> child.getNumber() == naryTreeNode.getNumber());
                if (treeNodesContainsOperation && !isInAlreadyCountedChildren) {
                    alreadyCountedChildren.add(systemOperation.getNaryTreeNode());
                    alreadyCountedChildren.add(systemOperation.getNaryTreeNode());
                }
            }
        }

        return count;
    }

    private SystemProcessor findSystemProcessorWithTheBiggestNumberOfSpecifiedChildren(List<NaryTreeNode> children) {
        int numberOfChildrenInFirstProcessor = countTreeNodesInSystemProcessor(systemProcessors.getFirst(), children);
        int numberOfChildrenInSecondProcessor = countTreeNodesInSystemProcessor(systemProcessors.getLast(), children);

        if (numberOfChildrenInFirstProcessor > numberOfChildrenInSecondProcessor) {
            return systemProcessors.getFirst();
        } else if (numberOfChildrenInFirstProcessor < numberOfChildrenInSecondProcessor) {
            return systemProcessors.getLast();
        } else {
            return null;
        }
    }

    private SystemProcessor findSystemProcessorToReadOperationsResultsFrom(SystemProcessor systemProcessorToReadOperationsTo) {
        return switch (systemProcessorToReadOperationsTo.getNumber()) {
            case 1 -> systemProcessors.getLast();
            case 2 -> systemProcessors.getFirst();
            default ->
                    throw new IllegalStateException("Unexpected value: " + systemProcessorToReadOperationsTo.getNumber());
        };
    }

    private List<NaryTreeNode> findChildrenToRead(SystemProcessor systemProcessorWithChildrenToRead, List<NaryTreeNode> children) {
        List<NaryTreeNode> childrenToRead = new ArrayList<>();
        for (SystemOperation systemOperation : systemProcessorWithChildrenToRead) {
            if (systemOperation != null) {
                NaryTreeNode naryTreeNode = systemOperation.getNaryTreeNode();
                boolean operationIsInChildren = children.stream().anyMatch(child -> child.getNumber() == naryTreeNode.getNumber());
                if (operationIsInChildren && !childrenToRead.contains(naryTreeNode)) {
                    childrenToRead.add(naryTreeNode);
                }
            }
        }

        return childrenToRead;
    }

    private int findIndexOfClockCycleAfterWhichLoadingNewTaskIsPossible(List<NaryTreeNode> children) {
        int indexOfClockCycleAfterWhichLoadingNewTaskIsPossible = 0;
        for (SystemProcessor systemProcessor : systemProcessors) {
            for (int i = 0; i < systemProcessor.size(); i++) {
                SystemOperation systemOperation = systemProcessor.get(i);
                if (systemOperation != null) {
                    NaryTreeNode naryTreeNode = systemOperation.getNaryTreeNode();
                    boolean operationIsInChildren = children.stream().anyMatch(child -> child.getNumber() == naryTreeNode.getNumber());
                    if (operationIsInChildren && i > indexOfClockCycleAfterWhichLoadingNewTaskIsPossible) {
                        indexOfClockCycleAfterWhichLoadingNewTaskIsPossible = i;
                    }
                }
            }
        }
        return indexOfClockCycleAfterWhichLoadingNewTaskIsPossible;
    }

    private void fillSystemProcessorUpToSpecifiedClockCycleIndex(SystemProcessor systemProcessor, int clockCycleIndex) {
        while (systemProcessor.size() < clockCycleIndex) {
            systemProcessor.add(null);
        }
    }

    private void writeFollowingGeneratedSystemOperationsToMemoryAndProcessorBased(
            int indexOfClockCycleToStartFrom,
            SystemProcessor systemProcessor,
            List<SystemOperation> readOperations,
            List<SystemOperation> systemOperations,
            SystemOperation writeOperation
    ) {
        int copyOfIndexOfClockCycleToStartFrom = indexOfClockCycleToStartFrom;

        fillSystemProcessorUpToSpecifiedClockCycleIndex(systemProcessor, indexOfClockCycleToStartFrom);

        for (SystemOperation readOperation : readOperations) {
            while (!isMemoryBankClockCycleFree(indexOfClockCycleToStartFrom)) {
                indexOfClockCycleToStartFrom++;
            }

            int numberOfExtraClockCyclesToOccupy = indexOfClockCycleToStartFrom - systemProcessor.size();
            systemProcessor.addAll(Collections.nCopies(numberOfExtraClockCyclesToOccupy, null));
            systemProcessor.add(readOperation);
            indexOfClockCycleToStartFrom++;
        }
        systemProcessor.addAll(systemOperations);


        indexOfClockCycleToStartFrom = copyOfIndexOfClockCycleToStartFrom;
        for (SystemOperation readOperation : readOperations) {

            while (!isMemoryBankClockCycleFree(indexOfClockCycleToStartFrom)) {
                indexOfClockCycleToStartFrom++;
            }

            if (indexOfClockCycleToStartFrom >= memoryBank.size()) {
                memoryBank.add(readOperation);
            } else if (memoryBank.get(indexOfClockCycleToStartFrom) == null) {
                memoryBank.set(indexOfClockCycleToStartFrom, readOperation);
            }

            indexOfClockCycleToStartFrom++;
        }


        indexOfClockCycleToStartFrom = systemProcessor.size();
        while (!isMemoryBankClockCycleFree(indexOfClockCycleToStartFrom)) {
            indexOfClockCycleToStartFrom++;
        }
        int numberOfExtraClockCyclesToOccupy = indexOfClockCycleToStartFrom - systemProcessor.size();
        systemProcessor.addAll(Collections.nCopies(numberOfExtraClockCyclesToOccupy, null));
        systemProcessor.add(writeOperation);

        if (indexOfClockCycleToStartFrom >= memoryBank.size()) {
            while (memoryBank.size() != indexOfClockCycleToStartFrom) {
                memoryBank.add(null);
            }
            memoryBank.add(writeOperation);
        } else if (memoryBank.get(indexOfClockCycleToStartFrom) == null) {
            memoryBank.set(indexOfClockCycleToStartFrom, writeOperation);
        }
    }

    private void removeTrailingNullsFromSystemProcessorsAndMemoryBank() {
        for (SystemProcessor systemProcessor : systemProcessors) {
            removeTrailingNullsFromSystemOperationsList(systemProcessor);
        }
        removeTrailingNullsFromSystemOperationsList(memoryBank);
    }

    private void removeTrailingNullsFromSystemOperationsList(List<SystemOperation> systemOperations) {
        int lastIndex = systemOperations.size() - 1;
        while (lastIndex >= 0 && systemOperations.get(lastIndex) == null) {
            systemOperations.remove(lastIndex);
            lastIndex--;
        }
    }

    private boolean isMemoryBankClockCycleFree(int indexOfMemoryBankClockCycle) {
        return indexOfMemoryBankClockCycle >= memoryBank.size() || memoryBank.get(indexOfMemoryBankClockCycle) == null;
    }

    public NaryTreeNode getNaryTreeNode() {
        return naryTreeNode;
    }

    private SystemProcessor getSystemProcessorThatProvidesTheLeastNumberOfClockCyclesForCurrentOperation(List<NaryTreeNode> children) throws CloneNotSupportedException {
        int p1Cycles = processByUsingTestInstanceOfCurrentStateAndReturnTotalNumberOfResultingClockCycles(children, systemProcessors.getFirst());
        int p2Cycles = processByUsingTestInstanceOfCurrentStateAndReturnTotalNumberOfResultingClockCycles(children, systemProcessors.getLast());

        if (p1Cycles < p2Cycles) {
            return systemProcessors.getFirst();
        } else if (p2Cycles < p1Cycles) {
            return systemProcessors.getLast();
        } else {
            return null;
        }
    }

    private int processByUsingTestInstanceOfCurrentStateAndReturnTotalNumberOfResultingClockCycles(List<NaryTreeNode> children, SystemProcessor systemProcessorToUse) throws CloneNotSupportedException {
        SystemProcessor systemProcessorToReadOperationsResultsFrom = findSystemProcessorToReadOperationsResultsFrom(systemProcessorToUse);
        List<NaryTreeNode> childrenToRead = findChildrenToRead(systemProcessorToReadOperationsResultsFrom, children);
        List<SystemOperation> readOperations = childrenToRead.stream()
                .map(treeNode -> new SystemOperation(treeNode, SystemOperationType.READ))
                .toList();
        List<SystemOperation> systemOperations = generateSystemOperationsForProvidedTreeNode(naryTreeNode);
        SystemOperation writeOperation = new SystemOperation(naryTreeNode, SystemOperationType.WRITE);

        int indexOfClockCycleToStartFrom = Math.max(systemProcessorToUse.size(), findIndexOfClockCycleAfterWhichLoadingNewTaskIsPossible(children));

        SystemProcessor systemProcessorToUseAfterWritingResults = getSystemProcessorAfterOperations(
                indexOfClockCycleToStartFrom,
                systemProcessorToUse,
                readOperations,
                systemOperations,
                writeOperation
        );
        return getTotalNumberOfOccupiedClockUnits(systemProcessorToUseAfterWritingResults, systemProcessorToReadOperationsResultsFrom.clone());
    }

    private SystemProcessor getSystemProcessorAfterOperations(
            int indexOfClockCycleToStartFrom,
            SystemProcessor systemProcessor,
            List<SystemOperation> readOperations,
            List<SystemOperation> systemOperations,
            SystemOperation writeOperation
    ) throws CloneNotSupportedException {
        SystemProcessor systemProcessorCopy = systemProcessor.clone();
        MemoryBank memoryBankCopy = memoryBank.clone();

        int copyOfIndexOfClockCycleToStartFrom = indexOfClockCycleToStartFrom;

        fillSystemProcessorUpToSpecifiedClockCycleIndex(systemProcessorCopy, indexOfClockCycleToStartFrom);

        for (SystemOperation readOperation : readOperations) {
            while (!isMemoryBankClockCycleFree(indexOfClockCycleToStartFrom, memoryBankCopy)) {
                indexOfClockCycleToStartFrom++;
            }

            int numberOfExtraClockCyclesToOccupy = indexOfClockCycleToStartFrom - systemProcessorCopy.size();
            systemProcessorCopy.addAll(Collections.nCopies(numberOfExtraClockCyclesToOccupy, null));
            systemProcessorCopy.add(readOperation);
            indexOfClockCycleToStartFrom++;
        }
        systemProcessorCopy.addAll(systemOperations);


        indexOfClockCycleToStartFrom = copyOfIndexOfClockCycleToStartFrom;
        for (SystemOperation readOperation : readOperations) {

            while (!isMemoryBankClockCycleFree(indexOfClockCycleToStartFrom, memoryBankCopy)) {
                indexOfClockCycleToStartFrom++;
            }

            if (indexOfClockCycleToStartFrom >= memoryBankCopy.size()) {
                memoryBankCopy.add(readOperation);
            } else if (memoryBankCopy.get(indexOfClockCycleToStartFrom) == null) {
                memoryBankCopy.set(indexOfClockCycleToStartFrom, readOperation);
            }

            indexOfClockCycleToStartFrom++;
        }


        indexOfClockCycleToStartFrom = systemProcessorCopy.size();
        while (!isMemoryBankClockCycleFree(indexOfClockCycleToStartFrom, memoryBankCopy)) {
            indexOfClockCycleToStartFrom++;
        }
        int numberOfExtraClockCyclesToOccupy = indexOfClockCycleToStartFrom - systemProcessorCopy.size();
        systemProcessorCopy.addAll(Collections.nCopies(numberOfExtraClockCyclesToOccupy, null));
        systemProcessorCopy.add(writeOperation);

        if (indexOfClockCycleToStartFrom >= memoryBankCopy.size()) {
            while (memoryBankCopy.size() != indexOfClockCycleToStartFrom) {
                memoryBankCopy.add(null);
            }
            memoryBankCopy.add(writeOperation);
        } else if (memoryBankCopy.get(indexOfClockCycleToStartFrom) == null) {
            memoryBankCopy.set(indexOfClockCycleToStartFrom, writeOperation);
        }
        return systemProcessorCopy;
    }

    private boolean isMemoryBankClockCycleFree(int indexOfMemoryBankClockCycle, MemoryBank memoryBank) {
        return indexOfMemoryBankClockCycle >= memoryBank.size() || memoryBank.get(indexOfMemoryBankClockCycle) == null;
    }

    private int getTotalNumberOfOccupiedClockUnits(SystemProcessor firstSystemProcessor, SystemProcessor secondSystemProcessor) throws CloneNotSupportedException {
        firstSystemProcessor = firstSystemProcessor.clone();
        secondSystemProcessor = secondSystemProcessor.clone();

        removeTrailingNullsFromSystemOperationsList(firstSystemProcessor);
        removeTrailingNullsFromSystemOperationsList(secondSystemProcessor);
        return Math.max(firstSystemProcessor.size(), secondSystemProcessor.size());
    }

    public SystemMetrics getSystemMetrics() {
        return systemMetrics;
    }
}
