package com.dtstack.taier.datasource.api.base;

import com.dtstack.taier.datasource.api.config.SourceConfig;

import java.io.IOException;

/**
 * client base
 *
 * @author ：wangchuan
 * date：Created in 11:40 2022/9/23
 * company: www.dtstack.com
 */
public interface Client {

    /**
     * 初始化 client
     *
     * @param config 配置信息
     * @throws Exception 异常信息
     */
    default void open(SourceConfig config) throws Exception {
    }

    /**
     * 关闭 client
     *
     * @throws IOException 异常信息
     */
    default void close() throws IOException {
    }
}
