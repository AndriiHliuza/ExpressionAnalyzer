package org.pzks.parsers.dataflow;

import org.pzks.utils.trees.NaryTreeNode;

public class SystemOperation {
    private SystemOperationType systemOperationType;
    private NaryTreeNode naryTreeNode;
    private int startingTact;

    private SystemThread systemThread;
    private MemoryBank memoryBank;
}
