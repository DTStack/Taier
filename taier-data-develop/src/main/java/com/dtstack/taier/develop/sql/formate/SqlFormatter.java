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

package com.dtstack.taier.develop.sql.formate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格式化sql语句注意需要区分create table 语句和其他
 * Date: 2018/7/6
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class SqlFormatter {

    private static final Logger logger = LoggerFactory.getLogger(SqlFormatter.class);

    private static final String CRLF = System.getProperty("line.separator", "\n");

    private static final String SQL_DELIMITER = ";";

    private static Formatter ddlFormat = new DDLFormatterImpl();

    private static Formatter basicFormat = new BasicFormatterImpl();

    public static Pattern createTablePattern = Pattern.compile("(?i)[0-1\\s]*create(\\s+|\\s+[0-1]*\\s+)table");

    public static Pattern alterTablePattern = Pattern.compile("(?i)[0-1\\s]*alter(\\s+|\\s+[0-1]*\\s+)table");

    public static Pattern commentOnPattern = Pattern.compile("(?i)[0-1\\s]*comment(\\s+|\\s+[0-1]*\\s+)on");

    private static final String NOTE = "--.*\n|/\\*\\*[\\s\\S]*\\*/";

    public static Pattern notAnnotation = Pattern.compile("'+.*(--)+.*'+");

    private static final Pattern note_pattern = Pattern.compile(NOTE);

    private static final String NEW_NOTE = "##.*\n";

    private static final Pattern note_pattern_new = Pattern.compile(NEW_NOTE);

    private static final Pattern delimiter_pattern = Pattern.compile(SQL_DELIMITER);

    private static final Pattern from_pattern = Pattern.compile("\n\\s*\n");

    private static final String WHITE_SPACE = " ";

    public static final String RDOSFORMAT_BINARY = SqlFormatter.toBinary("RDOSFORMAT");

    private static final String annotate_STR = WHITE_SPACE + RDOSFORMAT_BINARY + WHITE_SPACE;

    private static final String ANNOTATE_LIST = "annotateList";

    private static final String SQL = "sql";

    private static final String UUID_STR = UUID.randomUUID().toString();

    public static String format(String sql) throws Exception {
        Map<String, Object> map = addSplitWithNote(sql);
        sql = (String) map.get(SQL);
        List<String> annotateList = (List<String>) map.get(ANNOTATE_LIST);

        String format = formatSql(sql);

        //替换为注释语句
        for (int j = 0; j < annotateList.size(); j++) {
            format = format.replaceFirst(annotate_STR.trim(), "\n" + annotateList.get(j) + "\n");
        }
        //删除无用换行
        format = format.trim();
        Matcher matcher = from_pattern.matcher(format);
        while (matcher.find()) {
            format = matcher.replaceAll("\n");
        }
        return format.replaceAll(";", ";\n");
    }

    /**
     * 处理sqlText里的注释，先base64编码
     *
     * @param sql
     * @return
     */
    public static String dealAnnotationBefore(String sql) {
        //兼容注释regex
        sql = sql + "\n";
        Matcher matcher = note_pattern.matcher(sql);
        while (matcher.find()) {
            sql = matcher.replaceFirst("##" + matcher.group() + "\n");
            matcher = note_pattern.matcher(sql);
        }
        return sql;
    }

    /**
     * 处理sqlText里的注释，后base64解码
     *
     * @param sql
     * @return
     */
    public static String dealAnnotationAfter(String sql) {
        Matcher matcher = note_pattern_new.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group();
            if (group.endsWith("\n")) {
                group = group.substring(2, group.length() - 1);
            }
            String s = group;
            try {
                s = group;
            } catch (IllegalArgumentException e) {
                logger.warn("baseEncode failed, sql={}, e={}", sql, e);
            }
            //替换 $ 符号
            s = s.replaceAll("\\$", "RDS_CHAR_DOLLAR");
            sql = matcher.replaceFirst(s);
            sql = sql.replaceAll("RDS_CHAR_DOLLAR", "\\$");
            matcher = note_pattern_new.matcher(sql);
        }
        return sql;
    }

    /**
     * 提取出注释语句
     *
     * @param sql
     * @return
     */
    private static Map<String, Object> addSplitWithNote(String sql) {
        //兼容注释regex
        sql = sql + "\n";
        List<String> annotateList = new ArrayList<>();
        Matcher matcher = note_pattern.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group().trim();
            if (group.endsWith("\n")) {
                group = group.substring(0, group.length() - 2);
            }
            annotateList.add(group);
            sql = matcher.replaceFirst(annotate_STR);
            matcher = note_pattern.matcher(sql);
        }

        Map<String, Object> result = new HashMap<>(2);
        result.put(ANNOTATE_LIST, annotateList);
        result.put(SQL, sql);
        return result;
    }


    public static String formatSql(String sql) {
        String[] arrSql = DtStringUtil.splitIgnoreQuotaNotUsingRegex(sql, SQL_DELIMITER);
        StringBuilder sb = new StringBuilder();

        int index = 0;
        for (String tmpSql : arrSql) {
            tmpSql = tmpSql.trim();
            if (Strings.isNullOrEmpty(tmpSql)) {
                continue;
            }

            if (checkIsCreateTable(tmpSql)) {
                sb.append(ddlFormat.format(tmpSql));
            } else {
                sb.append(basicFormat.format(tmpSql));
            }
            if (index < arrSql.length) {
                sb.append(SQL_DELIMITER);
            }
            sb.append("\n");
            index++;
        }

        return sb.toString();
    }

    /**
     * 格式化sql
     */
    public static String sqlFormat(String sql) {
        if (!com.google.common.base.Strings.isNullOrEmpty(sql)) {
            boolean isJSON;
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) JSONObject.parse(sql);
                isJSON = true;
            } catch (Exception e) {
                isJSON = false;
            }
            try {
                if (isJSON) {
                    sql = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                            SerializerFeature.WriteDateUseDateFormat);
                    return sql;

                } else {
                    return SqlFormatter.format(sql);
                }

            } catch (Exception e) {
                logger.error("failure to format sql, e:{}", e.getMessage(), e);
            }
        }

        return sql;
    }


    /**
     * 去除注释，并排除''中有--被当成注释的情况
     *
     * @param sql
     * @return
     */
    public static String removeAnnotation(String sql) {
        sql = replaceNotAnnotation(sql);
        sql = sql.replaceAll("(--)+(.)*\\n", "").replaceAll("\\s", " ").replaceAll("\\n", " ").replace(UUID_STR, "--");
        return sql;
    }


    /**
     * 替换非注释的--为uuid，避免被当成注释处理 如   'to--'
     *
     * @param sql
     * @return
     */
    public static String replaceNotAnnotation(String sql) {
        Matcher matcher = notAnnotation.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group();
            String groupMod = group.replace("--", UUID_STR);
            sql = sql.replace(group, groupMod);
        }
        return sql;
    }

    private static boolean checkIsCreateTable(String sql) {
        if (createTablePattern.matcher(sql).find()
                || alterTablePattern.matcher(sql).find()
                || commentOnPattern.matcher(sql).find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取所有分号数量
     *
     * @param sql
     * @return
     */
    private static int getDelimiterCount(String sql) {
        Matcher matcher = delimiter_pattern.matcher(sql);
        int allDelimiter = 0;
        while (matcher.find()) {
            allDelimiter++;
        }
        return allDelimiter;
    }


    public static int[] BinstrToIntArray(String binStr) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;
        }
        return result;
    }

    //将二进制转换成字符
    public static char BinstrToChar(String binStr) {
        int[] temp = BinstrToIntArray(binStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }

    public static String toBinary(String str) {
        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            result += Integer.toBinaryString(strChar[i]);
        }
        return result;
    }
}
