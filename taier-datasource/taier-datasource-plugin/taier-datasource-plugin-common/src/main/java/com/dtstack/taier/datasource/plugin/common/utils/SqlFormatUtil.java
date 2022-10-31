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

package com.dtstack.taier.datasource.plugin.common.utils;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class SqlFormatUtil {
    private String sql;

    /**
     * 初始化
     *
     * @param sql
     * @return
     */
    public static SqlFormatUtil init(String sql) {
        SqlFormatUtil util = new SqlFormatUtil();
        util.sql = sql;
        return util;
    }

    /**
     * 格式化 SQL
     *
     * @param sql
     * @return
     */
    public static String formatSql(String sql) {
        return init(sql).removeComment().toOneLine().removeBlank().removeEndChar().getSql();
    }

    /**
     * 去除换行符，变为一行
     *
     * @return
     */
    public SqlFormatUtil toOneLine() {
        this.sql = this.sql.replaceAll("\r", " ").replaceAll("\n", " ");
        return this;
    }

    /**
     * 去除末尾分号
     *
     * @return
     */
    public SqlFormatUtil removeEndChar() {
        this.sql = this.sql.trim();
        if (this.sql.endsWith(";")) {
            this.sql = this.sql.substring(0, this.sql.length() - 1);
        }

        return this;
    }

    /**
     * 去除注释
     *
     * @return
     */
    public SqlFormatUtil removeComment() {
        this.sql = this.sql.replaceAll("--.*", "");
        this.sql = this.sql.replaceAll("\\/\\*\\*+.*\\*\\*+\\/", "");
        this.sql = this.sql.replaceAll("/\\*{1,2}\\*/", "");
        this.sql = this.sql.replaceAll("/\\*{1,2}[\\s\\S]*?\\*/", "");
        return this;
    }

    /**
     * 去除空格
     *
     * @return
     */
    public SqlFormatUtil removeBlank() {
        this.sql = this.sql.trim();
        return this;
    }

    /**
     * 获取 SQL
     *
     * @return
     */
    public String getSql() {
        return this.sql;
    }

    /**
     * 根据指定分隔符分割字符串---忽略在引号里面的分隔符
     *
     * @param sqls
     * @param delimiter
     * @return
     */
    public static List<String> splitIgnoreQuota(String sqls, char delimiter){
        List<String> tokensList = new ArrayList<>();
        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        StringBuilder b = new StringBuilder();
        char[] chars = sqls.toCharArray();
        int idx = 0;
        for (char c : chars) {
            // 是否转义, 忽略 \\, 例如: 'escapeChar' = '\\'
            boolean isEscape = false;
            if (idx > 0) {
                isEscape = '\\' == chars[idx - 1];
                if (idx > 1) {
                    isEscape = isEscape && '\\' != chars[idx - 2];
                }
            }
            if(c == delimiter){
                if (inQuotes) {
                    b.append(c);
                } else if(inSingleQuotes){
                    b.append(c);
                }else {
                    if (StringUtils.isNotBlank(b)){
                        tokensList.add(b.toString());
                        b = new StringBuilder();
                    }
                }
            }else if(c == '\"' && !isEscape){
                inQuotes = !inQuotes;
                b.append(c);
            }else if(c == '\'' && !isEscape && !inQuotes){
                inSingleQuotes = !inSingleQuotes;
                b.append(c);
            }else{
                b.append(c);
            }
            idx++;
        }

        if (StringUtils.isNotBlank(b)){
            tokensList.add(b.toString());
        }

        return tokensList;
    }
}
