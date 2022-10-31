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

package com.dtstack.taier.develop.sql.hive;

import com.dtstack.taier.develop.sql.hive.node.AlterNodeParser;
import com.dtstack.taier.develop.sql.hive.node.CreateNodeParser;
import com.dtstack.taier.develop.sql.hive.node.DropNodeParser;
import com.dtstack.taier.develop.sql.hive.node.InsertNodeParser;
import com.dtstack.taier.develop.sql.hive.node.NodeParser;
import com.dtstack.taier.develop.sql.hive.node.OtherNodeParser;
import com.dtstack.taier.develop.sql.hive.node.SelectNodeParser;
import com.dtstack.taier.develop.sql.utils.SqlFormatUtil;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 获取语法树解析器
 */
public class ASTNodeLineageParser {

    private static final String INSERT_REGEX = "(?i)^insert\\s+.*";

    private static ASTNodeLineageParser factory = new ASTNodeLineageParser();

    public static ASTNodeLineageParser getInstance() {
        return factory;
    }

    private static Set<Integer> alterType = new HashSet<>();

    static {
        alterType.add(HiveParser.TOK_ALTERTABLE);
        alterType.add(HiveParser.TOK_ALTERTABLE_RENAME);
        alterType.add(HiveParser.TOK_ALTERTABLE_PROPERTIES);
        alterType.add(HiveParser.TOK_ALTERTABLE_SERDEPROPERTIES);
        alterType.add(HiveParser.TOK_ALTERTABLE_ADDPARTS);
        alterType.add(HiveParser.TOK_ALTERTABLE_RENAMEPART);
        alterType.add(HiveParser.TOK_ALTERTABLE_DROPPARTS);
        alterType.add(HiveParser.TOK_ALTERTABLE_LOCATION);
        alterType.add(HiveParser.TOK_ALTERTABLE_RENAMECOL);
        alterType.add(HiveParser.TOK_ALTERTABLE_ADDCOLS);
        alterType.add(HiveParser.TOK_ALTERTABLE_REPLACECOLS);
    }

    /**
     * 获取一个血缘解析器
     * 只解析create和insert语句血缘关系
     */
    public static class ParserProxy {
        public static NodeParser getParser(ASTNode rootNode, String sql) {
            NodeParser parser = null;
            sql = SqlFormatUtil.formatSql(sql);
            if (sql.matches(INSERT_REGEX)) {
                parser = new InsertNodeParser();
                return parser;
            }
            if (rootNode.getType() == HiveParser.TOK_CREATETABLE || rootNode.getType() == HiveParser.TOK_CREATEVIEW) {
                parser = new CreateNodeParser();
            } else if (rootNode.getType() == HiveParser.TOK_QUERY) {
                ArrayList<Node> children = rootNode.getChildren();
                for (Node node : children) {
                    if (((ASTNode) node).getType() == HiveParser.TOK_INSERT) {
                        ArrayList<Node> insetNode = ((ASTNode) node).getChildren();
                        for (Node insert : insetNode) {
                            if (((ASTNode) insert).getType() == HiveParser.TOK_INSERT_INTO) {
                                parser = new InsertNodeParser();
                                return parser;
                            }
                        }
                    }
                }
                parser = new SelectNodeParser();
            } else if (rootNode.getType() == HiveParser.TOK_DROPVIEW || rootNode.getType() == HiveParser.TOK_DROPTABLE) {
                parser = new DropNodeParser();
            } else if (alterType.contains(rootNode.getType())) {
                parser = new AlterNodeParser();
            } else {
                parser = new OtherNodeParser();
            }

            return parser;
        }
    }
}
