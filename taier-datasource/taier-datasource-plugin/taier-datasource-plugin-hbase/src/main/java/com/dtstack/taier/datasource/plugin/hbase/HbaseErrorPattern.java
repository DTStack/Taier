package com.dtstack.taier.datasource.plugin.hbase;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 *
 * @author ：wangchuan
 * date：Created in 下午1:46 2020/11/6
 * company: www.dtstack.com
 */
public class HbaseErrorPattern extends AbsErrorPattern {

    private static final Pattern CANNOT_ACQUIRE_CONNECT = Pattern.compile("(?i)Connection\\s*refused");
    private static final Pattern ZK_NODE_NOT_EXISTS = Pattern.compile("(?i)The\\s*node.*is\\s*not\\s*in\\s*ZooKeeper");
    private static final Pattern ZK_IS_NOT_CONNECT = Pattern.compile("(?i)Can't\\s*get\\s*connection\\s*to\\s*ZooKeeper");
    static {
        PATTERN_MAP.put(ConnErrorCode.CANNOT_ACQUIRE_CONNECT.getCode(), CANNOT_ACQUIRE_CONNECT);
        PATTERN_MAP.put(ConnErrorCode.ZK_NODE_NOT_EXISTS.getCode(), ZK_NODE_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.ZK_IS_NOT_CONNECT.getCode(), ZK_IS_NOT_CONNECT);
    }
}
