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

package com.dtstack.taier.develop.sql.hive.node;

import com.dtstack.taier.develop.sql.Column;
import com.dtstack.taier.develop.sql.Table;
import com.dtstack.taier.develop.sql.TableOperateEnum;
import com.dtstack.taier.develop.sql.node.CreateNode;
import com.dtstack.taier.develop.sql.node.Identifier;
import com.dtstack.taier.develop.sql.node.Node;
import org.apache.calcite.sql.SqlKind;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateNodeParser extends NodeParser {
    @Override
    public CreateNode parseSql(ASTNode root, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        CreateNode createNode = new CreateNode(defultDb, tableColumnsMap);
        //  再次校验一下
        if (root.getType() == HiveParser.TOK_CREATETABLE || root.getType() == HiveParser.TOK_CREATEVIEW) {
            ArrayList<org.apache.hadoop.hive.ql.lib.Node> targetNode = root.getChildren();
            for (org.apache.hadoop.hive.ql.lib.Node node : targetNode) {
                if (((ASTNode) node).getType() == HiveParser.TOK_TABNAME) {
                    // 目标表名
                    Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                    getTableName(identifier, (ASTNode) node, defultDb);
                    createNode.setName(identifier);
                } else if (((ASTNode) node).getType() == HiveParser.TOK_QUERY) {
                    SelectNodeParser selectNodeParser = new SelectNodeParser();
                    createNode.setQuery(selectNodeParser.parseSql((ASTNode) node, defultDb, tableColumnsMap, new HashMap<>()));
                    createNode.setSqlKind(SqlKind.SELECT);
                } else if (((ASTNode) node).getType() == HiveParser.TOK_LIKETABLE) {
                    Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                    if (node.getChildren() != null && node.getChildren().size() > 0) {
                        getTableName(identifier, (ASTNode) ((ASTNode) node).getChild(0), defultDb);
                        createNode.setSqlKind(SqlKind.LIKE);
                    } else {
                        identifier = null;
                    }
                    createNode.setQuery(identifier);
                }
            }
        }
        if (createNode.getSqlKind() == null) {
            createNode.setSqlKind(SqlKind.CREATE_TABLE);
        }
        return createNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {
        if (node instanceof CreateNode) {
            CreateNode createNode = (CreateNode) node;
            Table table = new Table();
            table.setName(createNode.getName().getTable());
            table.setDb(createNode.getName().getDb());
            table.setOperate(TableOperateEnum.CREATE);
            tables.add(table);
            SelectNodeParser selectNodeParser = new SelectNodeParser();
            selectNodeParser.parseSqlTable(((CreateNode) node).getQuery(), tables);
        }
    }
}
