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
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.QueryTableTree;
import com.dtstack.engine.sql.SelectColumn;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.utils.SqlRegexUtil;
import org.dtstack.apache.calcite.sql.SqlDataTypeSpec;
import org.dtstack.apache.calcite.sql.SqlIdentifier;
import org.dtstack.apache.calcite.sql.SqlNode;
import org.dtstack.apache.calcite.sql.SqlNodeList;
import org.dtstack.apache.calcite.sql.ddl.SqlColumnDeclaration;
import org.dtstack.apache.calcite.sql.ddl.SqlCreateTable;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/5/21
 */
public class CreateTableSqlNodeParser extends SelectSqlNodeParser {

    private String sql;

    @Override
    public void parseSqlNode(SqlNode node, ParseResult parseResult) throws SQLException {
        result = parseResult;
        mainTable = result.getMainTable();
        result.setSqlType(SqlType.CREATE);
        mainTable.setOperate(TableOperateEnum.CREATE);

        if (StringUtils.isNotEmpty(sql)) {
            if(SqlRegexUtil.isCreateLike(sql)){
                parseCreateTableLike();
                return;
            }
        }

        SqlCreateTable createTable = (SqlCreateTable) node;
        List<SqlNode> operandList = createTable.getOperandList();
        SqlIdentifier dbTableIdentifier = (SqlIdentifier) operandList.get(0);
        SqlNodeList columnNodeList = (SqlNodeList) operandList.get(1);
        SqlNode query = operandList.get(2);

        getDbTableFromIdentifier(dbTableIdentifier);
        getColumnInfo(columnNodeList);
        parseQuerySql(query);
    }

    /**
     * create table as select ...
     * 这里要解析血缘
     */
    private void parseQuerySql(SqlNode query) throws SQLException {
        if (query == null) {
            return;
        }

        QueryTableTree root = new QueryTableTree();
        root.setName(getName(result.getCurrentDb(), mainTable.getName()));
        root.setColumns(getSelectColumns());

        result.setRoot(root);
        super.parseSqlNode(query, result);

        result.setSqlType(SqlType.CREATE_AS);

        if (null != mainTable.getDb() && !mainTable.getDb().equals(result.getCurrentDb())) {
            //处理 主表有db 且db 不是当前项目db的这种情况
            root.setName(String.format("%s.%s", mainTable.getDb(), mainTable.getName()));
        }

        // 解析血缘
        result.setColumnLineages(columnLineageParser.parseLineage(result.getRoot()));
    }

    private List<SelectColumn> getSelectColumns() {
        List<SelectColumn> selectColumns = new ArrayList<>();

        for (Column column : mainTable.getColumns()) {
            selectColumns.add(new SelectColumn(column.getName(), null));
        }

        return selectColumns;
    }

    /**
     * 解析字段信息
     *
     * @param sqlNodeList
     */
    private void getColumnInfo(SqlNodeList sqlNodeList) {
        // 可以建没有字段的表
        if (sqlNodeList == null) {
            return;
        }

        List<Column> columns = new ArrayList<>();

        List<SqlNode> columnNodes = sqlNodeList.getList();
        for (SqlNode columnNode : columnNodes) {
            if (columnNode instanceof SqlColumnDeclaration) {
                Column column = new Column();
                column.setTable(mainTable.getName());

                List<SqlNode> sqlNodes = ((SqlColumnDeclaration) columnNode).getOperandList();
                SqlIdentifier name = (SqlIdentifier) sqlNodes.get(0);
                SqlDataTypeSpec typeSpec = (SqlDataTypeSpec) sqlNodes.get(1);

                column.setName(name.names.get(0));
                column.setType(typeSpec.getTypeName().names.get(0));

                columns.add(column);
            }
        }

        mainTable.setColumns(columns);
    }

    /**
     * 从create like语句中解析建表信息
     */
    private void parseCreateTableLike() {
        result.setSqlType(SqlType.CREATE_LIKE);
        Map<String, String> dbTable = SqlRegexUtil.parseDbTableFromLikeSql(result.getStandardSql());

        Table table = result.getMainTable();
        table.setDb(dbTable.get(SqlRegexUtil.KEY_DB) == null ? result.getCurrentDb() : dbTable.get(SqlRegexUtil.KEY_DB));
        table.setName(dbTable.get(SqlRegexUtil.KEY_TABLE));
        table.setLikeTableDb(dbTable.get(SqlRegexUtil.KEY_LIKE_DB) == null ? result.getCurrentDb() : dbTable.get(SqlRegexUtil.KEY_LIKE_DB));
        table.setLikeTable(dbTable.get(SqlRegexUtil.KEY_LIKE_TABLE));
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
