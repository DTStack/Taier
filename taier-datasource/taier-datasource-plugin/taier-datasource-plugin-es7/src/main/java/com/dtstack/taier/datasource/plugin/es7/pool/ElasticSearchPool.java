package com.dtstack.taier.datasource.plugin.es7.pool;

import com.dtstack.taier.datasource.plugin.common.Pool;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:14 2020/8/3
 * @Description：
 */
public class ElasticSearchPool extends Pool<RestHighLevelClient> {

    private ElasticSearchPoolConfig config;

    public ElasticSearchPool(ElasticSearchPoolConfig config) {
        super(config, new ElasticSearchPoolFactory(config));
        this.config = config;
    }

    public ElasticSearchPoolConfig getConfig() {
        return config;
    }

    public void setConfig(ElasticSearchPoolConfig config) {
        this.config = config;
    }

}
