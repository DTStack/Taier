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

package com.dtstack.taier.develop.sql.utils;

import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.develop.sql.hive.ASTNodeUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql格式化工具类
 *
 * @author jiangbo
 * @date 2018/5/25 16:23
 */
public class SqlFormatUtil {

    private String sql;

    private ParseDriver parseDriver = new ParseDriver();

    private static final String RETURN_REGEX = "\r";

    private static final String NEWLINE_REGEX = "\n";

    private static final String COMMENTS_REGEX = "--.*";

    private static final String COMMENTS_REGEX_LIBRA = "\\/\\*\\*+.*\\*\\*+\\/";

    private static final String COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS = "/\\*{1,2}\\*/";

    private static final String COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS2 = "/\\*{1,2}[\\s\\S]*?\\*/";

    private static final String LIFECYCLE_REGEX = "(?i)lifecycle\\s+(?<lifecycle>[1-9]\\d*)";

    private static final String CATALOGUE_REGEX = "(?i)catalogue\\s+(?<catalogue>[1-9]\\d*)";

    private static final String STORE_REGEX = "(?i)store\\s+(?<store>[a-Z]\\d*)";

    private static final String EMPTY_QUOTATION = "''";

    private static final String CREATE_REGEX = "(?i)create\\s+(external|temporary)*\\s*table\\s+[\\W\\w]+";

    private static final String DDL_REGEX = "(?i)(insert|create|drop|alter|truncate|set|update|delete)+\\s+[\\W\\w]+";

    private static final String SQL_SPLIT_REGEX = "('[^']*?')|(\"[^\"]*?\")";

    public static final String SPLIT_CHAR = ";";

    private SqlFormatUtil() {
    }

    /**
     * 是否为ddl语句
     */
    public static boolean isDDLSql(String sql) {
        return sql.matches(DDL_REGEX);
    }

    /**
     * 是否为建表语句
     */
    public static boolean isCreateSql(String sql) {
        return sql.matches(CREATE_REGEX);
    }

    public static List<String> splitSqlText(String sqlText) {
        String sqlTemp = sqlText;
        Pattern pattern = Pattern.compile(SQL_SPLIT_REGEX);
        Matcher matcher = pattern.matcher(sqlTemp);
        while (matcher.find()) {
            String group = matcher.group();
            sqlTemp = sqlTemp.replace(group, StringUtils.repeat(" ", group.length()));
        }

        List<Integer> posits = Lists.newArrayList();
        while (sqlTemp.contains(SPLIT_CHAR)) {
            int pos = sqlTemp.indexOf(SPLIT_CHAR);
            posits.add(pos);
            sqlTemp = sqlTemp.substring(pos + 1);
        }

        List<String> sqls = Lists.newArrayList();
        for (Integer posit : posits) {
            sqls.add(sqlText.substring(0, posit));
            sqlText = sqlText.substring(posit + 1);
        }

        return sqls;
    }

    public static void checkSql(String sql) throws Exception {
        if (StringUtils.isEmpty(sql)) {
            throw new IllegalArgumentException("sql语句不能为空");
        }

        String sqlCopy = sql;
        sqlCopy = SqlFormatUtil.init(sqlCopy).getSql();
        String[] strings = Strings.splitIgnoreQuotaBrackets(sqlCopy, ";");
        if (strings.length > 2) {
            throw new IllegalArgumentException("只能执行单条sql");
        }
    }

    public static SqlFormatUtil init(String sql) {
        SqlFormatUtil util = new SqlFormatUtil();
        util.sql = sql;
        return util;
    }

    public static String formatSql(String sql) {
        return SqlFormatUtil.init(sql)
                .removeComment()
                .toOneLine()
                .removeBlank()
                .removeEndChar()
                .getSql();
    }

    /**
     * 标准化sql
     */
    public static String getStandardSql(String sql) {
        return SqlFormatUtil.init(sql)
                .removeCatalogue()
                .removeLifecycle()
                .getSql();
    }

    public SqlFormatUtil toSingleSql() {
        if (sql.contains(SPLIT_CHAR)) {
            sql = sql.split(SPLIT_CHAR)[0];
        }
        return this;
    }

    public SqlFormatUtil toOneLine() {
        sql = sql.replaceAll(RETURN_REGEX, " ").replaceAll(NEWLINE_REGEX, " ");
        return this;
    }

