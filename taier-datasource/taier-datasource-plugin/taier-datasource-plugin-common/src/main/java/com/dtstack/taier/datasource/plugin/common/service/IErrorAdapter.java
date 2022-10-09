package com.dtstack.taier.datasource.plugin.common.service;

import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;

/**
 * 数据库错误分析、统一接口
 *
 * @author ：wangchuan
 * date：Created in 上午10:53 2020/11/6
 * company: www.dtstack.com
 */
public interface IErrorAdapter {

    /**
     * 获取连接失败分析
     *
     * @param errorMsg 错误信息
     * @param errorPattern 对应的正则实现类
     * @return 统一的错误描述
     */
    String connAdapter(String errorMsg, IErrorPattern errorPattern);

    /**
     * sql执行失败分析
     *
     * @param errorMsg 错误信息
     * @param errorPattern 对应的正则实现类
     * @return 统一的错误描述
     */
    String sqlAdapter(String errorMsg, IErrorPattern errorPattern);
}
