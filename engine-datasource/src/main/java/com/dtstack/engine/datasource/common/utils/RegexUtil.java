package com.dtstack.engine.datasource.common.utils;

import java.util.regex.Pattern;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/3
 * @desc 正则工具类
 */
public class RegexUtil {

    /**
     * 模型英文名称
     * 只能包含 字母|数字|下划线
     */
    public static final String MODEL_EN_NAME_REGEX = "[a-zA-Z0-9_]*$";
    /**
     * 模型名称
     * 只能包含 汉字|字母|数字|下划线
     */
    public static final String MODEL_NAME_REGEX = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
    /**
     * 表别名
     * 只能包含 字母|数字|下划线 同时必须包含字母
     */
    public static final String TABLE_ALIAS_REGEX = "^(?=.*?[a-zA-Z])[a-zA-Z0-9_]*$";


    /**
     * 匹配函数
     *
     * @param regex 正则表达式
     * @param input 输入参数
     * @return true符合 false不符合
     */
    public static boolean match(String regex, CharSequence input) {
        return Pattern
                .compile(regex)
                .matcher(input)
                .matches();
    }
}
