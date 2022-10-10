package com.dtstack.taier.datasource.api.config;

/**
 * 配置信息
 *
 * @author ：wangchuan
 * date：Created in 20:14 2022/9/23
 * company: www.dtstack.com
 */
public interface Config {

    <T> T getConfig(String key, Class<T> type);

    <T> T getConfig(String key, Class<T> type, T defaultValue);
}
