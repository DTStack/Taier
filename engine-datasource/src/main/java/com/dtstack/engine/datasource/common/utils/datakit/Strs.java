package com.dtstack.engine.datasource.common.utils.datakit;

import com.google.common.base.Joiner;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/18
 * @desc 字符串操作工具类
 */
public class Strs {

    public static final char BLANK = ' ';
    public static final char DOUBLE_QUOTES = '"';
    public static final char SINGLE_QUOTES = '\'';
    public static final String POINT = ".";
    public static final String OBLIQUE_QUOTES = "`";

    private Strs() {
    }

    /**
     * 拼接"."
     *
     * @param vars 多个待拼接参数
     * @return <e.g.>schema1.t_student.name</e.g.>
     */
    public static String joinPoint(Object... vars) {
        return Joiner.on(POINT).skipNulls().join(vars);
    }

    /**
     * 拼接"."
     *
     * @param vars 多个待拼接参数
     * @return <e.g.>schema1.t_student.name</e.g.>
     */
    public static StringBuilder joinPoint(StringBuilder sb, Object... vars) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        for (int i = 0; i < vars.length; i++) {
            if (i > 0) {
                sb.append(POINT);
            }
            sb.append(vars[i]);
        }
        return sb;
    }

}
