package org.pzks.utils.trees;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BinaryTreeNode extends TreeNode {

    @JsonProperty("left")
    private BinaryTreeNode leftChild;

    @JsonProperty("right")
    private BinaryTreeNode rightChild;

    public BinaryTreeNode() {
    }

    public BinaryTreeNode(String value) {
        super(value);
    }

    public BinaryTreeNode(String value, BinaryTreeNode leftChild, BinaryTreeNode rightChild) {
        super(value);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public BinaryTreeNode(String value, BinaryTreeNode leftChild, BinaryTreeNode rightChild, int level) {
        super(value, level);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    @JsonIgnore
    public int getMaxHeight() {
        return getMaxHeight(this);
    }

    private int getMaxHeight(BinaryTreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(getMaxHeight(root.getLeftChild()), getMaxHeight(root.getRightChild()));
    }

    public BinaryTreeNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(BinaryTreeNode leftChild) {
        this.leftChild = leftChild;
    }

    public BinaryTreeNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(BinaryTreeNode rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BinaryTreeNode that = (BinaryTreeNode) o;
        return Objects.equals(leftChild, that.leftChild) && Objects.equals(rightChild, that.rightChild);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), leftChild, rightChild);
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "value='" + getValue() + '\'' +
                ", leftChild=" + leftChild +
                ", rightChild=" + rightChild +
                ", level=" + getLevel() +
                '}';
    }

    @Override
    public BinaryTreeNode clone() throws CloneNotSupportedException {
        BinaryTreeNode clone = (BinaryTreeNode) super.clone();
        clone.leftChild = (leftChild != null) ? leftChild.clone() : null;
        clone.rightChild = (rightChild != null) ? rightChild.clone() : null;
        return clone;
    }
}
