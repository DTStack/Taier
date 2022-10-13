package com.dtstack.taier.datasource.plugin.common.exception;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public abstract class AbsErrorPattern implements IErrorPattern {

    protected static final Map<Integer, Pattern> PATTERN_MAP = Maps.newHashMap();

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)Access\\s*denied\\s*for\\s*user");
    private static final Pattern DB_NOT_EXISTS = Pattern.compile("(?i)Unknown\\s+database\\s+'(?<database>(.*))'");
    private static final Pattern HDFS_PERMISSION_ERROR = Pattern.compile("(?i)Permission\\s*denied");
    private static final Pattern JDBC_FORMAT_ERROR = Pattern.compile("(?i)No\\s*suitable\\s*driver\\s*found");
    private static final Pattern CONNECTION_TIMEOUT = Pattern.compile("(?i)connect\\s*timed\\s*out");
    private static final Pattern CANNOT_ACQUIRE_CONNECT = Pattern.compile("(?i)Connection\\s*refused");
    private static final Pattern UNKNOWN_HOST_ERROR = Pattern.compile("(?i)UnknownHostException");

    static {
        PATTERN_MAP.put(ConnErrorCode.USERNAME_PASSWORD_ERROR.getCode(), USERNAME_PASSWORD_ERROR);
        PATTERN_MAP.put(ConnErrorCode.DB_NOT_EXISTS.getCode(), DB_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.HDFS_PERMISSION_ERROR.getCode(), HDFS_PERMISSION_ERROR);
        PATTERN_MAP.put(ConnErrorCode.JDBC_FORMAT_ERROR.getCode(), JDBC_FORMAT_ERROR);
        PATTERN_MAP.put(ConnErrorCode.CONNECTION_TIMEOUT.getCode(), CONNECTION_TIMEOUT);
        PATTERN_MAP.put(ConnErrorCode.CANNOT_ACQUIRE_CONNECT.getCode(), CANNOT_ACQUIRE_CONNECT);
        PATTERN_MAP.put(ConnErrorCode.UNKNOWN_HOST_ERROR.getCode(), UNKNOWN_HOST_ERROR);
    }

    @Override
    public Pattern getConnErrorPattern(Integer errorCode) {
        return PATTERN_MAP.get(errorCode);
    }
}
