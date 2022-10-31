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
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlType;
import com.dtstack.taier.develop.sql.Table;
import com.dtstack.taier.develop.sql.node.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用来解析 一些表结构操作 数据库操作
 */
public class OtherNodeParser extends NodeParser{


    /**
     * 一些特殊操作 不解析操作类型的 只获取到sql类型
     */
    public void getSqlType(ASTNode node, ParseResult parseResult){
        switch (node.getToken().getType()) {
            // 数据库操作
            case HiveParser.TOK_CREATEDATABASE:
            case HiveParser.TOK_DROPDATABASE:
            case HiveParser.TOK_ALTERDATABASE_OWNER:
            case HiveParser.TOK_ALTERDATABASE_PROPERTIES:
            case HiveParser.TOK_SWITCHDATABASE:
            case HiveParser.TOK_SHOWDATABASES:
            case HiveParser.TOK_DESCDATABASE:
                parseResult.setSqlType(SqlType.DATABASE_OPERATE);
                break;
            // 函数操作
            case HiveParser.TOK_CREATEFUNCTION:
                parseResult.setSqlType(SqlType.CREATE_FUNCTION);
                break;
            case HiveParser.TOK_DROPFUNCTION:
                parseResult.setSqlType(SqlType.DROP_FUNCTION);
                break;
            case HiveParser.TOK_RELOADFUNCTION:
                parseResult.setSqlType(SqlType.RELOAD_FUNCTION);
                break;
            case HiveParser.TOK_SHOWFUNCTIONS:
            case HiveParser.TOK_DESCFUNCTION:
                parseResult.setSqlType(SqlType.SHOW_FUNCTION);
                break;
            case HiveParser.TOK_UPDATE_TABLE:
                parseResult.setSqlType(SqlType.UPDATE);
                break;
            case HiveParser.TOK_DELETE_FROM:
                parseResult.setSqlType(SqlType.DELETE);
                break;
            case HiveParser.TOK_EXPLAIN:
                parseResult.setSqlType(SqlType.EXPLAIN);
                break;
            // 查看表信息
            case HiveParser.TOK_SHOWTABLES:
                parseResult.setSqlType(SqlType.SHOW_TABLES);
                break;
            case HiveParser.TOK_SHOW_CREATETABLE:
                parseResult.setSqlType(SqlType.SHOW_CREATETABLE);
                break;
            case HiveParser.TOK_SHOW_TBLPROPERTIES:
                parseResult.setSqlType(SqlType.SHOW_TBLPROPERTIES);
                break;
            case HiveParser.TOK_SHOWPARTITIONS:
                parseResult.setSqlType(SqlType.SHOW_PARTITIONS);
                break;
            case HiveParser.TOK_SHOWCOLUMNS:
                parseResult.setSqlType(SqlType.SHOW_COLUMNS);
                break;
            case HiveParser.TOK_DESCTABLE:
                parseResult.setSqlType(SqlType.DESC_TABLE);
                break;
            // 删表
            case HiveParser.TOK_DROPTABLE:
                parseResult.setSqlType(SqlType.DROP);
                break;
            // 清空表
            case HiveParser.TOK_TRUNCATETABLE:
                parseResult.setSqlType(SqlType.TRUNCATE);
                break;
            case HiveParser.TOK_LOAD:
                parseResult.setSqlType(SqlType.LOAD);
                break;
            case HiveParser.TOK_SHOW_SET_ROLE:
            case HiveParser.TOK_SHOW_ROLES:
                parseResult.setSqlType(SqlType.SHOW);
                break;
            default:
                parseResult.setSqlType(SqlType.OTHER);
                break;
        }
    }
    @Override
    public Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        return null;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {

    }
}
