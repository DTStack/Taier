package com.dtstack.engine.sql.hive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 处理 ASTNode 的工具类
 *
 * @author jiangbo
 * @time 2018/5/18
 */
public class ASTNodeUtil {

    public static final String DOUBLE_QUOTES = "\"";

    public static final String SINGLE_QUOTES = "'";

    public static final String TABLE_NAME_KEY = "tableName";

    public static final String DB_NAME_KEY = "dbName";

    public static ASTNode getNode(ASTNode root, Integer token) {
        return getNode(root, token, false);
    }

    /**
     * 获取表名和数据库
     */
    public static Map<String, String> getTableNameAndDbName(ASTNode root) {
        ASTNode tableNode = ASTNodeUtil.getNode(root, HiveParser.TOK_TABNAME, true);
        Map<String, String> info = null;
        if (tableNode != null) {
            List<String> values = ASTNodeUtil.getNodeValue(tableNode);
            if (CollectionUtils.isNotEmpty(values)) {
                info = Maps.newHashMap();
                String table = null;
                String db = null;
                if (values.size() == 1) {
                    table = values.get(0);
                    db = null;
                } else if (values.size() == 2) {
                    table = values.get(1);
                    db = values.get(0);
                }

                if (table != null && table.startsWith("`")) {
                    table = table.replace("`", "").trim();
                }

                if (db != null && db.startsWith("`")) {
                    db = db.replace("`", "").trim();
                }

                info.put(TABLE_NAME_KEY, table);
                info.put(DB_NAME_KEY, db);
            }
        }

        return info;
    }

    /**
     * 获取指定token的节点
     */
    public static ASTNode getNode(ASTNode root, Integer token, Boolean isDepthFirst) {
        if (CollectionUtils.isNotEmpty(root.getChildren()) && ParseUtils.containsTokenOfType(root, token)) {
            for (Node node : root.getChildren()) {
                if (((ASTNode) node).getToken().getType() == token) {
                    return (ASTNode) node;
                } else if (isDepthFirst) {
                    return getNode((ASTNode) node, token, isDepthFirst);
                }
            }
        }

        return null;
    }

    /**
     * 获取节点下的值
     */
    public static List<String> getNodeValue(ASTNode root) {
        ArrayList<Node> children = root.getChildren();
        List<String> values = null;
        if (CollectionUtils.isNotEmpty(children)) {
            values = Lists.newArrayList();
            String value;
            for (Node child : children) {
                value = ((ASTNode) child).getToken().getText();
                boolean isValue = (value.startsWith(SINGLE_QUOTES) || value.startsWith(DOUBLE_QUOTES)) && StringUtils.isNotEmpty(value);
                if (isValue) {
                    value = value.substring(1, value.length() - 1);
                }
                values.add(value);
            }
        }

        return values;
    }

    /**
     * 递归获取所有的指定节点
     */
    public static List<ASTNode> getNodes(ASTNode root, Integer token) {
        List<ASTNode> queryNodes = Lists.newArrayList();
        if (root.getToken().getType() == token) {
            queryNodes.add(root);
        }

        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            for (Node node : root.getChildren()) {
                queryNodes.addAll(getNodes((ASTNode) node, token));
            }
        }

        return queryNodes;
    }

    /**
     * 删除节点
     */
    public static ASTNode deleteNode(ASTNode root, Integer token) {
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            ArrayList<Node> children = root.getChildren();
            Iterator<Node> iterator = children.iterator();
            while (iterator.hasNext()) {
                int type = ((ASTNode) iterator.next()).getToken().getType();
                if (type == token) {
                    iterator.remove();
                } else {
                    deleteNode((ASTNode) iterator.next(), token);
                }
            }
        }

        return root;
    }

    /**
     * 判断节点是否包含给定的token
     */
    public static boolean contains(ASTNode root, Integer token) {
        List<Node> children = root.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            for (Node child : children) {
                if (((ASTNode) child).getToken().getType() == token) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 返回非空根节点
     *
     * @param tree
     * @return
     */
    public static ASTNode findRootNonNullToken(ASTNode tree) {
        while (tree.getToken() == null && tree.getChildCount() > 0) {
            tree = (ASTNode) tree.getChild(0);
        }

        return tree;
    }

    /**
     * 获取节点下带引号的值
     *
     * @param root 根节点
     * @return
     */
    public static List<String> getQuotationValue(ASTNode root) {
        List<String> values = Lists.newArrayList();
        if (root.getText().startsWith(SINGLE_QUOTES) || root.getText().startsWith(DOUBLE_QUOTES)) {
            values.add(root.getText());
        } else if (CollectionUtils.isNotEmpty(root.getChildren())) {
            for (Node node : root.getChildren()) {
                values.addAll(getQuotationValue((ASTNode) node));
            }
        }
        return values;
    }

    /**
     * 解析sql中使用的函数，返回函数名称
     * @param root
     * @return
     */
    public static List<String> getFunctionNames(ASTNode root) {
        List<String> values = Lists.newArrayList();
        if (null != root.token && HiveParser.TOK_FUNCTION == root.token.getType()) {
            if (CollectionUtils.isNotEmpty(root.getChildren())) {
                for (Node node : root.getChildren()) {
                    ASTNode astNode = (ASTNode) node;
                    if (astNode.getToken().getType() == HiveParser.Identifier){
                        values.add(astNode.getText());
                    }else if (CollectionUtils.isNotEmpty(node.getChildren())) {
                        for (Node childNode : node.getChildren()) {
                            values.addAll(getFunctionNames((ASTNode) childNode));
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            for (Node node : root.getChildren()) {
                values.addAll(getFunctionNames((ASTNode) node));
            }
        }
        return values;
    }

}
