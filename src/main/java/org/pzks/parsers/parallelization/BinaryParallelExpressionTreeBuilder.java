package org.pzks.parsers.parallelization;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.SyntaxUnitsProcessor;
import org.pzks.parsers.optimizers.AdditionAndSubtractionOperationsParallelizationOptimizer;
import org.pzks.parsers.optimizers.MultiplicationAndDivisionOperationsParallelizationOptimizer;
import org.pzks.utils.GlobalSettings;
import org.pzks.utils.SyntaxUnitsValidationUtil;
import org.pzks.utils.trees.BinaryTreeNode;
import org.pzks.units.*;
import org.pzks.utils.DynamicList;
import org.pzks.utils.DynamicObject;

import java.util.ArrayList;
import java.util.List;

public class BinaryParallelExpressionTreeBuilder {
    private BinaryTreeNode rootNode;
    private List<String> warnings;

    public BinaryParallelExpressionTreeBuilder(SyntaxUnit syntaxUnit) throws Exception {
        SyntaxUnit convertedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnit.getSyntaxUnits()));
        MultiplicationAndDivisionOperationsParallelizationOptimizer.replaceDivisionWithMultiplication(convertedSyntaxUnit.getSyntaxUnits());
        AdditionAndSubtractionOperationsParallelizationOptimizer.replaceSubtractionWithAddition(convertedSyntaxUnit.getSyntaxUnits());
        convertedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(convertedSyntaxUnit.getSyntaxUnits()));

        warnings = getWarningsIfBuildingTheParallelTreeIsForbidden(convertedSyntaxUnit.getSyntaxUnits());

        if (!GlobalSettings.CONFIGURATION.shouldBuildBinaryParallelCalculationTree()) {
            warnings = new ArrayList<>();
        }

        if (warnings.isEmpty()) {
            List<SyntaxUnit> syntaxUnits = convertedSyntaxUnit.getSyntaxUnits();
            SyntaxUnitsProcessor.takeOutNumberFromLogicalBlocksWithOneElement(syntaxUnits);
            DynamicList treeNodeList = convertSyntaxUnitsToListOfTreeNodes(syntaxUnits);
            buildTree(treeNodeList);
            if (treeNodeList.size() == 1) {
                BinaryTreeNode rootNode = (BinaryTreeNode) treeNodeList.getFirst();

                BinaryTreeNode rootNodeBeforeSimplification;
                do {
                    rootNodeBeforeSimplification = rootNode.clone();
                    simplifyTree(rootNode);
                } while (!rootNodeBeforeSimplification.equals(rootNode));
                this.rootNode = rootNode.clone();
            }
        }
    }

    private void simplifyTree(BinaryTreeNode treeNode) {
        if (treeNode != null) {
            String value = treeNode.getValue();
            BinaryTreeNode leftChild = treeNode.getLeftChild();
            BinaryTreeNode rightChild = treeNode.getRightChild();

            switch (value) {
                case "+" -> {
                    if (leftChild != null && rightChild != null &&
                            leftChild.getValue().equals("*") && rightChild.getValue().equals("*")) {
                        BinaryTreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        BinaryTreeNode rightChildOfLeftChild = leftChild.getRightChild();

                        BinaryTreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        BinaryTreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfLeftChild != null && rightChildOfLeftChild != null &&
                                leftChildOfRightChild != null && rightChildOfRightChild != null &&
                                leftChildOfLeftChild.getValue().equals("-1") && leftChildOfRightChild.getValue().equals("-1")) {
                            treeNode.setValue("*");
                            treeNode.setLeftChild(new BinaryTreeNode("-1"));

                            BinaryTreeNode newRightChild = new BinaryTreeNode("+");
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
                        BinaryTreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        BinaryTreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChild != null) {
                            restructureTreeForOptimizedMinusOperationProcessingForRightChildOfTheCurrentTreeNode(treeNode, rightChild, leftChildOfRightChild, rightChildOfRightChild);
                        }
                    } else if (leftChild != null && leftChild.getValue().equals("*")) {
                        BinaryTreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        BinaryTreeNode rightChildOfLeftChild = leftChild.getRightChild();

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
                        BinaryTreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        BinaryTreeNode rightChildOfLeftChild = leftChild.getRightChild();

                        BinaryTreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        BinaryTreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfLeftChild != null && rightChildOfLeftChild != null &&
                                leftChildOfRightChild != null && rightChildOfRightChild != null &&
                                leftChildOfLeftChild.getValue().equals("1") && leftChildOfRightChild.getValue().equals("1")) {
                            treeNode.setValue("/");
                            treeNode.setLeftChild(new BinaryTreeNode("1"));

                            BinaryTreeNode newRightChild = new BinaryTreeNode("*");
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
                        BinaryTreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        BinaryTreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChild != null &&
                                leftChildOfRightChild.getValue().equals("1")) {
                            treeNode.setValue("/");
                            treeNode.setRightChild(rightChildOfRightChild);
                        }
                    } else if (leftChild != null && leftChild.getValue().equals("/")) {
                        BinaryTreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        BinaryTreeNode rightChildOfLeftChild = leftChild.getRightChild();

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

    private void restructureTreeForOptimizedMinusOperationProcessingForLeftChildOfTheCurrentTreeNode(BinaryTreeNode treeNode, BinaryTreeNode leftChild, BinaryTreeNode rightChild, BinaryTreeNode leftChildOfLeftChild, BinaryTreeNode rightChildOfLeftChild) {
        if (leftChildOfLeftChild.getValue().equals("-1")) {
            treeNode.setValue("-");
            treeNode.setLeftChild(rightChild);
            treeNode.setRightChild(rightChildOfLeftChild);
        } else if (leftChildOfLeftChild.getValue().matches("[*/]")) {
            BinaryTreeNode parentOfTheMostLeftChild = getParentOfTheMostLeftChildIfPathContainsOnlyMultiplicationOrDivisionOperations(leftChild);
            if (parentOfTheMostLeftChild != null && parentOfTheMostLeftChild.getValue().equals("*")) {
                BinaryTreeNode theMostLeftChild = parentOfTheMostLeftChild.getLeftChild();
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

    private void restructureTreeForOptimizedMinusOperationProcessingForRightChildOfTheCurrentTreeNode(BinaryTreeNode treeNode, BinaryTreeNode rightChild, BinaryTreeNode leftChildOfRightChild, BinaryTreeNode rightChildOfRightChild) {
        if (leftChildOfRightChild.getValue().equals("-1")) {
            treeNode.setValue("-");
            treeNode.setRightChild(rightChildOfRightChild);
        } else if (leftChildOfRightChild.getValue().matches("[*/]")) {
            BinaryTreeNode parentOfTheMostLeftChild = getParentOfTheMostLeftChildIfPathContainsOnlyMultiplicationOrDivisionOperations(rightChild);
            if (parentOfTheMostLeftChild != null && parentOfTheMostLeftChild.getValue().equals("*")) {
                BinaryTreeNode theMostLeftChild = parentOfTheMostLeftChild.getLeftChild();
                if (theMostLeftChild.getValue().equals("-1")) {
                    treeNode.setValue("-");
                    parentOfTheMostLeftChild.setValue(parentOfTheMostLeftChild.getRightChild().getValue());
                    parentOfTheMostLeftChild.setLeftChild(null);
                    parentOfTheMostLeftChild.setRightChild(null);
                }
            }

        }
    }

    private BinaryTreeNode getParentOfTheMostLeftChildIfPathContainsOnlyMultiplicationOrDivisionOperations(BinaryTreeNode treeNode) {
        if (treeNode != null) {
            BinaryTreeNode leftChild = treeNode.getLeftChild();
            if (leftChild != null && !leftChild.getValue().matches("[+\\-]")) {
                BinaryTreeNode leftChildOfTheLeftChild = leftChild.getLeftChild();
                if (leftChildOfTheLeftChild != null && !leftChildOfTheLeftChild.getValue().matches("[+\\-]")) {
                    BinaryTreeNode leftChildOfTheLeftChildOfTheLeftChild = leftChildOfTheLeftChild.getLeftChild();
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
        if (!SyntaxUnitsValidationUtil.isFunctionsAbsent(syntaxUnits)) {
            warnings.add("Functions are not supported for the expression that needs to be parallelized.");
            warnings.add("Please provide each function param separately if you want to build tree for the function.");
        }
        return warnings;
    }

    private DynamicList convertSyntaxUnitsToListOfTreeNodes(List<SyntaxUnit> syntaxUnits) {
        DynamicList treeNodes = new DynamicList();
        for (SyntaxUnit syntaxUnit : syntaxUnits) {
            if (syntaxUnit instanceof LogicalBlock logicalBlock) {
                treeNodes.add(convertSyntaxUnitsToListOfTreeNodes(logicalBlock.getSyntaxUnits()));
            } else {
                treeNodes.add(new BinaryTreeNode(syntaxUnit.getValue()));
            }
        }
        return treeNodes;
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

            if (structure instanceof BinaryTreeNode currentTreeNode &&
                    currentTreeNode.getValue().matches("^[*/+\\-]$") &&
                    currentTreeNode.getLeftChild() == null &&
                    currentTreeNode.getRightChild() == null) {
                DynamicObject leftObject = treeNodes.get(i - 1);
                DynamicObject rightObject = treeNodes.get(i + 1);

                if (leftObject instanceof BinaryTreeNode leftTreeNode &&
                        rightObject instanceof BinaryTreeNode rightTreeNode &&
                        leftTreeNode.getLevel() < currentLevel &&
                        rightTreeNode.getLevel() < currentLevel) {
                    if (leftTreeNode.getValue().equals("-1") && currentTreeNode.getValue().equals("*") && i + 3 < treeNodes.size()) {
                        DynamicObject nextDynamicObjectAfterRightObject = treeNodes.get(i + 2);
                        DynamicObject nextDynamicObjectAfterNextDynamicObjectAfterRightObject = treeNodes.get(i + 3);
                        if (nextDynamicObjectAfterRightObject instanceof BinaryTreeNode nextTreeNodeAfterRightObject &&
                                nextTreeNodeAfterRightObject.getValue().equals("*")) {
                            if (nextDynamicObjectAfterNextDynamicObjectAfterRightObject instanceof BinaryTreeNode nextTreeNodeAfterNextTreeNodeAfterRightObject &&
                                    nextTreeNodeAfterNextTreeNodeAfterRightObject.getLevel() < currentLevel) {
                                i = buildTreeNodeWithOperationAsParent(treeNodes, i + 2, currentLevel);
                            }
                        } else {
                            i = buildTreeNodeWithOperationAsParent(treeNodes, i, currentLevel);
                        }
                    } else {
                        i = buildTreeNodeWithOperationAsParent(treeNodes, i, currentLevel);
                    }
                }
            } else if (structure instanceof DynamicList internalTreeNodeList && internalTreeNodeList.size() > 1) {
                processTreeNodesForLevel(internalTreeNodeList, currentLevel);
                replaceSingleElementDynamicListWithTreeNodeInside(treeNodes);

                DynamicObject processedTreeNode = treeNodes.get(i);
                if (processedTreeNode instanceof BinaryTreeNode treeNode) {
                    String value = treeNode.getValue();
                    BinaryTreeNode leftChild = treeNode.getLeftChild();
                    BinaryTreeNode rightChild = treeNode.getRightChild();

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
        BinaryTreeNode currentTreeNode = (BinaryTreeNode) treeNodes.get(currentPosition);
        BinaryTreeNode leftTreeNode = (BinaryTreeNode) treeNodes.get(currentPosition - 1);
        BinaryTreeNode rightTreeNode = (BinaryTreeNode) treeNodes.get(currentPosition + 1);

        if (currentTreeNode.getValue().matches("[*/]")) {
            BinaryTreeNode newTreeNode = new BinaryTreeNode(
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
                BinaryTreeNode previousOperationAsTreeNode = (BinaryTreeNode) treeNodes.get(currentPosition - 2);
                BinaryTreeNode nextOperationAsTreeNode = (BinaryTreeNode) treeNodes.get(currentPosition + 2);

                if (!previousOperationAsTreeNode.getValue().matches("[*/]") && !nextOperationAsTreeNode.getValue().matches("[*/]")) {
                    BinaryTreeNode newTreeNode = new BinaryTreeNode(
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
                BinaryTreeNode previousOperationAsTreeNode = (BinaryTreeNode) treeNodes.get(currentPosition - 2);
                if (!previousOperationAsTreeNode.getValue().matches("[*/]")) {
                    BinaryTreeNode newTreeNode = new BinaryTreeNode(
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
                BinaryTreeNode nextOperationAsTreeNode = (BinaryTreeNode) treeNodes.get(currentPosition + 2);
                if (!nextOperationAsTreeNode.getValue().matches("[*/]")) {
                    BinaryTreeNode newTreeNode = new BinaryTreeNode(
                            currentTreeNode.getValue(),
                            leftTreeNode,
                            rightTreeNode,
                            currentLevel
                    );
                    treeNodes.subList(0, currentPosition + 2).clear();
                    treeNodes.addFirst(newTreeNode);
                }
            } else {
                BinaryTreeNode newTreeNode = new BinaryTreeNode(
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


    public BinaryTreeNode getRootNode() {
        return rootNode;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
