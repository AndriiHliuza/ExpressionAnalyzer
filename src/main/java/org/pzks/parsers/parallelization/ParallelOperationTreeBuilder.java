package org.pzks.parsers.parallelization;

import org.pzks.units.SyntaxUnit;
import org.pzks.utils.BasicExpressionUnitRecognizer;
import org.pzks.utils.trees.NaryTreeNode;

public class ParallelOperationTreeBuilder {
    private final NaryTreeNode rootNode;

    public ParallelOperationTreeBuilder(SyntaxUnit syntaxUnit) throws Exception {
        NaryParallelExpressionTreeBuilder naryTreeBuilder = new NaryParallelExpressionTreeBuilder(syntaxUnit);
        rootNode = naryTreeBuilder.getRootNode();
        convertNaryTreeToNaryOperationTree(rootNode);
    }

    public NaryTreeNode getRootNode() {
        return rootNode;
    }

    private void convertNaryTreeToNaryOperationTree(NaryTreeNode naryTreeNode) throws Exception {
        if (naryTreeNode != null) {
            BasicExpressionUnitRecognizer basicExpressionUnitRecognizer = new BasicExpressionUnitRecognizer();
            String value = naryTreeNode.getValue();
            if (value != null) {

                if (basicExpressionUnitRecognizer.isValidAlphaNumericNaming(value) || basicExpressionUnitRecognizer.isFloatNumber(value)) {
                    naryTreeNode.setValue(null);
                } else {
                    naryTreeNode.getChildren().removeIf(
                            node -> basicExpressionUnitRecognizer.isValidAlphaNumericNaming(node.getValue()) ||
                                    basicExpressionUnitRecognizer.isFloatNumber(node.getValue())
                    );

                    for (NaryTreeNode child : naryTreeNode.getChildren()) {
                        convertNaryTreeToNaryOperationTree(child);
                    }
                }
            }
        }
    }
}
