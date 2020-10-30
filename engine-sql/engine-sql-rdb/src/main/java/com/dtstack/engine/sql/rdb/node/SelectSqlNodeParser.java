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


package com.dtstack.engine.sql.rdb.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ColumnLineageParser;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.QueryTableTree;
import com.dtstack.engine.sql.SelectColumn;
import com.dtstack.engine.sql.SqlType;
import org.dtstack.apache.calcite.sql.SqlBasicCall;
import org.dtstack.apache.calcite.sql.SqlFunction;
import org.dtstack.apache.calcite.sql.SqlIdentifier;
import org.dtstack.apache.calcite.sql.SqlJoin;
import org.dtstack.apache.calcite.sql.SqlKind;
import org.dtstack.apache.calcite.sql.SqlNode;
import org.dtstack.apache.calcite.sql.SqlNodeList;
import org.dtstack.apache.calcite.sql.SqlOperator;
import org.dtstack.apache.calcite.sql.SqlOrderBy;
import org.dtstack.apache.calcite.sql.SqlSelect;
import org.dtstack.apache.calcite.sql.SqlWith;
import org.dtstack.apache.calcite.sql.SqlWithItem;
import org.dtstack.apache.calcite.sql.fun.SqlCase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析查询语句，主要是为了解析血缘
 *
 * @author jiangbo
 * @date 2019/5/21
 */
public class SelectSqlNodeParser extends BaseSqlNodeParser {

    public static Logger LOG = LoggerFactory.getLogger(SelectSqlNodeParser.class);

    private static final String SPLIT_DOT = ".";

    protected Map<String,List<Column>> tableColumnMap;

    protected ColumnLineageParser columnLineageParser = new ColumnLineageParser();

    /**
     * 解析 SelectNode
     *
     * @param node
     * @param parseResult
     * @throws SQLException
     */
    @Override
    public void parseSqlNode(SqlNode node, ParseResult parseResult) throws SQLException {
        result = parseResult;
        result.setSqlType(SqlType.QUERY);
        mainTable = parseResult.getMainTable();

        QueryTableTree root = new QueryTableTree();
        if(result.getRoot() == null){
            result.setRoot(root);
        } else {
            result.getRoot().addChild(root);
        }

        parseSqlNode(node, null, root);
        columnLineageParser.setTableColumnMap(tableColumnMap);
        columnLineageParser.pretreatment(result.getRoot());
    }

    public void setTableColumnMap(Map<String, List<Column>> tableColumnMap) {
        this.tableColumnMap = tableColumnMap;
    }

    private void parseSqlNode(SqlNode sqlNode, String alias, QueryTableTree root){
        if(sqlNode instanceof SqlBasicCall){
            parseSqlBasicCall((SqlBasicCall)sqlNode, root);
        } else if(sqlNode instanceof SqlWith) {
            parseSqlWith((SqlWith)sqlNode, root);
        } else if (sqlNode instanceof SqlJoin){
            parseSqlJoin((SqlJoin)sqlNode,root);
        } else if (sqlNode instanceof SqlOrderBy){
            parseSqlSelect((SqlSelect) ((SqlOrderBy) sqlNode).query,alias,root);
        }
        else {
            parseSqlSelect((SqlSelect) sqlNode, alias, root);
        }
    }

    private void parseSqlJoin(SqlJoin sqlNode, QueryTableTree root) {
        sqlNode.getJoinType();
        SqlNode left = sqlNode.getLeft();
        SqlNode right = sqlNode.getRight();
        if (left != null && left instanceof SqlBasicCall){
            parseSqlBasicCall((SqlBasicCall)left, root);
        }
        if (right != null && right instanceof SqlBasicCall){
            parseSqlBasicCall((SqlBasicCall)right, root);
        }
    }

    private void parseSqlWith(SqlWith sqlWith, QueryTableTree root){
        List<SqlNode> operandList = sqlWith.getOperandList();
        QueryTableTree withItemQuery =  new QueryTableTree();
        SqlNodeList nodesList = (SqlNodeList) operandList.get(0);
        for (SqlNode sqlNode : nodesList.getList()) {
            parseSqlWithItem((SqlWithItem)sqlNode, withItemQuery);
        }

        Map<String, QueryTableTree> withQueryMap = new HashMap<>();
        for (QueryTableTree child : withItemQuery.getChildren()) {
            withQueryMap.put(removeDb(child.getAlias()), child);
        }

        SqlSelect body = (SqlSelect) operandList.get(1);
        parseSqlSelect(body, null, root);
        mappingWithQuery(withQueryMap, root);
    }

