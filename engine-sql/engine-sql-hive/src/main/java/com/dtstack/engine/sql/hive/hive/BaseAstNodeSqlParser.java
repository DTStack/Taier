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
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.hive.ASTNodeParserImpl;
import com.dtstack.engine.sql.hive.ASTNodeUtil;
import org.apache.hadoop.hive.ql.parse.ASTNode;

import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/5/23
 */
public abstract class BaseAstNodeSqlParser implements ASTNodeParserImpl {

    protected ParseResult result;

    protected Table mainTable;

    protected void setTableAndDb(ParseResult parseResult, ASTNode root){
        Map<String,String> tableDb = ASTNodeUtil.getTableNameAndDbName(root);
        if(tableDb != null && !tableDb.isEmpty()){
            Table table = parseResult.getMainTable();
            table.setDb(tableDb.get(ASTNodeUtil.DB_NAME_KEY) == null ? parseResult.getCurrentDb() : tableDb.get(ASTNodeUtil.DB_NAME_KEY));
            table.setName(tableDb.get(ASTNodeUtil.TABLE_NAME_KEY));
            table.setMain(true);
        }
    }
}
