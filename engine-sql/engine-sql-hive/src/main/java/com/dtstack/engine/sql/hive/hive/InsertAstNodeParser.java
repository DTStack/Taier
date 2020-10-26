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


import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.QueryTableTree;
import com.dtstack.engine.sql.SelectColumn;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.hive.ASTNodeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangbo
 * @date 2019/5/25
 */
public class InsertAstNodeParser extends SelectAstNodeParser {

    private static final String INSERT_OVERWRITE_REGEX = "(?i)insert\\s+overwrite\\s+.*";

    @Override
    public void parseNode(ASTNode rootNode, ParseResult parseResult) throws SQLException {
        result = parseResult;
        mainTable = result.getMainTable();
        setTableAndDb(result, rootNode);
        columnLineageParser.setTableColumnMap(tableColumnMap);

        result.setSqlType(SqlType.INSERT);
        if (result.getStandardSql().matches(INSERT_OVERWRITE_REGEX)) {
            result.setSqlType(SqlType.INSERT_OVERWRITE);
        }

        QueryTableTree rootTree = new QueryTableTree();
        parseResult.setRoot(rootTree);
        super.parseNode(rootNode, parseResult);

        String insertTable = getInsertTable(rootNode);
        setMainTableInfo(insertTable);

        rootTree.setName(insertTable);
        rootTree.setColumns(getInsertColumns(rootNode));

        if (null != mainTable.getDb() && null != mainTable.getName() && mainTable.getName().equals(insertTable)) {
            rootTree.setName(String.format("%s.%s", mainTable.getDb(), mainTable.getName()));
        }

        result.setColumnLineages(columnLineageParser.parseLineage(result.getRoot()));
    }

    private void setMainTableInfo(String insertTable){
        if(StringUtils.isEmpty(insertTable)){
            return;
        }

        String[] splits = insertTable.split("\\.");
        mainTable.setDb(splits[0]);
        String tableName = splits[1];
        if (StringUtils.isNotBlank(tableName)) {
            tableName = tableName.replaceAll("`", "").replaceAll("'", "").trim();
        }
        mainTable.setName(tableName);
        mainTable.setMain(true);
    }

    private String getInsertTable(ASTNode rootNode) {
        ASTNode insertNode = ASTNodeUtil.getNode(rootNode, HiveParser.TOK_INSERT);
        ASTNode tableNameNode = ASTNodeUtil.getNode(insertNode, HiveParser.TOK_TABNAME, true);
        if (tableNameNode == null) {
            return null;
        }

        List<Node> children = tableNameNode.getChildren();
        if (children.size() == 1) {
            return result.getCurrentDb() + "." + children.get(0).toString();
        }

        if (children.size() == 2) {
            return children.get(0).toString() + "." + children.get(1).toString();
        }

        return null;
    }

    private List<SelectColumn> getInsertColumns(ASTNode rootNode) {
        List<SelectColumn> selectColumns = new ArrayList<>();

        ASTNode insertNode = ASTNodeUtil.getNode(rootNode, HiveParser.TOK_INSERT);
        ASTNode insertIntoNode = ASTNodeUtil.getNode(insertNode, HiveParser.TOK_INSERT_INTO);
        if (insertIntoNode != null) {
            ASTNode insertColumnNode = ASTNodeUtil.getNode(insertIntoNode, HiveParser.TOK_TABCOLNAME);
            if (insertColumnNode != null && CollectionUtils.isNotEmpty(insertColumnNode.getChildren())) {
                for (Node child : insertColumnNode.getChildren()) {
                    selectColumns.add(new SelectColumn(((ASTNode) child).getText(), null));
                }
            }
        }

        return selectColumns;
    }
}
