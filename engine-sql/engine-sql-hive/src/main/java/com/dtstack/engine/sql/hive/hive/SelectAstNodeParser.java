/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.dtstack.engine.sql.hive.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ColumnLineageParser;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.QueryTableTree;
import com.dtstack.engine.sql.SelectColumn;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.hive.ASTNodeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/5/23
 */
public class SelectAstNodeParser extends BaseAstNodeSqlParser {

    private static final String SPLIT_DOT = ".";

    protected Map<String, List<Column>> tableColumnMap;

    protected ColumnLineageParser columnLineageParser = new ColumnLineageParser();

    public void setTableColumnsMap(Map<String, List<Column>> tableColumnsMap) {
        this.tableColumnMap = tableColumnsMap;
    }

    /**
     * 解析查询语句，通过解析的节点类型和正则结合判断
     * insert语句也会解析成查询节点，所以需要根据sql进一步判断
     */
    @Override
    public void parseNode(ASTNode rootNode, ParseResult parseResult) throws SQLException {
        if (result == null) {
            result = parseResult;
            result.setSqlType(SqlType.QUERY);
        }

        if (mainTable == null) {
           super.setTableAndDb(parseResult,rootNode);
        }
        columnLineageParser.setTableColumnMap(tableColumnMap);

        QueryTableTree rootTree = new QueryTableTree();
        if (result.getRoot() == null) {
            parseQueryNode(rootNode, rootTree, null);
            parseResult.setRoot(rootTree);
        } else {
            result.getRoot().addChild(rootTree);
            parseQueryNode(rootNode, rootTree, null);
        }

        dealCetQuery(rootTree);
        fillDb(rootTree);
        if (null != result.getRoot()) {
            columnLineageParser.pretreatment(result.getRoot());
        }
    }

    /**
     * 给表名填充db
     *
     * @param root
     */
    private void fillDb(QueryTableTree root) {
        if (StringUtils.isNotEmpty(root.getName())) {
            if (!root.getName().contains(SPLIT_DOT)) {
                root.setName(String.format("%s.%s", result.getCurrentDb(), root.getName()));
            }
        }

        if (CollectionUtils.isNotEmpty(root.getChildren())) {
            for (QueryTableTree child : root.getChildren()) {
                fillDb(child);
            }
        }
    }

