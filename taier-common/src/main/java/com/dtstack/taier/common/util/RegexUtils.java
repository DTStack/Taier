package com.dtstack.taier.common.util;

import java.util.regex.Pattern;

import static com.dtstack.taier.common.constant.PatternConstant.TENANT_NAME_REGEX;

/**
 * 正则表达式校验工具
 * Regular expression check utils
 *
 * @author bnyte
 * @date 2022/5/2 16:03
 */
public class RegexUtils {

    /**
     * 校验租户名称
     *
     * @param input 要检查的字符串
     *              string to check
     * @return TRUE: 校验通过, FALSE: 校验不通过
     * TRUE: verification passed, FALSE: verification failed
     */
    public static boolean tenantName(String input) {
        return matches(input, TENANT_NAME_REGEX);
    }

    /**
     * 正则表达式公共方法
     * regular expression public methods
     *
     * @param input 要检查的字符串
     *              string to check
     * @param regex 正则表达式
     *              regular expression
     * @return TRUE: 校验通过, FALSE: 校验不通过
     * TRUE: verification passed, FALSE: verification failed
     */
    private static boolean matches(String input, String regex) {
        return Pattern.matches(regex, input);
    }

    /**
     * 判断是否是否是查询语句
     * show tables 、explain 语句、select 语句、desc formatted 语句
     *
     * @param sql
     * @return
     */
    public static boolean isQuery(String sql) {
        return SqlRegexUtil.isShowSql(sql)
                || SqlRegexUtil.isExplainSql(sql)
                || SqlRegexUtil.isSelect(sql)
                || SqlRegexUtil.isDescSql(sql);
    }

}
