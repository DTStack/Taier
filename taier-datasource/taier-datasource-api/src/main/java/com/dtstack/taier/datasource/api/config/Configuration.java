package com.dtstack.taier.datasource.api.config;

import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * Configuration
 *
 * @author ：wangchuan
 * date：Created in 10:40 2022/9/23
 * company: www.dtstack.com
 */
public class Configuration extends AbstractConfig {

    public Configuration() {
    }

    public Configuration(Map<String, Object> configMap) {
        if (MapUtils.isNotEmpty(configMap)) {
            addAll(configMap);
        }
    }

}
