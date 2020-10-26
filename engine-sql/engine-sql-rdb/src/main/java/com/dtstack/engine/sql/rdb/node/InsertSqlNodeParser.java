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

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.QueryTableTree;
import com.dtstack.engine.sql.SelectColumn;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.TableOperateEnum;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析insert语句
 *
 * @author jiangbo
 * @date 2019/5/21
 */
public class InsertSqlNodeParser extends SelectSqlNodeParser {

    @Override
    public void parseSqlNode(SqlNode node, ParseResult parseResult) throws SQLException {
        result = parseResult;
        mainTable = parseResult.getMainTable();
        mainTable.setOperate(TableOperateEnum.INSERT);
        mainTable.setMain(true);
        columnLineageParser.setTableColumnMap(tableColumnMap);

        SqlInsert insert = (SqlInsert)node;
        getDbTableFromIdentifier((SqlIdentifier)insert.getTargetTable());

        SqlNode sqlNode = insert.getSource();
        if(sqlNode instanceof SqlSelect){
            QueryTableTree root = new QueryTableTree();
            parseResult.setRoot(root);
            super.parseSqlNode(sqlNode, parseResult);

            root.setName(getName(result.getCurrentDb(), mainTable.getName()));
            root.setColumns(getInsertColumns(insert));

            if (null != mainTable.getDb() && !mainTable.getDb().equals(result.getCurrentDb())) {
                //处理 主表有db 且db 不是当前项目db的这种情况
                root.setName(String.format("%s.%s", mainTable.getDb(), mainTable.getName()));
            }
            // 解析血缘
            result.setColumnLineages(columnLineageParser.parseLineage(result.getRoot()));
        }
        result.setSqlType(SqlType.INSERT);
    }

    private List<SelectColumn> getInsertColumns(SqlInsert insert){
        List<SelectColumn> selectColumns = new ArrayList<>();

        SqlNodeList columnNodeLIst = insert.getTargetColumnList();
        if(columnNodeLIst != null){
            for (SqlNode columnNode : columnNodeLIst) {
                selectColumns.add(new SelectColumn(columnNode.toString(), null));
            }
        }

        return selectColumns;
    }
}
