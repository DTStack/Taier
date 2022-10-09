package com.dtstack.taier.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * datasource config
 *
 * @author ：wangchuan
 * date：Created in 10:06 2022/9/27
 * company: www.dtstack.com
 */
@Configuration
@ConfigurationProperties(prefix = "taier")
public class DatasourceConfig {

    // datasource config
    Map<String, Object> datasource = new HashMap<>();

    public Map<String, Object> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, Object> datasource) {
        this.datasource = datasource;
    }
}
