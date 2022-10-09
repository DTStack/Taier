package com.dtstack.taier.datasource.plugin.impala;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 *
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public class ImpalaErrorPattern extends AbsErrorPattern {

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)Authentication\\s*failed");
    private static final Pattern MISSING_USERNAME_OR_PASSWORD = Pattern.compile("(?i)Required\\s*Connection\\s*Key");
    private static final Pattern CANNOT_ACQUIRE_CONNECT = Pattern.compile("(?i)Connection\\s*refused");
    private static final Pattern JDBC_FORMAT_ERROR = Pattern.compile("(?i)claims\\s*to\\s*not\\s*accept\\s*jdbcUrl");
    static {
        PATTERN_MAP.put(ConnErrorCode.USERNAME_PASSWORD_ERROR.getCode(), USERNAME_PASSWORD_ERROR);
        PATTERN_MAP.put(ConnErrorCode.MISSING_USERNAME_OR_PASSWORD.getCode(), MISSING_USERNAME_OR_PASSWORD);
        PATTERN_MAP.put(ConnErrorCode.CANNOT_ACQUIRE_CONNECT.getCode(), CANNOT_ACQUIRE_CONNECT);
        PATTERN_MAP.put(ConnErrorCode.JDBC_FORMAT_ERROR.getCode(), JDBC_FORMAT_ERROR);
    }
}
