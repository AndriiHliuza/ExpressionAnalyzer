package org.pzks.parsers.systems.dataflow;

import org.pzks.utils.trees.NaryTreeNode;

public abstract class SystemOperationRecognizer {
    public static SystemOperationType recognize(NaryTreeNode naryTreeNode) {
        return switch (naryTreeNode.getValue()) {
            case String operation when operation.matches("\\+") -> SystemOperationType.ADDITION;
            case String operation when operation.matches("-") -> SystemOperationType.SUBTRACTION;
            case String operation when operation.matches("\\*") -> SystemOperationType.MULTIPLICATION;
            case String operation when operation.matches("/") -> SystemOperationType.DIVISION;
            case String operation when operation.matches("^\\p{Alpha}+\\w*\\s*\\(.*\\)\\s*$") -> SystemOperationType.FUNCTION;
            default -> throw new IllegalStateException("Unexpected value: " + naryTreeNode.getValue());
        };
    }
}
