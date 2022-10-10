package com.dtstack.taier.datasource.plugin.hive3_cdp;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 14:03 2021/05/13
 */
public class HiveErrorPattern extends AbsErrorPattern {

    private static final Pattern DB_NOT_EXISTS = Pattern.compile("(?i)Database\\s+'(?<database>(.*))'\\s+not\\s+found");

    private static final Pattern DB_PERMISSION_ERROR = Pattern.compile("(?i)Permission\\s*denied");

    static {
        PATTERN_MAP.put(ConnErrorCode.DB_NOT_EXISTS.getCode(), DB_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.DB_PERMISSION_ERROR.getCode(), DB_PERMISSION_ERROR);
    }
}
