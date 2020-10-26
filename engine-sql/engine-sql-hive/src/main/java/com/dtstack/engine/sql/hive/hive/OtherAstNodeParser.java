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
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.hive.ASTNodeUtil;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;


/**
 * 这个类用来解析其它操作类型，只需要解析出操作类型即可
 *
 * @author jiangbo
 * @date 2019/5/22
 */
public class OtherAstNodeParser extends BaseAstNodeSqlParser {

    @Override
    public void parseNode(ASTNode root, ParseResult parseResult) {
        switch (root.getToken().getType()) {
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
            case HiveParser.TOK_SHOW_CREATETABLE:
            case HiveParser.TOK_SHOW_TBLPROPERTIES:
            case HiveParser.TOK_SHOWPARTITIONS:
            case HiveParser.TOK_SHOWCOLUMNS:
            case HiveParser.TOK_DESCTABLE:
                parseShowSql(root, parseResult, root.getToken().getType());
                break;
            // 删表
            case HiveParser.TOK_DROPTABLE:
                parseDropSql(root, parseResult);
                break;
            // 清空表
            case HiveParser.TOK_TRUNCATETABLE:
                parseTruncateSql(root, parseResult);
                break;
            case HiveParser.TOK_LOAD:
                parseLoadSql(root, parseResult);
                break;
            case HiveParser.TOK_SHOW_SET_ROLE:
                parseResult.setSqlType(SqlType.SHOW);
                break;
            default:
                parseResult.setSqlType(SqlType.OTHER);
                break;
        }
    }

    /**
     * 解析show和desc语句
     */
    private void parseShowSql(ASTNode root, ParseResult parseResult, Integer token) {
        parseResult.setSqlType(SqlType.getType(token));

        if (ASTNodeUtil.contains(root, HiveParser.TOK_TABTYPE)) {
            root = ASTNodeUtil.getNode(root, HiveParser.TOK_TABTYPE);
        }

        if (ASTNodeUtil.contains(root, HiveParser.TOK_TABNAME)) {
            setTableAndDb(parseResult, root);
        }
    }

    /**
     * 解析load sql
     */
    private void parseLoadSql(ASTNode root, ParseResult parseResult) {
        parseResult.setSqlType(SqlType.LOAD);
        ASTNode tabNode = ASTNodeUtil.getNode(root, HiveParser.TOK_TAB, false);
        setTableAndDb(parseResult, tabNode);
    }

    /**
     * 解析drop语句
     */
    private void parseDropSql(ASTNode root, ParseResult parseResult) {
        parseResult.setSqlType(SqlType.DROP);
        setTableAndDb(parseResult, root);

        boolean isIgnore = ASTNodeUtil.contains(root, HiveParser.TOK_IFEXISTS);
        parseResult.getMainTable().setIgnore(isIgnore);
    }

    /**
     * 解析truncate语句
     */
    private void parseTruncateSql(ASTNode root, ParseResult parseResult) {
        parseResult.setSqlType(SqlType.TRUNCATE);
        setTableAndDb(parseResult, root);
    }
}
