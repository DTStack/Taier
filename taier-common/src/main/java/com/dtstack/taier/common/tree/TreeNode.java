package com.dtstack.taier.common.tree;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-16 00:17
 **/
public class TreeNode {
    private List<TreeNode> children = new LinkedList<>();

    private String nodeId;
    private String parentId;
    private Object bindData;

    public List getChildren() {
        return this.children;
    }

    public void addChild(TreeNode node) {
        children.add(node);
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Object getBindData() {
        return bindData;
    }

    public void setBindData(Object bindData) {
        this.bindData = bindData;
    }
}
