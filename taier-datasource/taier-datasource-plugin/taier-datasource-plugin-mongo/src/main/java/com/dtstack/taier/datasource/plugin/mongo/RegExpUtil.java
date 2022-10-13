package com.dtstack.taier.datasource.plugin.mongo;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: data-api-gateway
 * @description:
 * @author: 遥远
 * @create: 2020-05-14 21:06
 */
public class RegExpUtil {

    static String collectionRegExp = "getCollection\\('(\\w+?)'\\)";
    static String findRegExp = "\\.(find|findOne|aggregate|count|countDocuments|distinct)\\(([\\w\\W]*?)\\)[;|.]";
    static String skipRegExp = "\\.skip\\(([\\w\\W].*?)\\)[;|.]";
    static String limitRegExp = "\\.limit\\(([\\w\\W].*?)\\)[;|.]";
    static String batchSizeRegExp = "\\.batchSize\\(([\\w\\W].*?)\\)[;|.]";
    static String sortRegExp = "\\.sort\\(([\\w\\W].*?)\\)[;|.]";
    static String countRegExp = "\\.count\\(.*\\)[;|.]";
    static String createCollectionRegExp = "db\\.createCollection\\(('\\w+?',\\{.+?\\}|'\\w+?')\\)[;]";

    /**
     * 传入Mongo建表语句获取还未创建的collection名称
     * tips: 表名称支持英文字符、数字、下划线，options条件支持任意字符
     *
     * @param sql
     * @return
     */
    public static String getUnsetCName(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        return getWithRegExp(sql, createCollectionRegExp);
    }

    public static String getCollectionName(String sql) {
        return getWithRegExp(sql, collectionRegExp);
    }

    public static String getQuery(String sql) {
        Pattern r = Pattern.compile(findRegExp);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(sql);
        if (m.find()) {
            return m.group(2);
        }
        return null;
    }

    public static String getSkip(String sql) {
        return getWithRegExp(sql, skipRegExp);
    }

    public static String getLimit(String sql) {
        return getWithRegExp(sql, limitRegExp);
    }

    public static String getBatchSize(String sql) {
        return getWithRegExp(sql, batchSizeRegExp);
    }

    public static String getSort(String sql) {
        return getWithRegExp(sql, sortRegExp);
    }

    public static Boolean isCount(String sql) {
        Pattern r = Pattern.compile(countRegExp);
        Matcher m = r.matcher(sql);
        return m.find();
    }

    private static String getWithRegExp(String s, String regExp) {
        Pattern r = Pattern.compile(regExp);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(s);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private static List<String> getWithRegExps(String s, String regExp) {
        List<String> ss = new ArrayList<String>();
        Pattern r = Pattern.compile(regExp);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(s);
        while (m.find()) {
            ss.add(m.group(1));
        }

        return ss;
    }

    /**
     * 压缩、" -> '
     *
     * @param str
     * @return
     */
    public static String transferred(String str) {
        str = StringEscapeUtils.unescapeJava(str);
        str = str.replaceAll("\r|\n| ", "");
        str = str.trim();
        return str;
    }
}
