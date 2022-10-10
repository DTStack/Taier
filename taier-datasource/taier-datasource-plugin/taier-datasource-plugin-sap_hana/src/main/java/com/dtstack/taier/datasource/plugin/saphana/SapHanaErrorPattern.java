package com.dtstack.taier.datasource.plugin.saphana;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 * sap hana 错误适配器
 *
 * @author ：wangchuan
 * date：Created in 上午10:13 2021/12/30
 * company: www.dtstack.com
 */
public class SapHanaErrorPattern extends AbsErrorPattern {

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)Access\\s*denied\\s*for\\s*user.*using\\s*password");
    private static final Pattern DB_NOT_EXISTS = Pattern.compile("(?i)Access\\s*denied\\s*for\\s*user.*to\\s*database");
    private static final Pattern IP_PORT_FORMAT_ERROR = Pattern.compile("(?i)not\\s*received\\s*any\\s*packets");
    static {
        PATTERN_MAP.put(ConnErrorCode.USERNAME_PASSWORD_ERROR.getCode(), USERNAME_PASSWORD_ERROR);
        PATTERN_MAP.put(ConnErrorCode.DB_NOT_EXISTS.getCode(), DB_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.IP_PORT_FORMAT_ERROR.getCode(), IP_PORT_FORMAT_ERROR);
    }
}
