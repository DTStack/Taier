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

package com.dtstack.taier.develop.sql.node;


import com.dtstack.taier.develop.sql.Column;
import com.dtstack.taier.develop.sql.Pair;
import com.google.common.collect.Lists;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/27 17:55
 * @Description: join的结构:left和right
 * <p>
 * left和right可以是SqlJoin，也可以是SqlSelect（原本是SqlBasicCall kind.AS。as已经挖去了）,可以是SqlIdentifier
 */
public class JoinCall extends BaseCall {

    public static Logger LOG = LoggerFactory.getLogger(JoinCall.class);


    private Node left;

    private Node right;

    /**
     * 将多重join形成的子树的子结点处理为兄弟结点
     */
    private List<Node> comboList;

    public List<Node> getComboList() {
        return comboList;
    }

    public void setComboList(List<Node> comboList) {
        this.comboList = comboList;
    }

    public JoinCall(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlJoin sqlJoin = checkNode(node);
        comboList = Lists.newArrayList();
        SqlNode left = sqlJoin.getLeft();
        handleLeftOrRight(left, 0);

        SqlNode right = sqlJoin.getRight();
        handleLeftOrRight(right, 1);
        return null;
    }

    /**
     * 0：left  1：right
     *
     * @param node
     * @param i
     */
    private void handleLeftOrRight(SqlNode node, int i) {
        Pair<String, SqlNode> leftSqlNodePair = removeAs(node);
        String alias = null;
        SqlNode handledNode = node;
        if (leftSqlNodePair != null) {
            alias = leftSqlNodePair.getKey();
            handledNode = leftSqlNodePair.getValue();
        }

        Node resultNode = null;

        //sqlJoin
        if (handledNode instanceof SqlJoin) {
            JoinCall joinCall = new JoinCall(getDefaultDb(), getTableColumnMap());
            joinCall.setAlias(alias);
            joinCall.parseSql(handledNode);
            resultNode = joinCall;
            comboList.addAll(joinCall.getComboList());
        }
        //子查询
        else if (handledNode instanceof SqlSelect) {
            SelectNode selectNode = new SelectNode(getDefaultDb(), getTableColumnMap());
            selectNode.setAlias(alias);
            selectNode.parseSql(handledNode);
            resultNode = selectNode;
            comboList.add(selectNode);
        }
        //表
        else if (handledNode instanceof SqlIdentifier) {
            Identifier identifier = new Identifier(getDefaultDb(), getTableColumnMap());
            identifier.setAlias(alias);
            identifier.setContext(Context.IDENTIFIER_TABLE);
            identifier.parseSql(handledNode);
            resultNode = identifier;
            comboList.add(identifier);
        }
        //join union
        else if (handledNode instanceof SqlBasicCall && SqlKind.UNION == handledNode.getKind()) {
            List<SqlNode> operandList = ((SqlBasicCall) handledNode).getOperandList();
            SqlNode leftSelect = operandList.get(0);
            SqlNode rightSelect = operandList.get(1);
            if (leftSelect instanceof SqlSelect) {
                SelectNode leftSelectNode = new SelectNode(getDefaultDb(), getTableColumnMap());
                Pair<String, SqlNode> leftSelectNodePair = removeAs(leftSelect);
                String leftSelectAlias = null;
                SqlNode leftSelectHandledNode = leftSelect;
                if (leftSelectNodePair != null) {
                    leftSelectAlias = leftSelectNodePair.getKey();
                    leftSelectHandledNode = leftSelectNodePair.getValue();
                }
                leftSelectNode.setAlias(leftSelectAlias);
                leftSelectNode.parseSql(leftSelectHandledNode);
                comboList.add(leftSelectNode);
            } else {
                LOG.warn("union中竟然有:{}类型!", leftSelect.getKind());
            }
            if (rightSelect instanceof SqlSelect) {
                SelectNode rightSelectNode = new SelectNode(getDefaultDb(), getTableColumnMap());
                Pair<String, SqlNode> rightSelectNodePair = removeAs(rightSelect);
                String rightSelectAlias = null;
                SqlNode rightSelectHandledNode = rightSelect;
                if (rightSelectNodePair != null) {
                    rightSelectAlias = rightSelectNodePair.getKey();
                    rightSelectHandledNode = rightSelectNodePair.getValue();
                }
                rightSelectNode.setAlias(rightSelectAlias);
                rightSelectNode.parseSql(rightSelectHandledNode);
                comboList.add(rightSelectNode);
            } else {
                LOG.warn("union中竟然有:{}类型!", rightSelect.getKind());
            }
        } else {
            LOG.warn("未处理的sql类型:{}", handledNode.getKind());
        }
        if (i == 0) {
            this.left = resultNode;
        } else if (i == 1) {
            this.right = resultNode;
        }
    }

    private SqlJoin checkNode(SqlNode node) {
        if (!(node instanceof SqlJoin)) {
            throw new IllegalArgumentException("不匹配的sqlNode类型");
        }
        return (SqlJoin) node;
    }
}
