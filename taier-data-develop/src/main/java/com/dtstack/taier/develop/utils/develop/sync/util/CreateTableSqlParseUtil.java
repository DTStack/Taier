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

package com.dtstack.taier.develop.utils.develop.sync.util;

import com.dtstack.taier.common.exception.DtCenterDefException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateTableSqlParseUtil {

    private static final String CREATE_REGEX = "(?i)create\\s+(.*\\s+)*table\\s+(if\\s+not\\s+exists\\s+)*(?<tableName>[\\.`'\"a-zA-Z0-9_]+)\\s*.*";

    private static final Pattern CREATE_PATTERN = Pattern.compile(CREATE_REGEX);

    public static String parseTableName(String originSql){
        String sql = originSql.replace("\n", "").replace("\r", "");
        if(sql.contains("(")){
            sql = sql.substring(0, sql.indexOf("("));
        }

        if(!sql.matches(CREATE_REGEX)){
            throw new DtCenterDefException("Only accept sql like 'create table ....'");
        }

        String tableName = null;
        Matcher matcher = CREATE_PATTERN.matcher(sql);
        if (matcher.find()) {
            tableName =  matcher.group("tableName");
        }

        if(tableName == null){
            throw new DtCenterDefException("Can not parse tableName from sql:" + sql);
        }

        if (tableName.contains(".")){
            tableName = tableName.split("\\.")[1];
        }

        if(tableName.contains("`") || tableName.contains("'") || tableName.contains("\"")){
            tableName = tableName.substring(1, tableName.length() - 1);
        }

        return tableName;
    }
}
