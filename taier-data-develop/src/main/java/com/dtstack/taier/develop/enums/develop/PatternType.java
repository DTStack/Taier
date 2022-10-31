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

package com.dtstack.taier.develop.enums.develop;

import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：WangChuan
 * @Date ：Created in 下午1:16 2020/5/26
 * @Description：
 */
public enum PatternType {

    /**
     * jdbc 正则
     */
    JDBC_PATTERN(Pattern.compile("(?i)jdbc:[a-zA-Z0-9\\.]+://(?<host>[0-9a-zA-Z\\.-]+)(:(?<port>\\d+)|)/(?<db>[0-9a-zA-Z_%\\.]+)(?<param>[\\?;#].*)*")),

    JDBC_IPV6_PATTERN(Pattern.compile("(?i)jdbc:[a-zA-Z0-9\\.]+://address=\\(protocol=tcp\\)\\(host=(?<host>[0-9a-zA-Z\\:]+)\\)(\\(port=(?<port>\\d+)\\)|)/(?<db>[0-9a-zA-Z_%\\.]+)(?<param>[\\?;#].*)*")),
    /**
     * 特殊表名 正则
     * example: s_-$#.s_-
     */
    SPECIAL_TABLE_PATTERN(Pattern.compile("(?i)[a-zA-Z0-9_\\-\\$#]+\\.[a-zA-Z0-9_\\-\\$#]+")),

    /**
     * mongodb系统表名正则
     */
    MONGODB_SYS_TABLE_REGEX(Pattern.compile("^system")),

    /**
     * mysql jdbc正则
     */
    MYSQL_PATTERN(Pattern.compile("jdbc:mysql://(.+):(\\d+)/(\\w+)(^\\?.*)?")),

    /**
     * 函数正则
     */
    FUNC_PATTERN(Pattern.compile("(?i)(.+)\\s+AS\\s+(.+)")),

    /**
     * hbase 字段正则
     */
    HBASE_COLUMN_PATTERN(Pattern.compile("(?i)\\s*([a-zA-Z0-9_]+):([a-zA-Z0-9_]+)\\s+([a-zA-Z0-9_]+)\\s+AS\\s+([a-zA-Z0-9_]+)")),

    /**
     * 忽略引号和括号
     */
    FUNC_SPLIT_REGEX_IGNORE_BRACKETS(Pattern.compile("(?i)(.+)\\s+AS\\s+(.+)(?![^()]*+\\))(?![^{}]*+})(?![^\\[\\]]*+\\])(?=(?:[^\"]|\"[^\"]*\")*$)")),

    /**
     * 建表语句正则
     */
    CREATE_TABLE_PATTERN(Pattern.compile("(?i)create\\s*(table|view)+", Pattern.CASE_INSENSITIVE)),

    /**
     * 建表语句正则
     */
    MATCH_CREATE_TABLE_PATTERN(Pattern.compile("(?i)create\\s*(table|view)+\\s*(?<tablename>[0-9a-zA-Z_%\\.]+)\\s*", Pattern.CASE_INSENSITIVE)),

    /**
     * 数字正则
     */
    NUM_PATTERN(Pattern.compile("^[0-9]*$")),

    /**
     * 函数正则
     */
    FUNCTION_PATTERN(Pattern.compile("\\s*([0-9a-zA-Z-_]+)\\s*\\("));

    /**
     * 正则表达式
     */
    private Pattern val;

    PatternType(Pattern val) {
        this.val = val;
    }

    public Pattern getVal() {
        return val;
    }
}
