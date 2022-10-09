package com.dtstack.taier.datasource.api.enums;

/**
 * 匹配类型
 *
 * @author ：wangchuan
 * date：Created in 下午9:07 2021/12/6
 * company: www.dtstack.com
 */
public enum MatchType {

    /**
     * 前缀匹配
     */
    PREFIX(),

    /**
     * 后缀匹配
     */
    SUFFIX(),

    /**
     * 全匹配
     */
    ALL(),

    /**
     * 模糊搜索
     */
    CONTAINS();
}
