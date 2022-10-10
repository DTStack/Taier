package com.dtstack.taier.datasource.plugin.common.exception;

import java.util.regex.Pattern;

/**
 * 错误信息正则匹配
 *
 * @author ：wangchuan
 * date：Created in 下午1:34 2020/11/6
 * company: www.dtstack.com
 */
public interface IErrorPattern {

    /**
     * 获取连接时错误匹配
     * @param errorCode 错误代码
     * @return 对应匹配正则规则
     */
    Pattern getConnErrorPattern(Integer errorCode);
}
