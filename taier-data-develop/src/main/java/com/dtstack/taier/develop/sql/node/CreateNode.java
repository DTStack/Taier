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
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.ddl.SqlCreateTable;
import org.apache.calcite.sql.ddl.SqlCreateView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/29 14:25
 * @Description: create 语法：
 * 1.CREATE TYPE 注册新数据类型
 * 2.常规create以及create table xx as select
 * 3.SqlCreateForeignSchema
 * 4.CREATE MATERIALIZED VIEW 物化视图
 * 5.create schema
 * 6.CREATE VIEW
 * 7.CREATE FUNCTION
 * <p>
 * 只处理create table
 */
public class CreateNode extends Node {

    public static Logger LOG = LoggerFactory.getLogger(CreateNode.class);

    /**
     * 表名
     */
    private Identifier name;

    /**
     * 字段。
     * 只能是identifier和basicCall(函数处理字段)
     */
    private NodeList columnList;

    /**
     * 用来标示具体类型
     */
    private SqlKind sqlKind;

    /**
     * 查询
     */
    private Node query;

    public SqlKind getSqlKind() {
        return sqlKind;
    }

    public void setSqlKind(SqlKind sqlKind) {
        this.sqlKind = sqlKind;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public NodeList getColumnList() {
        return columnList;
    }

    public void setColumnList(NodeList columnList) {
        this.columnList = columnList;
    }

    public Node getQuery() {
        return query;
    }

    public void setQuery(Node query) {
        this.query = query;
    }

    public CreateNode(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb, tableColumnsMap);
    }

    @Override
    public Node parseSql(SqlNode node) {
        checkNode(node);
        SqlNode queryNode = null;
        if (node instanceof SqlCreateView) {
            SqlCreateView sqlCreateView = (SqlCreateView) node;
            //1:columnList
            SqlNode columnsNode = sqlCreateView.getOperandList().get(1);
            handleColumnList(columnsNode);

            //0:name
            SqlNode nameNode = sqlCreateView.getOperandList().get(0);
            handleName(nameNode);

            queryNode = sqlCreateView.getOperandList().get(2);
        } else {
            SqlCreateTable sqlCreateTable = (SqlCreateTable) node;
            //1:columnList
            SqlNode columnsNode = sqlCreateTable.getOperandList().get(1);
            handleColumnList(columnsNode);

            //0:name
            SqlNode nameNode = sqlCreateTable.getOperandList().get(0);
            handleName(nameNode);

            queryNode = sqlCreateTable.getOperandList().get(2);
        }

        handleQuery(queryNode);
        return null;
    }

    private void handleQuery(SqlNode queryNode) {
        if (queryNode instanceof SqlBasicCall) {
            SqlOperator operator = ((SqlBasicCall) queryNode).getOperator();
            if (SqlKind.UNION == operator.kind) {
                UnionCall unionCall = new UnionCall(getDefaultDb(), getTableColumnMap());
                unionCall.parseSql(queryNode);
                NodeList nodeList = new NodeList(getDefaultDb(), getTableColumnMap());
                nodeList.setContext(Context.INSERT_FROM_UNION);
                List<Node> nodes = Lists.newArrayList();
                nodes.addAll(unionCall.getComboFromList());
                nodeList.setList(nodes);
                this.query = nodeList;
            }
        } else if (queryNode instanceof SqlSelect) {
            SelectNode selectNode = new SelectNode(getDefaultDb(), getTableColumnMap());
            selectNode.parseSql(queryNode);
            this.query = selectNode;
        }
    }

    private void handleColumnList(SqlNode sqlNode) {
        if (sqlNode == null) {
            return;
        }
        if (sqlNode instanceof SqlNodeList) {
            NodeList nodeList = new NodeList(getDefaultDb(), getTableColumnMap());
            List<Node> list = Lists.newArrayList();
            for (SqlNode sn : ((SqlNodeList) sqlNode).getList()) {
                //合并as
                Pair<String, SqlNode> sqlNodePair = removeAs(sn);
                SqlNode handledNode = sn;
                String alias = null;
                if (sqlNodePair != null) {
                    handledNode = sqlNodePair.getValue();
                    alias = sqlNodePair.getKey();
                }
                if (handledNode instanceof SqlIdentifier) {
                    Identifier identifier = new Identifier(getDefaultDb(), getTableColumnMap());
                    identifier.setAlias(alias);
                    identifier.setContext(Context.IDENTIFIER_COLUMN);
                    identifier.parseSql(handledNode);
                    list.add(identifier);
                } else {
                    LOG.warn("未处理的sql类型:{}", handledNode.getKind());
                }
            }
            nodeList.setList(list);
            this.columnList = nodeList;
        } else {
            LOG.warn("字段类型异常:{}", sqlNode.getKind());
        }
    }

    private void handleName(SqlNode nameNode) {
        if (nameNode instanceof SqlIdentifier) {
            Identifier identifier = new Identifier(getDefaultDb(), getTableColumnMap());
            identifier.setContext(Context.IDENTIFIER_TABLE);
            identifier.parseSql(nameNode);
            this.name = identifier;
        } else {
            LOG.warn("表名类型异常:{}", nameNode.getKind());
        }
    }

    private void checkNode(SqlNode node) {
        if (node instanceof SqlCreateTable) {
            if (SqlKind.CREATE_TABLE == ((SqlCreateTable) node).getOperator().getKind()) {
                return;
            }
        }
        if (node instanceof SqlCreateView) {
            if (SqlKind.CREATE_VIEW == ((SqlCreateView) node).getOperator().getKind()) {
                return;
            }
        }
        throw new IllegalArgumentException("不匹配的sqlNode类型");
    }
}
