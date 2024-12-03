package org.pzks.parsers.dataflow;

public enum SystemOperationType {
    ADDITION(2),
    SUBTRACTION(3),
    MULTIPLICATION(4),
    DIVISION(8),
    FUNCTION(9),
    READ(1),
    WRITE(1);

    private final int numberOfTimeUnits;

    SystemOperationType(int numberOfTimeUnits) {
        this.numberOfTimeUnits = numberOfTimeUnits;
    }

    public int getNumberOfTimeUnits() {
        return numberOfTimeUnits;
    }
}
