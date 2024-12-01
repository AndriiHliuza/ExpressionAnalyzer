package org.pzks.utils.trees;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.pzks.utils.DynamicObject;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class TreeNode implements DynamicObject, Cloneable {
    private String value; // operation for level 2 and up | any other

    @JsonIgnore
    private int level;

    public TreeNode() {
    }

    public TreeNode(String value) {
        this.value = value;
    }

    public TreeNode(String value, int level) {
        this.value = value;
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        return level == treeNode.level && Objects.equals(value, treeNode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, level);
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "value='" + value + '\'' +
                ", level=" + level +
                '}';
    }

    @Override
    public TreeNode clone() throws CloneNotSupportedException {
        return (TreeNode) super.clone();
    }
}
