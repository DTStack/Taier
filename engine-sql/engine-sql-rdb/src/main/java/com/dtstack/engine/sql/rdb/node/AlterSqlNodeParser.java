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
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.utils.SqlRegexUtil;
import org.dtstack.apache.calcite.sql.SqlNode;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

/**
 * TODO calcite不支持 alter语句，先用正则解析
 * @author jiangbo
 * @date 2019/6/13
 */
public class AlterSqlNodeParser extends BaseSqlNodeParser {

    private String sql;

    @Override
    public void parseSqlNode(SqlNode node, ParseResult parseResult) throws SQLException {
        Map<String, String> dbTable = SqlRegexUtil.parseDbTableFromAlterSql(sql);
        if (Objects.isNull(parseResult.getMainTable())) {
            parseResult.setMainTable(new Table());
        }
        parseResult.getMainTable().setName(dbTable.get(SqlRegexUtil.KEY_TABLE));
        parseResult.getMainTable().setDb(dbTable.get(SqlRegexUtil.KEY_DB));
        parseResult.getMainTable().setOperate(TableOperateEnum.ALTER);
        if(parseResult.getMainTable().getDb() == null){
            parseResult.getMainTable().setDb(parseResult.getCurrentDb());
        }
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
