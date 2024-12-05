package org.pzks.parsers.systems.dataflow;

import org.pzks.utils.trees.NaryTreeNode;

public class SystemOperation {
    private NaryTreeNode naryTreeNode;
    private SystemOperationType systemOperationType;

    public SystemOperation() {}

    public SystemOperation(NaryTreeNode naryTreeNode) {
        this.naryTreeNode = naryTreeNode;
    }

    public SystemOperation(SystemOperationType systemOperationType) {
        this.systemOperationType = systemOperationType;
    }

    public SystemOperation(NaryTreeNode naryTreeNode, SystemOperationType systemOperationType) {
        this.naryTreeNode = naryTreeNode;
        this.systemOperationType = systemOperationType;
    }

    public SystemOperationType getSystemOperationType() {
        return systemOperationType;
    }

    public void setSystemOperationType(SystemOperationType systemOperationType) {
        this.systemOperationType = systemOperationType;
    }

    public NaryTreeNode getNaryTreeNode() {
        return naryTreeNode;
    }

    public void setNaryTreeNode(NaryTreeNode naryTreeNode) {
        this.naryTreeNode = naryTreeNode;
    }

    @Override
    public String toString() {
        return switch (systemOperationType) {
            case READ -> "R" + " [" + naryTreeNode.getNumber() + "]";
            case WRITE -> "W" + " [" + naryTreeNode.getNumber() + "]";
            default -> naryTreeNode.getValue();
        };
    }
}
