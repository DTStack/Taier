package com.dtstack.taier.datasource.plugin.presto;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 * 用于 presto 数据源获取连接失败原因匹配
 *
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public class PrestoErrorPattern extends AbsErrorPattern {

    private static final Pattern MISSING_USERNAME = Pattern.compile("(?i)'user'\\s*is\\s*required");
    private static final Pattern JDBC_FORMAT_ERROR = Pattern.compile("(?i)'claims\\s*to\\s*not\\s*accept\\s*jdbcUrl");

    static {
        PATTERN_MAP.put(ConnErrorCode.MISSING_USERNAME.getCode(), MISSING_USERNAME);
        PATTERN_MAP.put(ConnErrorCode.JDBC_FORMAT_ERROR.getCode(), JDBC_FORMAT_ERROR);
    }
}