    private void mappingWithQuery(Map<String, QueryTableTree> withQueryMap, QueryTableTree bodyQueryTree){
        if(bodyQueryTree.getName() != null){
            String name = removeDb(bodyQueryTree.getName());
            QueryTableTree mapping = withQueryMap.get(name);
            if(mapping != null){
                bodyQueryTree.setAlias(name);
                bodyQueryTree.addChild(mapping);
            }
        }

        if (bodyQueryTree.getChildren().size() > 0) {
            for (QueryTableTree child : bodyQueryTree.getChildren()) {
                mappingWithQuery(withQueryMap, child);
            }
        }
    }

    private String removeDb(String table){
        if(!table.contains(".")){
            return table;
        }

        return table.split("\\.")[1];
    }

    private void parseSqlWithItem(SqlWithItem item, QueryTableTree root){
        QueryTableTree child = new QueryTableTree();
        child.setAlias(item.name.toString());
        root.addChild(child);
        parseSqlSelect((SqlSelect) item.query, item.name.toString(), child);
    }

    private void parseSqlBasicCall(SqlBasicCall sqlBasicCall, QueryTableTree root){
        SqlKind kind = sqlBasicCall.getOperator().kind;
        if(SqlKind.AS == kind){
            // temp和join
            List<SqlNode> operands = sqlBasicCall.getOperandList();

            String alias = operands.get(1).toString();
            root.setAlias(alias);
            if(operands.get(0) instanceof SqlIdentifier){
                // join tb
                root.setName(getName(result.getCurrentDb(), operands.get(0).toString()));
            } else {
                parseSqlNode(operands.get(0), alias, root);
            }
        } else if(SqlKind.UNION == kind){
            for (SqlNode sqlNode : sqlBasicCall.getOperandList()) {
                QueryTableTree child = new QueryTableTree();
                root.addChild(child);
                parseSqlNode(sqlNode, null, child);
            }
        }
    }

    protected String getName(String dbOrTable, String tableOrColumn){
        if(!tableOrColumn.contains(SPLIT_DOT) && StringUtils.isNotEmpty(dbOrTable)){
            tableOrColumn = dbOrTable + SPLIT_DOT + tableOrColumn;
        }

        return tableOrColumn;
    }

    private void parseSqlSelect(SqlSelect sqlSelect,String alias, QueryTableTree root){
        //deal from
        dealFrom(sqlSelect.getFrom(), alias, root);

        //deal select
        dealSelectColumns(sqlSelect.getSelectList(), alias, root);
    }

    private void dealFrom(SqlNode tableNode, String alias, QueryTableTree root) {
        switch (tableNode.getKind()) {
            case IDENTIFIER:
                root.setName(getName(result.getCurrentDb(), tableNode.toString()));
                root.setAlias(alias);
                break;
            case AS:
                // 这里需要单独处理 select * from db.tb alias 这种情况，不用再生成子节点去处理了
                if(tableNode instanceof SqlBasicCall){
                    List<SqlNode> operandList = ((SqlBasicCall) tableNode).getOperandList();
                    if(operandList.get(0) instanceof SqlIdentifier){
                        root.setAlias(alias);
                        root.setName(getName(result.getCurrentDb(), operandList.get(0).toString()));

                        break;
                    }
                }

                QueryTableTree child = new QueryTableTree();
                root.addChild(child);
                parseSqlNode(tableNode, alias, child);
                break;
            case JOIN:
                dealJoinNode(tableNode,root);
                break;
            case UNION:
                if (tableNode instanceof SqlBasicCall){
                    for (SqlNode sqlNode:((SqlBasicCall) tableNode).getOperandList()){
                        QueryTableTree unionChild = new QueryTableTree();
                        root.addChild(unionChild);
                        root.setAlias(alias);
                        parseSqlNode(sqlNode,alias,unionChild);
                    }
                }else {
                    LOG.debug("warning!!! 未处理的union case");
                }
                break;
            case SELECT:
                if (tableNode instanceof SqlSelect){
                    QueryTableTree selectChild = new QueryTableTree();
                    root.addChild(selectChild);
                    root.setAlias(alias);
                    parseSqlSelect((SqlSelect) tableNode,alias,selectChild);
                }else {
                    LOG.debug("warning!!! 未处理的select case");
                }
                break;
            default:
        }
    }

