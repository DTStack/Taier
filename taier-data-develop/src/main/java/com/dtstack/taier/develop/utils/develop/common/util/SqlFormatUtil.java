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

package com.dtstack.taier.develop.utils.develop.common.util;

import com.dtstack.taier.common.util.Base64Util;
import com.dtstack.taier.common.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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


    private static final String RETURN_REGEX = "\r";

    private static final String NEWLINE_REGEX = "\n";

    private static final String COMMENTS_REGEX = "--.*";

    public static final String EXTERNAL = "external";

    public static final String EXTERNAL_X = "external_x";

    private static final String COMMENTS_REGEX_LIBRA = "\\/\\*\\*+.*\\*\\*+\\/";

    private static final String COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS = "/\\*{1,2}\\*/";

    private static final String COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS2 = "/\\*{1,2}[\\s\\S]*?\\*/";

    private static final String LIFECYCLE_REGEX = "(?i)lifecycle\\s+(?<lifecycle>[1-9]\\d*)";

    private static final String CATALOGUE_REGEX = "(?i)catalogue\\s+(?<catalogue>[1-9]\\d*)";

    private static final String STORE_REGEX = "(?i)store\\s+(?<store>[a-Z]\\d*)";

    public static Pattern selectStarPattern = Pattern.compile("(?i)select\\s+\\*");

    private static final String CREATE_REGEX = "(?i)create\\s+(external|temporary)*\\s*table\\s+[\\W\\w]+";

    private static final String DDL_REGEX = "(?i)(insert|create|drop|alter|truncate|set|update|delete)+\\s+[\\W\\w]+";

    private static final String SQL_SPLIT_REGEX = "('[^']*?')|(\"[^\"]*?\")";

    public static final String SPLIT_CHAR = ";";

    private static final String MULTIPLE_BLANKS = "(?i)\\s\\s+";

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


    public static List<String> splitSqlWithoutSemi(String sqlText) {
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }
        return splitSqlText(sqlText);
    }


    public static List<String> splitSqlText(String sqlText) {
        String sqlTemp = sqlText;
        Pattern pattern = Pattern.compile(SQL_SPLIT_REGEX);
        Matcher matcher = pattern.matcher(sqlTemp);
        while (matcher.find()) {
            String group = matcher.group();
            sqlTemp = sqlTemp.replace(group, StringUtils.repeat(" ", group.length()));
        }

        List<Integer> posits = new ArrayList<>();
        while (sqlTemp.contains(SPLIT_CHAR)) {
            int pos = sqlTemp.indexOf(SPLIT_CHAR);
            posits.add(pos);
            sqlTemp = sqlTemp.substring(pos + 1);
        }

        List<String> sqls = new ArrayList<>();
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

    public static boolean checkSelectStar(String sql) {

        return selectStarPattern.matcher(sql).find();
    }

    /**
     * 标准化sql
     */
    public static String getStandardSql(String sql) {
        return SqlFormatUtil.init(sql)
                .removeCatalogue()
                .removeLifecycle()
                .removeBlanks()
                .getSql();
    }

    public static String replaceKeyWord(String sql) {

        return sql.replaceAll(EXTERNAL, EXTERNAL_X);
    }

    public SqlFormatUtil removeBlanks() {
        sql = sql.replaceAll(MULTIPLE_BLANKS, " ");
        return this;
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
     * 去除注释 -- 开头的sql
     */
    public SqlFormatUtil removeComment() {
        sql = removeCommentByQuotes(sql);
        sql = sql.replaceAll(COMMENTS_REGEX_LIBRA, StringUtils.EMPTY);
        sql = sql.replaceAll(COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS, StringUtils.EMPTY);
        sql = sql.replaceAll(COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS2, StringUtils.EMPTY);
        return this;
    }

    /**
     * 去除 --注释  避免了" '的影响
     *
     * @param osql
     * @return
     */
    private static String removeCommentByQuotes(String osql) {
        String[] sqls = osql.split("\n");
        StringBuffer buffer = new StringBuffer();
        //修复-- 注释在sql中间的情况
        for (int j = 0; j < sqls.length; j++) {
            String sqlLine = sqls[j];
            Boolean flag = false;
            char[] sqlindex = sqlLine.toCharArray();
            for (int i = 0; sqlLine.length() > i; i++) {
                if (!flag) {
                    if (sqlindex[i] == '\"' || sqlindex[i] == '\'') {
                        flag = true;
                    } else {
                        if (sqlindex[i] == '-' && i + 1 < sqlLine.length() && sqlindex[i + 1] == '-') {
                            break;
                        }
                    }
                    buffer.append(sqlindex[i]);
                } else {
                    if (sqlindex[i] == '\"' || sqlindex[i] == '\'') {
                        flag = false;
                    }
                    buffer.append(sqlindex[i]);
                }
            }
            buffer.append("\n");
        }
        return buffer.toString();

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
     *
     * @param sql
     * @return
     */
    public static String formatType(String sql) {

        if (StringUtils.isBlank(sql)) {
            return sql;
        }
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
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
                    if (stack.isEmpty()) {
                        isBegin = false;
                    }
                }
                if (!isBegin && stack.isEmpty() && !">".equals(value)) {
                    stringBuilder.append(value);
                }
            }
            if (i != s.length - 1 && !isBegin && stack.isEmpty()) {
                stringBuilder.append(" string ");
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 向后读一个token
     *
     * @param originSql
     * @param start
     * @return
     */
    public static String readTokenBackwards(String originSql, int start) {
        Deque<Character> charStack = new LinkedList<>();
        for (int i = start; i < originSql.length(); i++) {
            char c = originSql.charAt(i);
            if (!charStack.isEmpty()) {
                //栈不为空判断栈顶元素，如果是结束标志，则结束，否则压栈
                Character peek = charStack.peek();
                if (isStopSignal(peek)) {
                    charStack.pop();
                    break;
                }
                charStack.push(c);
            } else {
                //栈为空则直接压栈非空元素
                if (' ' == c) {
                    continue;
                }
                charStack.push(c);
            }
        }
        StringBuilder builder = new StringBuilder();
        while (!charStack.isEmpty()) {
            Character pop = charStack.removeLast();
            builder.append(pop);
        }
        return builder.toString();
    }

    private static boolean isStopSignal(char c) {
        if (' ' == c || ',' == c || '(' == c || ')' == c) {
            return true;
        }
        return false;
    }


    private static final Logger logger = LoggerFactory.getLogger(SqlFormatUtil.class);
    private static final Pattern note_pattern = Pattern.compile("--.*\n|/\\*\\*[\\s\\S]*\\*/");
    private static final Pattern note_pattern_new = Pattern.compile("##.*\n");

    public static String dealAnnotationBefore(String sql) {
        sql = sql + "\n";
        for (Matcher matcher = note_pattern.matcher(sql);
             matcher.find(); matcher = note_pattern.matcher(sql)) {
            sql = matcher.replaceFirst("##" +
                    Base64Util.baseEncode(matcher.group()) + "\n");
        }
        return sql;
    }

    public static String dealAnnotationAfter(String sql) {
        for (Matcher matcher = note_pattern_new.matcher(sql);
             matcher.find(); matcher = note_pattern_new.matcher(sql)) {
            String group = matcher.group();
            if (group.endsWith("\n")) {
                group = group.substring(2, group.length() - 1);
            }
            String s = group;
            try {
                s = group;
            } catch (IllegalArgumentException var5) {
                logger.warn("baseEncode failed, sql={}, e={}", sql, var5);
            }
            s = s.replaceAll("\\$", "RDS_CHAR_DOLLAR");
            sql = matcher.replaceFirst(s);
            sql = sql.replaceAll("RDS_CHAR_DOLLAR", "\\$");
        }
        return sql;
    }


}
