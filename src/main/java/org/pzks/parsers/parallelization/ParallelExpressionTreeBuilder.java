package org.pzks.parsers.parallelization;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.optimizers.AdditionAndSubtractionOperationsParallelizationOptimizer;
import org.pzks.parsers.optimizers.ExpressionParallelizationOptimizer;
import org.pzks.parsers.optimizers.MultiplicationAndDivisionOperationsParallelizationOptimizer;
import org.pzks.utils.trees.TreeNode;
import org.pzks.units.*;
import org.pzks.units.Number;
import org.pzks.utils.Color;
import org.pzks.utils.DynamicList;
import org.pzks.utils.DynamicObject;

import java.util.ArrayList;
import java.util.List;

public class ParallelExpressionTreeBuilder {
    private TreeNode rootNode;
    private List<String> warnings = new ArrayList<>();

    public ParallelExpressionTreeBuilder(SyntaxUnit syntaxUnit) throws Exception {
        SyntaxUnit convertedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnit.getSyntaxUnits()));
        MultiplicationAndDivisionOperationsParallelizationOptimizer.replaceDivisionWithMultiplication(convertedSyntaxUnit.getSyntaxUnits());
        AdditionAndSubtractionOperationsParallelizationOptimizer.replaceSubtractionWithAddition(convertedSyntaxUnit.getSyntaxUnits());
        convertedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(convertedSyntaxUnit.getSyntaxUnits()));

