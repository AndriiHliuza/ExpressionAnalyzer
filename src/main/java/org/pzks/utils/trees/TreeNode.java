package org.pzks.utils.trees;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pzks.utils.DynamicObject;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeNode implements DynamicObject, Cloneable {
    private String value;  // operation for level 2 and up | any other

    @JsonProperty("left")
    private TreeNode leftChild;

    @JsonProperty("right")
    private TreeNode rightChild;

    @JsonIgnore
    private int level;

    public TreeNode() {
    }

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

    @JsonIgnore
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode treeNode = (TreeNode) o;
        return Objects.equals(value, treeNode.value) && Objects.equals(leftChild, treeNode.leftChild) && Objects.equals(rightChild, treeNode.rightChild);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, leftChild, rightChild);
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

    @Override
    public TreeNode clone() throws CloneNotSupportedException {
        TreeNode clone = (TreeNode) super.clone();
        clone.leftChild = (leftChild != null) ? leftChild.clone() : null;
        clone.rightChild = (rightChild != null) ? rightChild.clone() : null;
        return clone;
    }
}
