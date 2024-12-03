package org.pzks.parsers.dataflow;

import org.pzks.utils.trees.NaryTreeNode;
import org.pzks.utils.trees.NaryTreeParser;

import java.util.ArrayList;
import java.util.List;

public class DataflowSystem {
    private NaryTreeNode naryTreeNode = new NaryTreeNode();
    private List<List<NaryTreeNode>> levelsOfTreeNodes;

    private List<SystemThread> systemThreads = new ArrayList<>();
    private List<MemoryBank> memoryBanks = new ArrayList<>();

    public DataflowSystem(NaryTreeNode naryTreeNode) {
        this.naryTreeNode = naryTreeNode;
        preprocess();
        process();
    }

    private void process() {

    }

    private void preprocess() {
        NaryTreeParser naryTreeParser = new NaryTreeParser(naryTreeNode);
        naryTreeNode = naryTreeParser.getRootNode();
        levelsOfTreeNodes = naryTreeParser.getLevelsOfTreeNodes();
    }

    public NaryTreeNode getNaryTreeNode() {
        return naryTreeNode;
    }

    public List<List<NaryTreeNode>> getLevelsOfTreeNodes() {
        return levelsOfTreeNodes;
    }
}
