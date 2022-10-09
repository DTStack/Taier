package com.dtstack.taier.datasource.plugin.postgresql;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 *
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public class PostgreSqlErrorPattern extends AbsErrorPattern {

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)password\\s*authentication\\s*failed\\s*for\\s*user");
    private static final Pattern DB_NOT_EXISTS = Pattern.compile("(?i)database.*does\\s*not\\s*exist");
    private static final Pattern CANNOT_ACQUIRE_CONNECT = Pattern.compile("(?i)Connection\\s*refused");
    private static final Pattern JDBC_FORMAT_ERROR = Pattern.compile("(?i)claims\\s*to\\s*not\\s*accept\\s*jdbcUrl");
    static {
        PATTERN_MAP.put(ConnErrorCode.USERNAME_PASSWORD_ERROR.getCode(), USERNAME_PASSWORD_ERROR);
        PATTERN_MAP.put(ConnErrorCode.DB_NOT_EXISTS.getCode(), DB_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.CANNOT_ACQUIRE_CONNECT.getCode(), CANNOT_ACQUIRE_CONNECT);
        PATTERN_MAP.put(ConnErrorCode.JDBC_FORMAT_ERROR.getCode(), JDBC_FORMAT_ERROR);
    }
}
