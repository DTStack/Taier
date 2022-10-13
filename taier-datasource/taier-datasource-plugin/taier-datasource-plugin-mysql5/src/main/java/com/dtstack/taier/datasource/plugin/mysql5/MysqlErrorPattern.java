package com.dtstack.taier.datasource.plugin.mysql5;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public class MysqlErrorPattern extends AbsErrorPattern {

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)Access\\s*denied\\s*for\\s*user.*using\\s*password");
    private static final Pattern DB_NOT_EXISTS = Pattern.compile("(?i)Access\\s*denied\\s*for\\s*user.*to\\s*database");
    private static final Pattern IP_PORT_FORMAT_ERROR = Pattern.compile("(?i)not\\s*received\\s*any\\s*packets");

    static {
        PATTERN_MAP.put(ConnErrorCode.USERNAME_PASSWORD_ERROR.getCode(), USERNAME_PASSWORD_ERROR);
        PATTERN_MAP.put(ConnErrorCode.DB_NOT_EXISTS.getCode(), DB_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.IP_PORT_FORMAT_ERROR.getCode(), IP_PORT_FORMAT_ERROR);
    }
}