    public SqlFormatUtil removeEndChar() {
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        return this;
    }

    /**
     * 去除引号内的内容
     */
    public SqlFormatUtil removeQuotation() throws Exception {
        ASTNode root = parseDriver.parse(sql);
        List<String> values = ASTNodeUtil.getQuotationValue(ASTNodeUtil.findRootNonNullToken(root));
        if (CollectionUtils.isNotEmpty(values)) {
            for (String value : values) {
                sql = sql.replace(value, EMPTY_QUOTATION);
            }
        }
        return this;
    }

    /**
     * 去除注释 -- 开头的sql
     */
    public SqlFormatUtil removeComment() {
        sql = sql.replaceAll(COMMENTS_REGEX, StringUtils.EMPTY);
        sql = sql.replaceAll(COMMENTS_REGEX_LIBRA, StringUtils.EMPTY);
        sql = sql.replaceAll(COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS, StringUtils.EMPTY);
        sql = sql.replaceAll(COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS2, StringUtils.EMPTY);
        return this;
    }

    public SqlFormatUtil removeCatalogue() {
        sql = sql.replaceAll(CATALOGUE_REGEX, StringUtils.EMPTY);
        return this;
    }

    public SqlFormatUtil removeLifecycle() {
        sql = sql.replaceAll(LIFECYCLE_REGEX, StringUtils.EMPTY);
        return this;
    }

    public SqlFormatUtil removeBlank() {
        sql = sql.trim();
        return this;
    }

    public String getSql() {
        return sql;
    }

    public static String removeLimit(String sql) {
        if (StringUtils.isBlank(sql)) {
            return sql;
        }
        String formattedSql = sql;
        Pattern compile = Pattern.compile(SqlRegexUtil.LIMIT);
        Matcher matcher = compile.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group(0);
            formattedSql = sql.replaceAll(group, " ");
        }
        return formattedSql;
    }

    /**
     * 删除'' 内容
     *
     * @param sql
     * @return
     */
    public static String removeComment(String sql) {
        return StringUtils.isBlank(sql) ? "" : sql.replaceAll("(?i)comment\\s*'([^']*)'", StringUtils.EMPTY);
    }


    /**
     * 删除 comment ""
     *
     * @param sql
     * @return
     */
    public static String removeDoubleQuotesComment(String sql) {
        return StringUtils.isBlank(sql) ? "" : sql.replaceAll("(?i)comment\\s*\"([^\"]*)\"", StringUtils.EMPTY);
    }

    public static Pattern pattern = Pattern.compile("(?i)(map|struct|array)<");


    /**
     * 处理 array<struct<room_id:string,days:array<struct<day_id:string,price:int>>>> 格式
     * @param sql
     * @return
     */
    public static String formatType(String sql) {

        if (StringUtils.isBlank(sql)) {
            return sql;
        }
        Matcher matcher = pattern.matcher(sql);
        if(!matcher.find()){
            return sql;
        }
        String replace_type = sql.replaceAll("(?i)(map|struct|array)\\s*<", "replace_type<");
        Stack<String> stack = new Stack<>();
        String[] s = replace_type.split("replace_type");
        StringBuilder stringBuilder = new StringBuilder();
        boolean isBegin = false;
        for (int i = 0; i < s.length; i++) {
            String data = s[i];
            if (data.contains("replace_type")) {
                isBegin = true;
            }
            for (char c : data.toCharArray()) {
                String value = String.valueOf(c);
                if ("<".equalsIgnoreCase(value)) {
                    stack.push(value);
                } else if (">".equals(value)) {
                    stack.pop();
                    if(stack.isEmpty()){
                        isBegin = false;
                    }
                }
                if (!isBegin && stack.isEmpty() && !">".equals(value)) {
                    stringBuilder.append(value);
                }
            }
           if(i != s.length -1 && !isBegin && stack.isEmpty() ){
               stringBuilder.append(" string ");
           }
        }
        return stringBuilder.toString();
    }

    public static List<String> splitSqlWithoutSemi(String sqlText) {
        if(StringUtils.isEmpty(sqlText)){
            return ListUtils.EMPTY_LIST;
        }
        String sql = sqlText.trim();
        if(!sql.endsWith(";")){
            sql = sql + ";";
        }
        return splitSqlText(sql);
    }

}