    private void dealJoinNode(SqlNode sqlNode, QueryTableTree child){
        if(sqlNode instanceof SqlJoin){
            QueryTableTree child1 = new QueryTableTree();
            QueryTableTree child2 = new QueryTableTree();
            child.addChild(child1);
            child.addChild(child2);
            SqlJoin joinTab = (SqlJoin) sqlNode;
            dealJoinNode(joinTab.getLeft(),child1);
            dealJoinNode(joinTab.getRight(),child2);
        }else{
            if(sqlNode instanceof SqlIdentifier){
                child.setName(getName(result.getCurrentDb(), sqlNode.toString()));
            } else {
                parseSqlNode(sqlNode, null, child);
            }
        }

    }

    private void dealSelectColumns(SqlNodeList selectList,String tableName, QueryTableTree current) {
        List<SelectColumn> columns = new ArrayList<>();
        for (SqlNode sqlNode : selectList) {
            dealSingleColumn(sqlNode, tableName, columns, null);
        }

        current.setColumns(columns);
    }

    /**
     * 处理单个查询字段
     *
     * @param columnNode
     * @param tableName
     * @param columns
     */
    private void dealSingleColumn(SqlNode columnNode, String tableName, List<SelectColumn> columns, String columnAlias){
        switch (columnNode.getKind()) {
            case IDENTIFIER:
                // select name| u.nae
                String name = getName(tableName, columnNode.toString());
                columns.add(new SelectColumn(name, columnAlias));
                break;
            case AS:
                SqlBasicCall sbc = (SqlBasicCall) columnNode;
                SqlNode[] operands = sbc.getOperands();
                if (operands[0] instanceof SqlBasicCall) {
                    dealFunCol(operands[0], operands[1].toString(), columns);
                } else if(operands[0] instanceof SqlIdentifier){
                    // user.name as u_name
                    String tabAndCol = getName(tableName, operands[0].toString());

                    String colAlias = operands[1].toString();
                    columns.add(new SelectColumn(tabAndCol, colAlias));
                } else if(operands[0] instanceof SqlCase){
                    String colAlias = null;
                    if (operands.length > 1){
                        colAlias = operands[1].toString();
                    }
                    dealCaseWhen((SqlCase)operands[0], tableName, columns, colAlias);
                }
                break;
            case LITERAL:
                //常量
                columns.add(new SelectColumn(SelectColumn.CONSTANT, columnAlias));
                break;
            default:
                dealFunCol(columnNode, columnNode.getKind().toString(), columns);
        }
    }

    private void dealCaseWhen(SqlCase sqlCase, String tableName, List<SelectColumn> columns, String columnAlias){
        for (SqlNode sqlNode : sqlCase.getThenOperands().getList()) {
            dealSingleColumn(sqlNode, tableName, columns, columnAlias);
        }

        dealSingleColumn(sqlCase.getElseOperand(), tableName, columns, columnAlias);
    }

    /**
     * 解析使用了函数的字段
     *
     * @param colFunc
     * @param columnAlias 字段别名
     */
    private void dealFunCol(SqlNode colFunc, String columnAlias, List<SelectColumn> columns) {
        if(!(colFunc instanceof SqlBasicCall)){
            return;
        }

        SqlOperator operatorType = ((SqlBasicCall) colFunc).getOperator();
        if (!(operatorType instanceof SqlFunction)) {
            return;
        }

        ((SqlBasicCall) colFunc).getOperandList().forEach(c -> {
            //处理函数中包含的非*字段
            if (c instanceof SqlIdentifier && !StringUtils.equalsIgnoreCase("*", c.toString())) {
                columns.add(new SelectColumn(c.toString(), columnAlias));
            }
        });
    }

}
