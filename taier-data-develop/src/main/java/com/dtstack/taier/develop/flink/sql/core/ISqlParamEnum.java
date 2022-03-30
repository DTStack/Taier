package com.dtstack.taier.develop.flink.sql.core;

/**
 * sql 参数基本方法
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public interface ISqlParamEnum {

    /**
     * 获取前端映射参数
     *
     * @return 前端展示参数
     */
    String getFront();

    /**
     * 获取 flink 1.10 版本参数
     *
     * @return flink 1.10 版本参数
     */
    String getFlink110();

    /**
     * 获取 flink 1.12 版本参数
     *
     * @return flink 1.12 版本参数
     */
    String getFlink112();
}
