package org.pzks.parsers.parallelization;

import org.pzks.parsers.ExpressionParser;
import org.pzks.units.Function;
import org.pzks.units.SyntaxUnit;
import org.pzks.utils.trees.BinaryTreeNode;
import org.pzks.utils.trees.NaryTreeNode;

import java.util.List;

public class NaryParallelExpressionTreeBuilder {
    private final NaryTreeNode rootNode;

    public NaryParallelExpressionTreeBuilder(SyntaxUnit syntaxUnit) throws Exception {
        BinaryParallelExpressionTreeBuilder treeBuilder = new BinaryParallelExpressionTreeBuilder(syntaxUnit);
        BinaryTreeNode binaryTreeRootNode = treeBuilder.getRootNode();
        rootNode = convertBinaryTreeToNaryTree(binaryTreeRootNode);
    }

    public NaryTreeNode getRootNode() {
        return rootNode;
    }

    private NaryTreeNode convertBinaryTreeToNaryTree(BinaryTreeNode binaryTreeNode) throws Exception {
        if (binaryTreeNode == null) return null;

        NaryTreeNode naryTreeNode = new NaryTreeNode();
        String value = binaryTreeNode.getValue();
        if (value.matches("^\\p{Alpha}+\\w*\\s*\\(.*\\)\\s*$")) {
            SyntaxUnit syntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(value);
            if (syntaxUnit.getSyntaxUnits().getFirst() instanceof Function function) {
                List<SyntaxUnit> functionParams = function.getSyntaxUnits();
                naryTreeNode.setValue(function.getSimplifiedFunctionSignature());
                for (SyntaxUnit param : functionParams) {
                    BinaryParallelExpressionTreeBuilder functionParamTreeBuilder = new BinaryParallelExpressionTreeBuilder(param);
                    BinaryTreeNode functionParamBinaryTreeRootNode = functionParamTreeBuilder.getRootNode();
                    naryTreeNode.getChildren().add(convertBinaryTreeToNaryTree(functionParamBinaryTreeRootNode));
                }
            } else {
                naryTreeNode.setValue(value);
                if (binaryTreeNode.getLeftChild() != null) {
                    naryTreeNode.getChildren().add(convertBinaryTreeToNaryTree(binaryTreeNode.getLeftChild()));
                }
                if (binaryTreeNode.getRightChild() != null) {
                    naryTreeNode.getChildren().add(convertBinaryTreeToNaryTree(binaryTreeNode.getRightChild()));
                }
            }
        } else {
            naryTreeNode.setValue(value);
            if (binaryTreeNode.getLeftChild() != null) {
                naryTreeNode.getChildren().add(convertBinaryTreeToNaryTree(binaryTreeNode.getLeftChild()));
            }
            if (binaryTreeNode.getRightChild() != null) {
                naryTreeNode.getChildren().add(convertBinaryTreeToNaryTree(binaryTreeNode.getRightChild()));
            }
        }

        return naryTreeNode;
    }
}
