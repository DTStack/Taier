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

package com.dtstack.taier.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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

    private static final String COMMENTS_REGEX_LIBRA = "\\/\\*\\*+.*\\*\\*+\\/";

    private static final String COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS = "/\\*{1,2}\\*/";

    private static final String COMMENTS_REGEX_LIBRA_SINGLE_QUOTATION_MARKS2 = "/\\*{1,2}[\\s\\S]*?\\*/";

    private static final String LIFECYCLE_REGEX = "(?i)lifecycle\\s+(?<lifecycle>[1-9]\\d*)";

    private static final String CATALOGUE_REGEX = "(?i)catalogue\\s+(?<catalogue>[1-9]\\d*)";

    private static final String CREATE_REGEX = "(?i)create\\s+(external|temporary)*\\s*table\\s+[\\W\\w]+";

    private static final String SQL_SPLIT_REGEX = "('[^']*?')|(\"[^\"]*?\")";

    public static final String SPLIT_CHAR = ";";

    private static final String MULTIPLE_BLANKS = "(?i)\\s\\s+";

    private SqlFormatUtil() {
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
                .toOneLine()
                .getSql();
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


    public SqlFormatUtil removeBlanks() {
        sql = sql.replaceAll(MULTIPLE_BLANKS, " ");
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


    public static Pattern pattern = Pattern.compile("(?i)(map|struct|array)<");


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
