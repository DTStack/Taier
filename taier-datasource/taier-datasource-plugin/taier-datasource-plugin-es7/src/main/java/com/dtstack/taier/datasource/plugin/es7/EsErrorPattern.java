package com.dtstack.taier.datasource.plugin.es7;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 *
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public class EsErrorPattern extends AbsErrorPattern {

    private static final Pattern CANNOT_ACQUIRE_CONNECT = Pattern.compile("(?i)Connection\\s*refused");
    static {
        PATTERN_MAP.put(ConnErrorCode.CANNOT_ACQUIRE_CONNECT.getCode(), CANNOT_ACQUIRE_CONNECT);
    }
}
