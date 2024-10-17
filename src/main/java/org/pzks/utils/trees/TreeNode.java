package org.pzks.utils.trees;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pzks.utils.DynamicObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeNode implements DynamicObject {
    private String value;  // operation for level 2 and up | any other
    private TreeNode leftChild;
    private TreeNode rightChild;
    private int level;

    public TreeNode() {}

    public TreeNode(String value) {
        this.value = value;
    }

    public TreeNode(String value, TreeNode leftChild, TreeNode rightChild) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public TreeNode(String value, TreeNode leftChild, TreeNode rightChild, int level) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.level = level;
    }

    public int getMaxHeight() {
        return getMaxHeight(this);
    }

    private int getMaxHeight(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(getMaxHeight(root.getLeftChild()), getMaxHeight(root.getRightChild()));
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TreeNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(TreeNode leftChild) {
        this.leftChild = leftChild;
    }

    public TreeNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(TreeNode rightChild) {
        this.rightChild = rightChild;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "value='" + value + '\'' +
                ", leftChild=" + leftChild +
                ", rightChild=" + rightChild +
                ", level=" + level +
                '}';
    }
}
