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
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.rdb.SqlNodeParserImpl;
import org.apache.calcite.sql.SqlIdentifier;

import java.sql.SQLException;
import java.util.List;

/**
 * @author jiangbo
 * @date 2019/5/21
 */
public abstract class BaseSqlNodeParser implements SqlNodeParserImpl {

    protected ParseResult result;

    protected Table mainTable;

    /**
     * 解析表名和数据库
     *
     * @param dbTableIdentifier
     * @throws SQLException
     */
    protected void getDbTableFromIdentifier(SqlIdentifier dbTableIdentifier) throws SQLException {
        if (dbTableIdentifier == null) {
            throw new SQLException("解析见表语句时出错,dbTableIdentifier=null,sql为:" + result.getOriginSql());
        }

        List<String> identifierList = dbTableIdentifier.names.asList();
        if (identifierList.size() == 1) {
            mainTable.setName(identifierList.get(0));
            mainTable.setDb(result.getCurrentDb());
        } else if (identifierList.size() == 2) {
            mainTable.setName(identifierList.get(1));
            mainTable.setDb(identifierList.get(0));
        }
    }
}
