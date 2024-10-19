package org.pzks.parsers.parallelization;

import org.pzks.parsers.ExpressionParser;
import org.pzks.parsers.optimizers.ExpressionParallelizationOptimizer;
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

    public ParallelExpressionTreeBuilder(SyntaxUnit syntaxUnit) throws Exception {
        ExpressionParallelizationOptimizer expressionParallelizationOptimizer = new ExpressionParallelizationOptimizer(syntaxUnit);
        SyntaxUnit optimizedSyntaxUnit = expressionParallelizationOptimizer.getOptimizedSyntaxUnit();

        List<SyntaxUnit> syntaxUnits = optimizedSyntaxUnit.getSyntaxUnits();
        SyntaxUnit convertedSyntaxUnit = ExpressionParser.convertExpressionToParsedSyntaxUnit(ExpressionParser.getExpressionAsString(syntaxUnits));

        List<String> warnings = getWarningsIfBuildingTheParallelTreeIsForbidden(syntaxUnits);
        if (warnings.isEmpty()) {
            syntaxUnits = convertedSyntaxUnit.getSyntaxUnits();
            takeOutNumberFromLogicalBlocksWithOneElement(syntaxUnits);

            DynamicList treeNodeList = convertSyntaxUnitsToListOfTreeNodes(syntaxUnits);
            buildTree(treeNodeList);
            if (treeNodeList.size() == 1) {
                rootNode = (TreeNode) treeNodeList.getFirst();
                simplifyTree(rootNode);
            }
        } else {
            System.out.println("\n" + Color.YELLOW.getAnsiValue() + "Warning: " + Color.DEFAULT.getAnsiValue() + "The provided expression is not supported for building the parallel tree!");
            int maxWarningLength = warnings.stream()
                    .mapToInt(String::length)
                    .max()
                    .orElse(20);
            maxWarningLength = Math.max(maxWarningLength, 29);

            System.out.println("-".repeat(10) + Color.YELLOW.getAnsiValue() + "Warning details" + Color.DEFAULT.getAnsiValue() + "-".repeat(maxWarningLength + 6 - 25));
            for (String warning : warnings) {
                int warningLength = warning.length();
                int numberOfSpacesToAddTOTheOutput = maxWarningLength - warningLength;
                System.out.println("| - " + warning + " ".repeat(numberOfSpacesToAddTOTheOutput) + " |");
            }
            System.out.println("-".repeat(maxWarningLength + 6));
        }
    }

    private void simplifyTree(TreeNode treeNode) { //todo to be implemented
        if (treeNode != null) {
            String value = treeNode.getValue();
            TreeNode leftChild = treeNode.getLeftChild();
            TreeNode rightChild = treeNode.getRightChild();

            switch (value) {
                case "+" -> {
                    if (rightChild != null && rightChild.getValue().equals("*")) {
                        TreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        TreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChild != null &&
                                leftChildOfRightChild.getValue().equals("-1")) {
                            treeNode.setValue("-");
                            treeNode.setRightChild(rightChildOfRightChild);
                        }
                    } else if (leftChild != null && leftChild.getValue().equals("*")) {
                        TreeNode leftChildOfLeftChild = leftChild.getLeftChild();
                        TreeNode rightChildOfLeftChild = leftChild.getRightChild();

                        if (leftChildOfLeftChild != null &&
                                rightChildOfLeftChild != null &&
                                rightChild != null &&
                                leftChildOfLeftChild.getValue().equals("-1")) {
                            treeNode.setValue("-");
                            treeNode.setLeftChild(rightChild);
                            treeNode.setRightChild(rightChildOfLeftChild);
                        }
                    } else if (rightChild != null && rightChild.getValue().equals("+")) {
                        TreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        TreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChildOfRightChild.getValue().equals("*") &&
                                rightChildOfRightChild.getValue().equals("*")) {
                            TreeNode leftChildOfLeftChildOfRightChild = leftChildOfRightChild.getLeftChild();
                            TreeNode rightChildOfLeftChildOfRightChild = leftChildOfRightChild.getRightChild();

                            TreeNode leftChildOfRightChildOfRightChild = rightChildOfRightChild.getLeftChild();
                            TreeNode rightChildOfRightChildOfRightChild = rightChildOfRightChild.getRightChild();

                            if (leftChildOfLeftChildOfRightChild != null &&
                                    rightChildOfLeftChildOfRightChild != null &&
                                    leftChildOfRightChildOfRightChild != null &&
                                    rightChildOfRightChildOfRightChild != null &&
                                    leftChildOfLeftChildOfRightChild.getValue().equals("-1") &&
                                    leftChildOfRightChildOfRightChild.getValue().equals("-1")) {
                                treeNode.setValue("-");
                                rightChild.setLeftChild(rightChildOfLeftChildOfRightChild);
                                rightChild.setRightChild(rightChildOfRightChildOfRightChild);
                            }
                        }
                    }
                }
                case "*" -> {
                    if (leftChild != null &&
                            rightChild != null &&
                            leftChild.getValue().equals("-1") &&
                            rightChild.getValue().matches("\\w+")) {
                        treeNode.setValue("-" + rightChild.getValue());
                        treeNode.setLeftChild(null);
                        treeNode.setRightChild(null);
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
                    } else if (rightChild != null && rightChild.getValue().equals("*")) {
                        TreeNode leftChildOfRightChild = rightChild.getLeftChild();
                        TreeNode rightChildOfRightChild = rightChild.getRightChild();

                        if (leftChildOfRightChild != null &&
                                rightChildOfRightChild != null &&
                                leftChildOfRightChild.getValue().equals("/") &&
                                rightChildOfRightChild.getValue().equals("/")) {
                            TreeNode leftChildOfLeftChildOfRightChild = leftChildOfRightChild.getLeftChild();
                            TreeNode rightChildOfLeftChildOfRightChild = leftChildOfRightChild.getRightChild();

                            TreeNode leftChildOfRightChildOfRightChild = rightChildOfRightChild.getLeftChild();
                            TreeNode rightChildOfRightChildOfRightChild = rightChildOfRightChild.getRightChild();

                            if (leftChildOfLeftChildOfRightChild != null &&
                                    rightChildOfLeftChildOfRightChild != null &&
                                    leftChildOfRightChildOfRightChild != null &&
                                    rightChildOfRightChildOfRightChild != null &&
                                    leftChildOfLeftChildOfRightChild.getValue().equals("1") &&
                                    leftChildOfRightChildOfRightChild.getValue().equals("1")) {
                                treeNode.setValue("/");
                                rightChild.setLeftChild(rightChildOfLeftChildOfRightChild);
                                rightChild.setRightChild(rightChildOfRightChildOfRightChild);
                            }
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
                    i = buildTreeNodeWithOperationAsParent(treeNodes, i, currentLevel);
                }
            } else if (structure instanceof DynamicList internalTreeNodeList && internalTreeNodeList.size() > 1) {
                processTreeNodesForLevel(internalTreeNodeList, currentLevel);
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
}
