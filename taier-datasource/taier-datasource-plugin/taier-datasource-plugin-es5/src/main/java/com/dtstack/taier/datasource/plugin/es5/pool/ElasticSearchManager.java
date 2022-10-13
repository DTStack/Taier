package com.dtstack.taier.datasource.plugin.es5.pool;

import com.dtstack.taier.datasource.api.dto.source.ESSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.pool.PoolConfig;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:30 2020/8/3
 * @Description：
 */
@Slf4j
@NoArgsConstructor
public class ElasticSearchManager {
    private volatile static ElasticSearchManager manager;

    private volatile Map<String, ElasticSearchPool> sourcePool = Maps.newConcurrentMap();

    private static final String ES_KEY = "address:%s,username:%s,password:%s";

    public static ElasticSearchManager getInstance() {
        if (null == manager) {
            synchronized (ElasticSearchManager.class) {
                if (null == manager) {
                    manager = new ElasticSearchManager();
                }
            }
        }
        return manager;
    }

    public ElasticSearchPool getConnection(ISourceDTO source) {
        String key = getPrimaryKey(source).intern();
        ElasticSearchPool elasticSearchPool = sourcePool.get(key);
        if (elasticSearchPool == null) {
            synchronized (ElasticSearchManager.class) {
                elasticSearchPool = sourcePool.get(key);
                if (elasticSearchPool == null) {
                    elasticSearchPool = initSource(source);
                    sourcePool.putIfAbsent(key, elasticSearchPool);
                }
            }
        }

        return elasticSearchPool;
    }

    /**
     * 初始化es pool
     *
     * @param source
     * @return
     */
    public ElasticSearchPool initSource(ISourceDTO source) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) source;
        PoolConfig poolConfig = esSourceDTO.getPoolConfig();
        if (Objects.isNull(poolConfig)) {
            throw new SourceException("init ElasticSearchPool fail ,poolConfig can't null");
        }
        ElasticSearchPoolConfig config = new ElasticSearchPoolConfig();
        config.setMaxWaitMillis(poolConfig.getConnectionTimeout());
        config.setMinIdle(poolConfig.getMinimumIdle());
        config.setMaxIdle(poolConfig.getMaximumPoolSize());
        config.setMaxTotal(poolConfig.getMaximumPoolSize());
        config.setTimeBetweenEvictionRunsMillis(poolConfig.getMaxLifetime() / 10);
        config.setMinEvictableIdleTimeMillis(poolConfig.getMaxLifetime());
        // 闲置实例校验标识，如果校验失败会删除当前实例
        config.setTestWhileIdle(Boolean.TRUE);

        config.setUsername(esSourceDTO.getUsername());
        config.setPassword(esSourceDTO.getPassword());
        config.setNodes(new HashSet<>(Arrays.asList(esSourceDTO.getUrl().split(","))));

        ElasticSearchPool pool = new ElasticSearchPool(config);
        pool.addObjects(poolConfig.getMinimumIdle());
        log.info("Get ES data source connection(Pool), address : {}, userName : {}", esSourceDTO.getUrl(), esSourceDTO.getUsername());
        return pool;
    }

    private String getPrimaryKey(ISourceDTO sourceDTO) {
        ESSourceDTO esSourceDTO = (ESSourceDTO) sourceDTO;
        return String.format(ES_KEY, esSourceDTO.getUrl(), esSourceDTO.getUsername(), esSourceDTO.getPassword());
    }
}
