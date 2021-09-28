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

package com.dtstack.batch.utils;

import com.dtstack.batch.bo.ParseResult;
import com.dtstack.engine.lineage.adapter.AlterResultAdapter;
import com.dtstack.engine.lineage.adapter.SqlTypeAdapter;
import com.dtstack.engine.lineage.adapter.TableAdapter;

/**
 * @author beihai
 * @Description parseResult
 * @Date 2021/4/1 19:24
 */
public class ParseResultUtils {

    public static ParseResult convertParseResult(com.dtstack.sqlparser.common.client.domain.ParseResult originResult) {
        ParseResult parseResult = new ParseResult();
        parseResult.setMainDb(originResult.getCurrentDb());
        parseResult.setMainTable(TableAdapter.sqlTable2ApiTable(originResult.getMainTable()));
        parseResult.setParseSuccess(originResult.isParseSuccess());
        parseResult.setFailedMsg(originResult.getFailedMsg());
        parseResult.setStandardSql(originResult.getStandardSql());
        parseResult.setOriginSql(originResult.getOriginSql());
        parseResult.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(originResult.getSqlType()));
        parseResult.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(originResult.getExtraSqlType()));
        parseResult.setCurrentDb(originResult.getCurrentDb());
        parseResult.setAlterResult(AlterResultAdapter.sqlAlterResult2ApiResult(originResult.getAlterResult()));
        return parseResult;
    }

}
