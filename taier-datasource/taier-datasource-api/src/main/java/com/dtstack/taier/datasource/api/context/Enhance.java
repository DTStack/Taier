package com.dtstack.taier.datasource.api.context;

/**
 * 基础类增强, 提供获取运行时上下文等能力
 *
 * @author ：wangchuan
 * date：Created in 10:05 2022/9/23
 * company: www.dtstack.com
 */
public interface Enhance {

    /**
     * 获取运行环境
     *
     * @return 运行环境
     */
    RuntimeContext getRuntimeContext();

    /**
     * 设置运行环境
     */
    void setRuntimeContext(RuntimeContext runtimeContext);
}
