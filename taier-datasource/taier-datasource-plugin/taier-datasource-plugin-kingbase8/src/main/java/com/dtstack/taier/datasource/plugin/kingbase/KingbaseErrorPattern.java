package com.dtstack.taier.datasource.plugin.kingbase;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public class KingbaseErrorPattern extends AbsErrorPattern {

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)authentication\\s*failed");
    private static final Pattern DB_NOT_EXISTS = Pattern.compile("(?i)database.*does\\s*not\\s*exist");
    private static final Pattern JDBC_FORMAT_ERROR = Pattern.compile("(?i)claims\\s*to\\s*not\\s*accept\\s*jdbcUrl");
    private static final Pattern CANNOT_ACQUIRE_CONNECT = Pattern.compile("(?i)could\\s*not\\s*establish\\s*the\\s*connection");

    static {
        PATTERN_MAP.put(ConnErrorCode.USERNAME_PASSWORD_ERROR.getCode(), USERNAME_PASSWORD_ERROR);
        PATTERN_MAP.put(ConnErrorCode.DB_NOT_EXISTS.getCode(), DB_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.JDBC_FORMAT_ERROR.getCode(), JDBC_FORMAT_ERROR);
        PATTERN_MAP.put(ConnErrorCode.CANNOT_ACQUIRE_CONNECT.getCode(), CANNOT_ACQUIRE_CONNECT);
    }
}
