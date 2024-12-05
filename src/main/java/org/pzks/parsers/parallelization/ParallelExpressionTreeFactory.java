package org.pzks.parsers.parallelization;

import org.pzks.units.SyntaxUnit;
import org.pzks.settings.GlobalSettings;
import org.pzks.utils.trees.BinaryTreeNode;
import org.pzks.utils.trees.NaryTreeNode;
import org.pzks.utils.trees.TreeNode;

import java.util.List;

public class ParallelExpressionTreeFactory {
    private SyntaxUnit syntaxUnit;

    private BinaryTreeNode binaryTreeRootNode;
    private NaryTreeNode naryTreeRootNode;
    private NaryTreeNode operationsTreeRootNode;

    private List<String> warnings;

    public ParallelExpressionTreeFactory(SyntaxUnit syntaxUnit) throws Exception {
        this.syntaxUnit = syntaxUnit;
        build();
    }

    private void build() throws Exception {
        if (GlobalSettings.CONFIGURATION.shouldBuildBinaryParallelCalculationTree()) {
            BinaryParallelExpressionTreeBuilder binaryParallelExpressionTreeBuilder = new BinaryParallelExpressionTreeBuilder(syntaxUnit);
            warnings = binaryParallelExpressionTreeBuilder.getWarnings();
            if (warnings.isEmpty()) {
                binaryTreeRootNode = binaryParallelExpressionTreeBuilder.getRootNode();

                ParallelOperationTreeBuilder parallelOperationTreeBuilder = new ParallelOperationTreeBuilder(syntaxUnit);
                operationsTreeRootNode = parallelOperationTreeBuilder.getRootNode();
            }
        } else {
            NaryParallelExpressionTreeBuilder naryParallelExpressionTreeBuilder = new NaryParallelExpressionTreeBuilder(syntaxUnit);
            naryTreeRootNode = naryParallelExpressionTreeBuilder.getRootNode();

            ParallelOperationTreeBuilder parallelOperationTreeBuilder = new ParallelOperationTreeBuilder(syntaxUnit);
            operationsTreeRootNode = parallelOperationTreeBuilder.getRootNode();
        }

        resetLevelsInTrees();
        setParentsForEachNodeInTrees();
        calculateNewLevelsInTrees();
    }

    private void calculateNewLevelsInTrees() {
        calculateNewLevelsInBinaryTree(binaryTreeRootNode);
        calculateNewLevelsInNaryTree(naryTreeRootNode);
        calculateNewLevelsInNaryTree(operationsTreeRootNode);
    }

    private void calculateNewLevelsInBinaryTree(BinaryTreeNode binaryTreeRootNode) {
        if (binaryTreeRootNode != null) {

            binaryTreeRootNode.setLevel(distanceToRootNodeInTree(binaryTreeRootNode) + 1);

            BinaryTreeNode leftChild = binaryTreeRootNode.getLeftChild();
            BinaryTreeNode rightChild = binaryTreeRootNode.getRightChild();
            calculateNewLevelsInBinaryTree(leftChild);
            calculateNewLevelsInBinaryTree(rightChild);
        }
    }

    private void calculateNewLevelsInNaryTree(NaryTreeNode naryTreeNode) {
        if (naryTreeNode != null) {

            naryTreeNode.setLevel(distanceToRootNodeInTree(naryTreeNode) + 1);

            naryTreeNode.getChildren().forEach(this::calculateNewLevelsInNaryTree);
        }
    }


    private int distanceToRootNodeInTree(TreeNode treeNode) {
        return treeNode.getParent() == null ? 0 : 1 + distanceToRootNodeInTree(treeNode.getParent());
    }

    private void setParentsForEachNodeInTrees() {
        setParentForNodeInBinaryTree(binaryTreeRootNode);
        setParentForNodeInNaryTree(naryTreeRootNode);
        setParentForNodeInNaryTree(operationsTreeRootNode);
    }

    private void setParentForNodeInBinaryTree(BinaryTreeNode binaryTreeNode) {
        if (binaryTreeNode != null) {
            BinaryTreeNode leftChild = binaryTreeNode.getLeftChild();
            BinaryTreeNode rightChild = binaryTreeNode.getRightChild();
            if (leftChild != null) {
                leftChild.setParent(binaryTreeNode);
            }
            if (rightChild != null) {
                rightChild.setParent(binaryTreeNode);
            }

            setParentForNodeInBinaryTree(leftChild);
            setParentForNodeInBinaryTree(rightChild);
        }
    }

    private void setParentForNodeInNaryTree(NaryTreeNode naryTreeNode) {
        if (naryTreeNode != null) {
            naryTreeNode.getChildren().forEach(child -> child.setParent(naryTreeNode));
            naryTreeNode.getChildren().forEach(this::setParentForNodeInNaryTree);
        }
    }

    private void resetLevelsInTrees() {
        resetLevelsInBinaryTree(binaryTreeRootNode);
        resetLevelsInNaryTree(naryTreeRootNode);
        resetLevelsInNaryTree(operationsTreeRootNode);
    }

    private void resetLevelsInBinaryTree(BinaryTreeNode binaryTreeRootNode) {
        if (binaryTreeRootNode != null) {
            binaryTreeRootNode.setLevel(-1);
            BinaryTreeNode leftChild = binaryTreeRootNode.getLeftChild();
            BinaryTreeNode rightChild = binaryTreeRootNode.getRightChild();
            resetLevelsInBinaryTree(leftChild);
            resetLevelsInBinaryTree(rightChild);
        }
    }

    private void resetLevelsInNaryTree(NaryTreeNode naryTreeRootNode) {
        if (naryTreeRootNode != null) {
            naryTreeRootNode.setLevel(-1);
            naryTreeRootNode.getChildren().forEach(this::resetLevelsInNaryTree);
        }
    }

    public SyntaxUnit getSyntaxUnit() {
        return syntaxUnit;
    }

    public void setSyntaxUnit(SyntaxUnit syntaxUnit) {
        this.syntaxUnit = syntaxUnit;
    }

    public BinaryTreeNode getBinaryTreeRootNode() {
        return binaryTreeRootNode;
    }

    public void setBinaryTreeRootNode(BinaryTreeNode binaryTreeRootNode) {
        this.binaryTreeRootNode = binaryTreeRootNode;
    }

    public NaryTreeNode getNaryTreeRootNode() {
        return naryTreeRootNode;
    }

    public void setNaryTreeRootNode(NaryTreeNode naryTreeRootNode) {
        this.naryTreeRootNode = naryTreeRootNode;
    }

    public NaryTreeNode getOperationsTreeRootNode() {
        return operationsTreeRootNode;
    }

    public NaryTreeNode getNumberedOperationsTreeRootNode() {
        return operationsTreeRootNode;
    }

    public void setOperationsTreeRootNode(NaryTreeNode operationsTreeRootNode) {
        this.operationsTreeRootNode = operationsTreeRootNode;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
