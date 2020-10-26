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
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.hive.ASTNodeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/5/23
 */
public class CreateTableAstNodeParser extends SelectAstNodeParser {

    private static final String STORE_TYPE_TEXT = "TEXTFILE";

    private static final String STORE_TYPE_ORC = "ORC";

    private final static String PARQUET_STORE_TYPE = "PARQUET";

    /**
     * 解析create语句，由于建表语句包括 create like语句，无法解析出具体的字段和其它信息
     * 因此这里只解析出表名，由外部系统自己根据表名查询具体信息
     *
     * @param root
     * @param parseResult
     */
    @Override
    public void parseNode(ASTNode root, ParseResult parseResult) throws SQLException {
        result = parseResult;
        mainTable = result.getMainTable();
        setTableAndDb(result, root);
        columnLineageParser.setTableColumnMap(tableColumnMap);

        // 获取创建的表名
        Map<String, String> tableDb = ASTNodeUtil.getTableNameAndDbName(root);
        if (tableDb != null && !tableDb.isEmpty()) {
            mainTable.setName(tableDb.get(ASTNodeUtil.TABLE_NAME_KEY));
            mainTable.setDb(tableDb.get(ASTNodeUtil.DB_NAME_KEY) == null ? parseResult.getCurrentDb() : tableDb.get(ASTNodeUtil.DB_NAME_KEY));
            mainTable.setAlias(String.format("%s.%s", mainTable.getDb(), mainTable.getName()));
        }

        ASTNode queryNode = ASTNodeUtil.getNode(root, HiveParser.TOK_QUERY, false);
        if (queryNode == null) {
            parseCreateTableWithoutQuery(root);
        } else {
            parseCreateTableWithQuery(queryNode);
        }

        // 存储格式
        parseStoreType(root);

        // 是否包含if not exists
        boolean isIgnore = ASTNodeUtil.contains(root, HiveParser.TOK_IFNOTEXISTS);
        mainTable.setIgnore(isIgnore);
        mainTable.setMain(true);
    }

    /**
     * 解析没有查询语句的见表语句
     */
    private void parseCreateTableWithoutQuery(ASTNode root) {
        ASTNode likeTableNode = ASTNodeUtil.getNode(root, HiveParser.TOK_LIKETABLE, false);
        if (likeTableNode != null) {
            if (CollectionUtils.isEmpty(likeTableNode.getChildren())) {
                // 普通建表语句
                result.setSqlType(SqlType.CREATE);
            } else {
                // create table like
                result.setSqlType(SqlType.CREATE_LIKE);
                List<String> values = ASTNodeUtil.getNodeValue((ASTNode) likeTableNode.getChild(0));
                if (values.size() == 1) {
                    mainTable.setLikeTable(values.get(0));
                    mainTable.setLikeTableDb(result.getCurrentDb());
                } else {
                    mainTable.setLikeTable(values.get(1));
                    mainTable.setLikeTableDb(values.get(0));
                }
            }
        }
    }

    /**
     * 解析带有查询语句的建表语句
     */
    private void parseCreateTableWithQuery(ASTNode queryNode) throws SQLException {
        QueryTableTree root = new QueryTableTree();
        root.setName(mainTable.getName());
        root.setColumns(columnLineageParser.getSelectColumn(mainTable.getName()));

        if (null != mainTable.getDb() && !mainTable.getDb().equals(result.getCurrentDb())) {
            //处理 主表有
            root.setName(String.format("%s.%s",mainTable.getDb(),mainTable.getName()));
        }

        result.setRoot(root);
        result.setSqlType(SqlType.CREATE_AS);

        super.parseNode(queryNode, result);

        // 解析血缘
        result.setColumnLineages(columnLineageParser.parseLineage(result.getRoot()));
    }

    /**
     * 解析表存储格式，目前只解析 text,orc,parquet三种格式
     *
     * @param root
     */
    private void parseStoreType(ASTNode root) {
        List<String> nodeValues;
        ASTNode tableStoreType = ASTNodeUtil.getNode(root, HiveParser.TOK_FILEFORMAT_GENERIC, false);
        if (tableStoreType != null) {
            nodeValues = ASTNodeUtil.getNodeValue(tableStoreType);
            mainTable.setStoreType(nodeValues.get(0).toUpperCase());
        } else {
            tableStoreType = ASTNodeUtil.getNode(root, HiveParser.TOK_TABLEFILEFORMAT, false);
            if (tableStoreType != null) {
                nodeValues = ASTNodeUtil.getNodeValue(tableStoreType);
                if (nodeValues.get(0).contains("OrcInputFormat")) {
                    mainTable.setStoreType(STORE_TYPE_ORC);
                } else if (nodeValues.get(0).contains("TextInputFormat")) {
                    mainTable.setStoreType(STORE_TYPE_TEXT);
                } else if (nodeValues.get(0).contains("MapredParquetInputFormat")) {
                    mainTable.setStoreType(PARQUET_STORE_TYPE);
                }
            }
        }
    }
}
