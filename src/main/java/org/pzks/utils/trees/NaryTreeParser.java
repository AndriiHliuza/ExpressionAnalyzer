package org.pzks.utils.trees;

import java.util.ArrayList;
import java.util.List;

public class NaryTreeParser {
    private final NaryTreeNode rootNode;

    List<List<NaryTreeNode>> levelsOfTreeNodes = new ArrayList<>();


    public NaryTreeParser(NaryTreeNode rootNode) {
        this.rootNode = rootNode;
        process();
    }

    private void process() {
        fillLevelsOfTreeNodes();
        setNumbersToNodes();
    }

    public void addNumberInfoToValueInTreeNodes() {
        addInfoToValueInTreeNode(rootNode);
    }

    private void addInfoToValueInTreeNode(NaryTreeNode naryTreeNode) {
        if (naryTreeNode != null) {
            naryTreeNode.setValue(naryTreeNode.getValue() + " [" + naryTreeNode.getNumber() + "]");
            naryTreeNode.getChildren().forEach(this::addInfoToValueInTreeNode);
        }
    }

    private void setNumbersToNodes() {
        int number = 1;
        for (List<NaryTreeNode> naryTreeNodeList : levelsOfTreeNodes) {
            for (NaryTreeNode naryTreeNode : naryTreeNodeList) {
                naryTreeNode.setNumber(number);
                number++;
            }
        }
    }

    private void fillLevelsOfTreeNodes() {
        int lastLevelNumber = rootNode.getMaxHeight() + 1;

        for (int i = lastLevelNumber; i > 0; i--) {
            levelsOfTreeNodes.add(findTreeNodesOfSpecifiedLevel(rootNode, i));
        }
    }

    private List<NaryTreeNode> findTreeNodesOfSpecifiedLevel(NaryTreeNode naryTreeNode, int level) {
        List<NaryTreeNode> treeNodesOfSpecifiedLevel = new ArrayList<>();
        findTreeNodesOfSpecifiedLevel(naryTreeNode, level, treeNodesOfSpecifiedLevel);
        return treeNodesOfSpecifiedLevel;
    }

    private void findTreeNodesOfSpecifiedLevel(NaryTreeNode naryTreeNode, int level, List<NaryTreeNode> treeNodesOfSpecifiedLevel) {
        if (naryTreeNode != null) {
            if (naryTreeNode.getLevel() == level) {
                treeNodesOfSpecifiedLevel.add(naryTreeNode);
            } else {
                naryTreeNode.getChildren().forEach(node -> findTreeNodesOfSpecifiedLevel(node, level, treeNodesOfSpecifiedLevel));
            }
        }
    }


    public List<List<NaryTreeNode>> getLevelsOfTreeNodes() {
        return levelsOfTreeNodes;
    }

    public NaryTreeNode getRootNode() {
        return rootNode;
    }
}