//        List<String> warnings = getWarningsIfBuildingTheParallelTreeIsForbidden(convertedSyntaxUnit.getSyntaxUnits());
        warnings = getWarningsIfBuildingTheParallelTreeIsForbidden(convertedSyntaxUnit.getSyntaxUnits());
        if (warnings.isEmpty()) {
            List<SyntaxUnit> syntaxUnits = convertedSyntaxUnit.getSyntaxUnits();
            takeOutNumberFromLogicalBlocksWithOneElement(syntaxUnits);
            DynamicList treeNodeList = convertSyntaxUnitsToListOfTreeNodes(syntaxUnits);
            buildTree(treeNodeList);
            if (treeNodeList.size() == 1) {
                TreeNode rootNode = (TreeNode) treeNodeList.getFirst();

                TreeNode rootNodeBeforeSimplification;
                do {
                    rootNodeBeforeSimplification = rootNode.clone();
                    simplifyTree(rootNode);
                } while (!rootNodeBeforeSimplification.equals(rootNode));
                this.rootNode = rootNode.clone();
            }
        }
    }

    private void simplifyTree(TreeNode treeNode) {
        if (treeNode != null) {
            String value = treeNode.getValue();
            TreeNode leftChild = treeNode.getLeftChild();
            TreeNode rightChild = treeNode.getRightChild();

            switch (value) {
                case "+" -> {
                    if (leftChild != null && rightChild != null &&
                            leftChild.getValue().equals("*") && rightChild.getValue().equals("*")) {
                        TreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        TreeNode rightChildOfLeftChild = leftChild.getRightChild();

                        TreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        TreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfLeftChild != null && rightChildOfLeftChild != null &&
                                leftChildOfRightChild != null && rightChildOfRightChild != null &&
                                leftChildOfLeftChild.getValue().equals("-1") && leftChildOfRightChild.getValue().equals("-1")) {
                            treeNode.setValue("*");
                            treeNode.setLeftChild(new TreeNode("-1"));

                            TreeNode newRightChild = new TreeNode("+");
                            treeNode.setRightChild(newRightChild);

                            newRightChild.setLeftChild(rightChildOfLeftChild);
                            newRightChild.setRightChild(rightChildOfRightChild);
                        } else if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChildOfRightChild.getValue().matches("\\*|/|(-1)")) {
                            restructureTreeForOptimizedMinusOperationProcessingForRightChildOfTheCurrentTreeNode(treeNode, rightChild, leftChildOfRightChild, rightChildOfRightChild);
                        } else if (leftChildOfLeftChild != null &&
                                rightChildOfLeftChild != null &&
                                leftChildOfLeftChild.getValue().matches("\\*|/|(-1)")) {
                            restructureTreeForOptimizedMinusOperationProcessingForLeftChildOfTheCurrentTreeNode(treeNode, leftChild, rightChild, leftChildOfLeftChild, rightChildOfLeftChild);
                        }

                    } else if (rightChild != null && rightChild.getValue().equals("*")) {
                        TreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        TreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChild != null) {
                            restructureTreeForOptimizedMinusOperationProcessingForRightChildOfTheCurrentTreeNode(treeNode, rightChild, leftChildOfRightChild, rightChildOfRightChild);
                        }
                    } else if (leftChild != null && leftChild.getValue().equals("*")) {
                        TreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        TreeNode rightChildOfLeftChild = leftChild.getRightChild();

                        if (leftChildOfLeftChild != null &&
                                rightChildOfLeftChild != null &&
                                rightChild != null) {
                            restructureTreeForOptimizedMinusOperationProcessingForLeftChildOfTheCurrentTreeNode(treeNode, leftChild, rightChild, leftChildOfLeftChild, rightChildOfLeftChild);
                        }
                    }
                }
                case "*" -> {
                    if (leftChild != null && rightChild != null &&
                            leftChild.getValue().equals("/") && rightChild.getValue().equals("/")) {
                        TreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        TreeNode rightChildOfLeftChild = leftChild.getRightChild();

                        TreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        TreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfLeftChild != null && rightChildOfLeftChild != null &&
                                leftChildOfRightChild != null && rightChildOfRightChild != null &&
                                leftChildOfLeftChild.getValue().equals("1") && leftChildOfRightChild.getValue().equals("1")) {
                            treeNode.setValue("/");
                            treeNode.setLeftChild(new TreeNode("1"));

                            TreeNode newRightChild = new TreeNode("*");
                            treeNode.setRightChild(newRightChild);

                            newRightChild.setLeftChild(rightChildOfLeftChild);
                            newRightChild.setRightChild(rightChildOfRightChild);
                        } else if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChildOfRightChild.getValue().equals("1")) {
                            treeNode.setValue("/");
                            treeNode.setRightChild(rightChildOfRightChild);
                        } else if (leftChildOfLeftChild != null &&
                                rightChildOfLeftChild != null &&
                                leftChildOfLeftChild.getValue().equals("1")) {
                            treeNode.setValue("/");
                            treeNode.setLeftChild(rightChild);
                            treeNode.setRightChild(rightChildOfLeftChild);
                        }

                    } else if (rightChild != null && rightChild.getValue().equals("/")) {
                        TreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        TreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChild != null &&
                                leftChildOfRightChild.getValue().equals("1")) {
                            treeNode.setValue("/");
                            treeNode.setRightChild(rightChildOfRightChild);
                        }
                    } else if (leftChild != null && leftChild.getValue().equals("/")) {
                        TreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        TreeNode rightChildOfLeftChild = leftChild.getRightChild();

                        if (leftChildOfLeftChild != null &&
                                rightChildOfLeftChild != null &&
                                rightChild != null &&
                                leftChildOfLeftChild.getValue().equals("1")) {
                            treeNode.setValue("/");
                            treeNode.setLeftChild(rightChild);
                            treeNode.setRightChild(rightChildOfLeftChild);
                        }
                    }
                }
            }

            if (treeNode.getLeftChild() != null) {
                simplifyTree(treeNode.getLeftChild());
            }

            if (treeNode.getRightChild() != null) {
                simplifyTree(treeNode.getRightChild());
            }
        }
    }

    private void restructureTreeForOptimizedMinusOperationProcessingForLeftChildOfTheCurrentTreeNode(TreeNode treeNode, TreeNode leftChild, TreeNode rightChild, TreeNode leftChildOfLeftChild, TreeNode rightChildOfLeftChild) {
        if (leftChildOfLeftChild.getValue().equals("-1")) {
            treeNode.setValue("-");
            treeNode.setLeftChild(rightChild);
            treeNode.setRightChild(rightChildOfLeftChild);
        } else if (leftChildOfLeftChild.getValue().matches("[*/]")) {
            TreeNode parentOfTheMostLeftChild = getParentOfTheMostLeftChildIfPathContainsOnlyMultiplicationOrDivisionOperations(leftChild);
            if (parentOfTheMostLeftChild != null && parentOfTheMostLeftChild.getValue().equals("*")) {
                TreeNode theMostLeftChild = parentOfTheMostLeftChild.getLeftChild();
                if (theMostLeftChild.getValue().equals("-1")) {
                    treeNode.setValue("-");
                    treeNode.setLeftChild(rightChild);
                    treeNode.setRightChild(leftChild);
                    parentOfTheMostLeftChild.setValue(parentOfTheMostLeftChild.getRightChild().getValue());
                    parentOfTheMostLeftChild.setLeftChild(null);
                    parentOfTheMostLeftChild.setRightChild(null);
                }
            }
        }
    }

    private void restructureTreeForOptimizedMinusOperationProcessingForRightChildOfTheCurrentTreeNode(TreeNode treeNode, TreeNode rightChild, TreeNode leftChildOfRightChild, TreeNode rightChildOfRightChild) {
        if (leftChildOfRightChild.getValue().equals("-1")) {
            treeNode.setValue("-");
            treeNode.setRightChild(rightChildOfRightChild);
        } else if (leftChildOfRightChild.getValue().matches("[*/]")) {
            TreeNode parentOfTheMostLeftChild = getParentOfTheMostLeftChildIfPathContainsOnlyMultiplicationOrDivisionOperations(rightChild);
            if (parentOfTheMostLeftChild != null && parentOfTheMostLeftChild.getValue().equals("*")) {
                TreeNode theMostLeftChild = parentOfTheMostLeftChild.getLeftChild();
                if (theMostLeftChild.getValue().equals("-1")) {
                    treeNode.setValue("-");
                    parentOfTheMostLeftChild.setValue(parentOfTheMostLeftChild.getRightChild().getValue());
                    parentOfTheMostLeftChild.setLeftChild(null);
                    parentOfTheMostLeftChild.setRightChild(null);
                }
            }

        }
    }

    private TreeNode getParentOfTheMostLeftChildIfPathContainsOnlyMultiplicationOrDivisionOperations(TreeNode treeNode) {
        if (treeNode != null) {
            TreeNode leftChild = treeNode.getLeftChild();
            if (leftChild != null && !leftChild.getValue().matches("[+\\-]")) {
                TreeNode leftChildOfTheLeftChild = leftChild.getLeftChild();
                if (leftChildOfTheLeftChild != null && !leftChildOfTheLeftChild.getValue().matches("[+\\-]")) {
                    TreeNode leftChildOfTheLeftChildOfTheLeftChild = leftChildOfTheLeftChild.getLeftChild();
                    return leftChildOfTheLeftChildOfTheLeftChild == null ? leftChild : getParentOfTheMostLeftChildIfPathContainsOnlyMultiplicationOrDivisionOperations(leftChild);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private List<String> getWarningsIfBuildingTheParallelTreeIsForbidden(List<SyntaxUnit> syntaxUnits) {
        List<String> warnings = new ArrayList<>();
        if (!isFunctionsAbsent(syntaxUnits)) {
            warnings.add("Functions are forbidden for the expression that needs to be parallelized.");
            warnings.add("Please provide each function param separately if you want to build tree for the function.");
        }
        return warnings;
    }

    private boolean isFunctionsAbsent(List<SyntaxUnit> syntaxUnits) {
        boolean isFunctionsAbsent = true;
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            if (syntaxUnit instanceof Function) {
                isFunctionsAbsent = false;
                break;
            } else if (syntaxUnit instanceof LogicalBlock) {
                boolean innerResult = isFunctionsAbsent(syntaxUnit.getSyntaxUnits());
                if (!innerResult) {
                    isFunctionsAbsent = false;
                    break;
                }
            }
        }
        return isFunctionsAbsent;
    }

    private DynamicList convertSyntaxUnitsToListOfTreeNodes(List<SyntaxUnit> syntaxUnits) {
        DynamicList treeNodes = new DynamicList();
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            if (syntaxUnit instanceof LogicalBlock logicalBlock) {
                treeNodes.add(convertSyntaxUnitsToListOfTreeNodes(logicalBlock.getSyntaxUnits()));
            } else {
                treeNodes.add(new TreeNode(syntaxUnit.getValue()));
            }
        }
        return treeNodes;
    }


    private void takeOutNumberFromLogicalBlocksWithOneElement(List<SyntaxUnit> syntaxUnits) {
        for (int i = 0; i < syntaxUnits.size(); i++) {
            SyntaxUnit syntaxUnit = syntaxUnits.get(i);
            if (syntaxUnit instanceof LogicalBlock) {
                if (syntaxUnit.getSyntaxUnits().size() == 1) {
                    SyntaxUnit syntaxUnitInLogicalBlock = syntaxUnit.getSyntaxUnits().getFirst();
                    if (syntaxUnitInLogicalBlock instanceof Number number) {
                        syntaxUnits.set(i, number);
                    } else {
                        takeOutNumberFromLogicalBlocksWithOneElement(syntaxUnitInLogicalBlock.getSyntaxUnits());
                    }
                } else {
                    takeOutNumberFromLogicalBlocksWithOneElement(syntaxUnit.getSyntaxUnits());
                }
            }
        }
    }

    private void buildTree(DynamicList treeNodes) {
        int currentLevel = 1;

        while (treeNodes.size() > 1) {
            processTreeNodesForLevel(treeNodes, currentLevel);
            replaceSingleElementDynamicListWithTreeNodeInside(treeNodes);
            currentLevel++;
        }
    }

    private void replaceSingleElementDynamicListWithTreeNodeInside(DynamicList treeNodes) {
        for (int i = 0; i < treeNodes.size(); i++) {
            DynamicObject dynamicObject = treeNodes.get(i);
            if (dynamicObject instanceof DynamicList dynamicList) {
                if (dynamicList.size() == 1) {
                    DynamicObject dynamicObjectInList = dynamicList.getFirst();
                    treeNodes.set(i, dynamicObjectInList);
                } else {
                    replaceSingleElementDynamicListWithTreeNodeInside(dynamicList);
                }
            }
        }
    }

    private void processTreeNodesForLevel(DynamicList treeNodes, int currentLevel) {
        for (int i = 0; i < treeNodes.size(); i++) {
            DynamicObject structure = treeNodes.get(i);

            if (structure instanceof TreeNode currentTreeNode &&
                    currentTreeNode.getValue().matches("^[*/+\\-]$") &&
                    currentTreeNode.getLeftChild() == null &&
                    currentTreeNode.getRightChild() == null) {
                DynamicObject leftObject = treeNodes.get(i - 1);
                DynamicObject rightObject = treeNodes.get(i + 1);

                if (leftObject instanceof TreeNode leftTreeNode &&
                        rightObject instanceof TreeNode rightTreeNode &&
                        leftTreeNode.getLevel() < currentLevel &&
                        rightTreeNode.getLevel() < currentLevel) {
                    if (leftTreeNode.getValue().equals("-1") && currentTreeNode.getValue().equals("*") && i + 3 < treeNodes.size()) {
                        DynamicObject nextDynamicObjectAfterRightObject = treeNodes.get(i + 2);
                        DynamicObject nextDynamicObjectAfterNextDynamicObjectAfterRightObject = treeNodes.get(i + 3);
                        if (nextDynamicObjectAfterRightObject instanceof TreeNode nextTreeNodeAfterRightObject &&
                                nextTreeNodeAfterRightObject.getValue().equals("*")) {
                            if (nextDynamicObjectAfterNextDynamicObjectAfterRightObject instanceof TreeNode nextTreeNodeAfterNextTreeNodeAfterRightObject &&
                                    nextTreeNodeAfterNextTreeNodeAfterRightObject.getLevel() < currentLevel) {
                                i = buildTreeNodeWithOperationAsParent(treeNodes, i + 2, currentLevel);
                            }
                        } else {
                            i = buildTreeNodeWithOperationAsParent(treeNodes, i, currentLevel);
                        }
                    } else {
                        i = buildTreeNodeWithOperationAsParent(treeNodes, i, currentLevel);
                    }
//                    i = buildTreeNodeWithOperationAsParent(treeNodes, i, currentLevel);
                }
            } else if (structure instanceof DynamicList internalTreeNodeList && internalTreeNodeList.size() > 1) {
                processTreeNodesForLevel(internalTreeNodeList, currentLevel);
                replaceSingleElementDynamicListWithTreeNodeInside(treeNodes);

                DynamicObject processedTreeNode = treeNodes.get(i);
                if (processedTreeNode instanceof TreeNode treeNode) {
                    String value = treeNode.getValue();
                    TreeNode leftChild = treeNode.getLeftChild();
                    TreeNode rightChild = treeNode.getRightChild();

                    if (value.equals("/") &&
                            leftChild != null &&
                            rightChild != null &&
                            leftChild.getValue().equals("1")) {
                        treeNode.setLevel(0);
                        if (i - 2 >= 0) {
                            i -= 2;
                        }
                    }
                }
            }
        }
    }

    private int buildTreeNodeWithOperationAsParent(DynamicList treeNodes, int currentPosition, int currentLevel) {
        TreeNode currentTreeNode = (TreeNode) treeNodes.get(currentPosition);
        TreeNode leftTreeNode = (TreeNode) treeNodes.get(currentPosition - 1);
        TreeNode rightTreeNode = (TreeNode) treeNodes.get(currentPosition + 1);

        if (currentTreeNode.getValue().matches("[*/]")) {
            TreeNode newTreeNode = new TreeNode(
                    currentTreeNode.getValue(),
                    leftTreeNode,
                    rightTreeNode,
                    currentLevel
            );
            treeNodes.subList(currentPosition - 1, currentPosition + 2).clear();
            treeNodes.add(currentPosition - 1, newTreeNode);
            currentPosition--;

            if (currentTreeNode.getValue().equals("*") && leftTreeNode.getValue().equals("-1")) {
                newTreeNode.setLevel(0);
                if (currentPosition - 2 >= 0) {
                    currentPosition -= 2;
                }
            }

        } else if (currentTreeNode.getValue().matches("[\\-+]")) {
            if (currentPosition - 2 >= 0 && currentPosition + 2 < treeNodes.size()) {
                TreeNode previousOperationAsTreeNode = (TreeNode) treeNodes.get(currentPosition - 2);
                TreeNode nextOperationAsTreeNode = (TreeNode) treeNodes.get(currentPosition + 2);

                if (!previousOperationAsTreeNode.getValue().matches("[*/]") && !nextOperationAsTreeNode.getValue().matches("[*/]")) {
                    TreeNode newTreeNode = new TreeNode(
                            currentTreeNode.getValue(),
                            leftTreeNode,
                            rightTreeNode,
                            currentLevel
                    );
                    treeNodes.subList(currentPosition - 1, currentPosition + 2).clear();
                    treeNodes.add(currentPosition - 1, newTreeNode);
                    currentPosition--;
                }

            } else if (currentPosition - 2 >= 0) {
                TreeNode previousOperationAsTreeNode = (TreeNode) treeNodes.get(currentPosition - 2);
                if (!previousOperationAsTreeNode.getValue().matches("[*/]")) {
                    TreeNode newTreeNode = new TreeNode(
                            currentTreeNode.getValue(),
                            leftTreeNode,
                            rightTreeNode,
                            currentLevel
                    );
                    treeNodes.subList(currentPosition - 1, currentPosition + 2).clear();
                    treeNodes.add(currentPosition - 1, newTreeNode);
                    currentPosition--;
                }
            } else if (currentPosition + 2 < treeNodes.size()) {
                TreeNode nextOperationAsTreeNode = (TreeNode) treeNodes.get(currentPosition + 2);
                if (!nextOperationAsTreeNode.getValue().matches("[*/]")) {
                    TreeNode newTreeNode = new TreeNode(
                            currentTreeNode.getValue(),
                            leftTreeNode,
                            rightTreeNode,
                            currentLevel
                    );
                    treeNodes.subList(0, currentPosition + 2).clear();
                    treeNodes.addFirst(newTreeNode);
                }
            } else {
                TreeNode newTreeNode = new TreeNode(
                        currentTreeNode.getValue(),
                        leftTreeNode,
                        rightTreeNode,
                        currentLevel
                );
                treeNodes.subList(0, currentPosition + 2).clear();
                treeNodes.addFirst(newTreeNode);
            }
        }
        return currentPosition;
    }


    public TreeNode getRootNode() {
        return rootNode;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
