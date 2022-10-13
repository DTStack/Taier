package com.dtstack.taier.datasource.plugin.vertica;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 * @author ：wangchuan
 * date：Created in 下午1:46 2022/8/16
 * company: www.dtstack.com
 */
public class VerticaErrorPattern extends AbsErrorPattern {

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)Required\\s+Connection\\s+Key\\(s\\):\\s+database,\\s+host");

    static {
        PATTERN_MAP.clear();
        PATTERN_MAP.put(ConnErrorCode.URL_MISSING_DATABASE.getCode(), USERNAME_PASSWORD_ERROR);
    }
}
