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
import com.dtstack.taier.develop.sql.node.DropNode;
import com.dtstack.taier.develop.sql.node.Identifier;
import com.dtstack.taier.develop.sql.node.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * drop语句 目前只解析出mainTable就可以了
 * shixi
 */
public class DropNodeParser extends NodeParser {
    @Override
    public Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        DropNode dropNode = new DropNode(defultDb, tableColumnsMap);
        ArrayList<org.apache.hadoop.hive.ql.lib.Node> children = node.getChildren();
        for (org.apache.hadoop.hive.ql.lib.Node n : children) {
            if (((ASTNode) n).getType() == HiveParser.TOK_TABNAME) {
                Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                getTableName(identifier, (ASTNode) n, defultDb);
                dropNode.setTargetTable(identifier);
                break;
            }
        }
        return dropNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {

    }
}
