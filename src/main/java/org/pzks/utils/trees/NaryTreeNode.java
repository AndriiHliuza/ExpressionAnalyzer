package org.pzks.utils.trees;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NaryTreeNode extends TreeNode {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<NaryTreeNode> children = new ArrayList<>();

    public NaryTreeNode() {}

    public NaryTreeNode(String value) {
        super(value);
    }

    public NaryTreeNode(String value, int level) {
        super(value, level);
    }

    public NaryTreeNode(String value, List<NaryTreeNode> children) {
        super(value);
        this.children = children;
    }

    public NaryTreeNode(String value, List<NaryTreeNode> children, int level) {
        super(value, level);
        this.children = children;
    }

    public List<NaryTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<NaryTreeNode> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NaryTreeNode that = (NaryTreeNode) o;
        return Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), children);
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "value='" + getValue() + '\'' +
                ", children=" + children +
                ", level=" + getLevel() +
                '}';
    }

    @Override
    public NaryTreeNode clone() throws CloneNotSupportedException {
        NaryTreeNode clone = (NaryTreeNode) super.clone();
        clone.children = new ArrayList<>();
        for (NaryTreeNode child : this.children) {
            clone.children.add(child.clone());
        }
        return clone;
    }
}
