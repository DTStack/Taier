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

package com.dtstack.taier.develop.sql.handler;

import com.dtstack.taier.develop.sql.utils.SqlFormatUtil;
import com.dtstack.taier.develop.sql.utils.SqlRegexUtil;

import java.util.regex.Matcher;

/**
 * @author yuebai
 * @date 2019-12-23
 */
public class HiveUglySqlHandler implements IUglySqlHandler {
    private String sql;

    private String formattedSql;

    private int uglySqlMode = 0b00000000;
    private int sqlMode = 0b00000000;

    public int getSqlMode() {
        return sqlMode;
    }

    @Override
    public boolean isTemp() {
        return (this.getSqlMode() & IUglySqlHandler.CREATE_TEMP) == IUglySqlHandler.CREATE_TEMP;
    }


    private void initSql() {
        formattedSql = SqlFormatUtil.getStandardSql(SqlFormatUtil.formatSql(sql));
        formattedSql = formattedSql.trim();
        if (formattedSql.endsWith(";")) {
            formattedSql = formattedSql.substring(0, formattedSql.length() - 1);
        }
    }

    private void initSqlMode() {
        this.sqlMode = 0b00000000;
        this.uglySqlMode = 0b00000000;
        if (SqlRegexUtil.isCreateTemp(formattedSql)) {
            uglySqlMode = uglySqlMode + CREATE_TEMP;
        }
        sqlMode = uglySqlMode;
    }

    private boolean parseUgly(int sqlMode) {
        int flag = uglySqlMode & sqlMode;
        if (flag == 0) {
            return false;
        }
        Matcher matcher = null;
        String group = "";

        return true;
    }


    @Override
    public String parseUglySql(String sql) {
        this.sql = sql;
        initSql();
        initSqlMode();
        int i = 1;
        int currentMode = uglySqlMode;
        while (currentMode > 0 && i <= currentMode) {
            boolean b = parseUgly(i);
            if (b) {
                currentMode -= i;
            }
            i <<= 1;
        }
        if (formattedSql.endsWith(",")) {
            formattedSql = formattedSql.substring(0, formattedSql.length() - 1);
        }
        return formattedSql;
    }
}
