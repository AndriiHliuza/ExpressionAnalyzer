package org.pzks.parsers.systems.dataflow;

public enum SystemOperationType {
    ADDITION(2),
    SUBTRACTION(3),
    MULTIPLICATION(4),
    DIVISION(8),
    FUNCTION(9),
    READ(1),
    WRITE(1);

    private final int numberOfTimeUnitsPerOperation;

    SystemOperationType(int numberOfTimeUnitsPerOperation) {
        this.numberOfTimeUnitsPerOperation = numberOfTimeUnitsPerOperation;
    }

    public int getNumberOfTimeUnitsPerOperation() {
        return numberOfTimeUnitsPerOperation;
    }
}