    /**
     * cet查询需要删除，并且映射到真正的查询
     *
     * @param rootTree
     */
    private void dealCetQuery(QueryTableTree rootTree) {
        if (CollectionUtils.isEmpty(rootTree.getChildren())) {
            return;
        }

        Map<String, QueryTableTree> cetQueryMap = new HashMap<>();
        Iterator<QueryTableTree> it = rootTree.getChildren().iterator();
        while (it.hasNext()) {
            QueryTableTree child = it.next();
            if (child.isCetQuery()) {
                cetQueryMap.put(child.getAlias(), child);
                it.remove();
            }
        }

        if (!cetQueryMap.isEmpty() && CollectionUtils.isNotEmpty(rootTree.getChildren())) {
            for (QueryTableTree child : rootTree.getChildren()) {
                QueryTableTree cetQuery = cetQueryMap.get(child.getName());
                if (cetQuery != null) {
                    cetQuery.setCetQuery(false);
                    child.addChild(cetQuery);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(rootTree.getChildren())) {
            for (QueryTableTree child : rootTree.getChildren()) {
                dealCetQuery(child);
            }
        }
    }

    private void parseQueryNode(ASTNode root, QueryTableTree treeRoot, String queryAlias) {
        if (HiveParser.TOK_QUERY != root.getToken().getType()) {
            return;
        }

        treeRoot.setAlias(queryAlias);

        ASTNode fromNode = ASTNodeUtil.getNode(root, HiveParser.TOK_FROM);
        if (fromNode != null) {
            parseFromNode(fromNode, treeRoot, queryAlias);
        }

        ASTNode cteNode = ASTNodeUtil.getNode(root, HiveParser.TOK_CTE);
        if (cteNode != null) {
            parseCTENode(cteNode, treeRoot);
        }

        ASTNode insertNode = ASTNodeUtil.getNode(root, HiveParser.TOK_INSERT);
        if (insertNode != null) {
            parseInsertNode(insertNode, treeRoot);
        }
    }

    private void parseCTENode(ASTNode cteNode, QueryTableTree rootTree) {
        if (HiveParser.TOK_CTE != cteNode.getToken().getType()) {
            return;
        }

        for (Node child : cteNode.getChildren()) {
            parseSubQueryNode((ASTNode) child, rootTree, true);
        }
    }

    private void parseFromNode(ASTNode fromNode, QueryTableTree rootTree, String queryAlias) {
        if (HiveParser.TOK_FROM != fromNode.getToken().getType()) {
            return;
        }

        ASTNode tabrefNode = ASTNodeUtil.getNode(fromNode, HiveParser.TOK_TABREF);
        if (tabrefNode != null) {
            parseTabrefNode(tabrefNode, rootTree, queryAlias);
        }

        ASTNode subQueryNode = ASTNodeUtil.getNode(fromNode, HiveParser.TOK_SUBQUERY);
        if (subQueryNode != null) {
            parseSubQueryNode(subQueryNode, rootTree, false);
        }

        ASTNode joinNode = ASTNodeUtil.getNode(fromNode, HiveParser.TOK_JOIN);
        if (joinNode != null) {
            parseJoinNode(joinNode, rootTree);
        }

        ASTNode fullOuterJoinNode = ASTNodeUtil.getNode(fromNode, HiveParser.TOK_FULLOUTERJOIN);
        if (fullOuterJoinNode != null) {
            parserFullOuterJoin(fullOuterJoinNode, rootTree);
        }

        ASTNode leftOuterJoinNode = ASTNodeUtil.getNode(fromNode, HiveParser.TOK_LEFTOUTERJOIN);
        if (leftOuterJoinNode != null) {
            parserFullOuterJoin(leftOuterJoinNode, rootTree);
        }

        ASTNode rightOuterJoinNode = ASTNodeUtil.getNode(fromNode, HiveParser.TOK_RIGHTOUTERJOIN);
        if (rightOuterJoinNode != null) {
            parserFullOuterJoin(rightOuterJoinNode, rootTree);
        }

        ASTNode leftNode = ASTNodeUtil.getNode(fromNode, HiveParser.TOK_LEFTOUTERJOIN);
        if (leftNode != null) {
            parseJoinNode(leftNode, rootTree);
        }
    }

    private void parseTabrefNode(ASTNode tabrefNode, QueryTableTree rootTree, String queryAlias) {
        ASTNode tableNameNode = ASTNodeUtil.getNode(tabrefNode, HiveParser.TOK_TABNAME);
        ArrayList<Node> children = tableNameNode.getChildren();
        if (children.size() == 1) {
            // 这里不对表名添加db，处理完cet查询后在添加db
            rootTree.setName(((ASTNode) children.get(0)).getText());
        } else if (children.size() == 2) {
            rootTree.setName(((ASTNode) children.get(0)).getText() + SPLIT_DOT + ((ASTNode) children.get(1)).getText());
        }

        if (tabrefNode.getChildren().size() == 2 && queryAlias == null) {
            ASTNode aliasNode = (ASTNode) tabrefNode.getChildren().get(1);
            rootTree.setAlias(aliasNode.getText());
        }
    }

    private void parserFullOuterJoin(ASTNode joinNode, QueryTableTree rootTree){

        ArrayList<Node> children = joinNode.getChildren();
        for (Node child : children) {
            if (((ASTNode) child).getToken().getType() == HiveParser.TOK_TABREF) {
                QueryTableTree childTree = new QueryTableTree();
                rootTree.addChild(childTree);
                parseTabrefNode((ASTNode) child, childTree, null);
            } else if (((ASTNode) child).getToken().getType() == HiveParser.TOK_SUBQUERY) {
                parseSubQueryNode((ASTNode) child, rootTree, false);
            } else if (((ASTNode) child).getToken().getType() == HiveParser.TOK_LEFTOUTERJOIN
                    || ((ASTNode) child).getToken().getType() == HiveParser.TOK_FULLOUTERJOIN
                    || ((ASTNode) child).getToken().getType() == HiveParser.TOK_RIGHTOUTERJOIN) {
                QueryTableTree childTree = new QueryTableTree();
                rootTree.addChild(childTree);
                parserFullOuterJoin((ASTNode) child, childTree);
            }
        }
    }

    private void parseJoinNode(ASTNode joinNode, QueryTableTree rootTree) {
        if (HiveParser.TOK_JOIN != joinNode.getToken().getType()) {
            return;
        }

        ArrayList<Node> children = joinNode.getChildren();
        for (Node child : children) {
            if (((ASTNode) child).getToken().getType() == HiveParser.TOK_TABREF) {
                QueryTableTree childTree = new QueryTableTree();
                rootTree.addChild(childTree);
                parseTabrefNode((ASTNode) child, childTree, null);
            } else if (((ASTNode) child).getToken().getType() == HiveParser.TOK_SUBQUERY) {
                parseSubQueryNode((ASTNode) child, rootTree, false);
            }
        }
    }

    private void parseSubQueryNode(ASTNode subQueryNode, QueryTableTree rootTree, boolean cetNode) {
        if (HiveParser.TOK_SUBQUERY != subQueryNode.getToken().getType()) {
            return;
        }

        String queryAlias = null;
        if (subQueryNode.getChildren().size() == 2) {
            ASTNode aliasNode = (ASTNode) subQueryNode.getChildren().get(1);
            queryAlias = aliasNode.getText();
        }

        ASTNode queryNode = ASTNodeUtil.getNode(subQueryNode, HiveParser.TOK_QUERY);
        if (queryNode != null) {
            QueryTableTree child = new QueryTableTree();
            child.setCetQuery(cetNode);
            rootTree.addChild(child);
            parseQueryNode(queryNode, child, queryAlias);
        } else {
            ASTNode unionNode = ASTNodeUtil.getNode(subQueryNode, HiveParser.TOK_UNIONALL);
            for (Node unionNodeChild : unionNode.getChildren()) {
                QueryTableTree unionChild = new QueryTableTree();
                unionChild.setCetQuery(cetNode);
                rootTree.addChild(unionChild);
                parseQueryNode((ASTNode) unionNodeChild, unionChild, queryAlias);
            }
        }
    }

    private void parseInsertNode(ASTNode insertNode, QueryTableTree root) {
        ASTNode selectNode = ASTNodeUtil.getNode(insertNode, HiveParser.TOK_SELECT);
        if (selectNode == null || CollectionUtils.isEmpty(selectNode.getChildren())) {
            return;
        }

        List<SelectColumn> selectColumns = new ArrayList<>();
        for (Node child : selectNode.getChildren()) {
            selectColumns.addAll(parseSingleColumnNode((ASTNode) child));
        }

        root.setColumns(selectColumns);
    }

    private List<SelectColumn> parseSingleColumnNode(ASTNode columnNode) {
        List<SelectColumn> selectColumns = new ArrayList<>();

        String alias = null;
        List<Node> children = columnNode.getChildren();
        if (children.size() == 2) {
            alias = ((ASTNode) children.get(1)).getText();
        }

        ASTNode firstNode = (ASTNode) children.get(0);
        if (firstNode.getToken().getType() == HiveParser.DOT) {
            if (firstNode.getChildren().size() == 2) {
                String name = ((ASTNode) firstNode.getChildren().get(1)).getText();
                String tb = ((ASTNode) firstNode.getChildren().get(0).getChildren().get(0)).getText();
                if (tb != null) {
                    name = tb + SPLIT_DOT + name;
                }

                selectColumns.add(new SelectColumn(name, alias));
                return selectColumns;
            }

            firstNode = (ASTNode) firstNode.getChildren().get(0);
        }

        switch (firstNode.getToken().getType()) {
            case HiveParser.TOK_ALLCOLREF:
                if (CollectionUtils.isEmpty(firstNode.getChildren())) {
                    selectColumns.add(new SelectColumn("*", null));
                } else {
                    ASTNode tabNameNode = (ASTNode) firstNode.getChildren().get(0);
                    String tableAlias = ((ASTNode) tabNameNode.getChildren().get(0)).getText();
                    selectColumns.add(new SelectColumn(tableAlias + ".*", null));
                }
                break;
            case HiveParser.TOK_TABLE_OR_COL:
                String name = getColumnName(firstNode);
                if (alias == null) {
                    selectColumns.add(new SelectColumn(name, name));
                } else {
                    selectColumns.add(new SelectColumn(name, alias));
                }
                break;
            case HiveParser.TOK_FUNCTION:
                for (Node child : firstNode.getChildren()) {
                    if (((ASTNode) child).getToken().getType() == HiveParser.TOK_TABLE_OR_COL) {
                        // 使用函数时可以不指定别名，hive默认的别名就是函数加字段，比如select count(id) from tb1中别名
                        // 就是 count(id)，这种情况可能会影响最后的血缘解析，所以不考虑这种情况，需要规范用户sql
                        name = getColumnName(child);
                        selectColumns.add(new SelectColumn(name, alias));
                    }
                }
                break;
            default:
                // 常量字段不提取出来，最后再做血缘解析的时候会处理
        }

        return selectColumns;
    }

    private String getColumnName(Node columnNameNode) {
        return ((ASTNode) columnNameNode.getChildren().get(0)).getText();
    }
}
