package com.dtstack.taier.datasource.plugin.common.enums;

/**
 * @author luming
 * @date 2022/2/17
 */
public class LocalVar {
    public static ThreadLocal<String> procSqlName = new ThreadLocal<>();
}
