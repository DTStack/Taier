package com.dtstack.taier.datasource.plugin.solr.pool;

import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SolrSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.pool.PoolConfig;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 下午5:14 2021/5/7
 * @Description：
 */
@Slf4j
@NoArgsConstructor
public class SolrManager {
    private volatile static SolrManager manager;

    private volatile Map<String, SolrPool> sourcePool = Maps.newConcurrentMap();

    private static final String SOLR_KEY = "zkHost:%s,chroot:%s";

    public static SolrManager getInstance() {
        if (null == manager) {
            synchronized (SolrManager.class) {
                if (null == manager) {
                    manager = new SolrManager();
                }
            }
        }
        return manager;
    }

    public SolrPool getConnection(ISourceDTO source) {
        String key = getPrimaryKey(source).intern();
        SolrPool SolrPool = sourcePool.get(key);
        if (SolrPool == null) {
            synchronized (SolrManager.class) {
                SolrPool = sourcePool.get(key);
                if (SolrPool == null) {
                    SolrPool = initSource(source);
                    sourcePool.putIfAbsent(key, SolrPool);
                }
            }
        }

        return SolrPool;
    }

    /**
     * 初始化solr pool
     *
     * @param source
     * @return
     */
    public SolrPool initSource(ISourceDTO source) {
        SolrSourceDTO solrSourceDTO = (SolrSourceDTO) source;
        PoolConfig poolConfig = solrSourceDTO.getPoolConfig();
        if (Objects.isNull(poolConfig)) {
            throw new SourceException("init SolrPool fail ,poolConfig can't null");
        }
        SolrPoolConfig config = new SolrPoolConfig();
        config.setMaxWaitMillis(poolConfig.getConnectionTimeout());
        config.setMinIdle(poolConfig.getMinimumIdle());
        config.setMaxIdle(poolConfig.getMaximumPoolSize());
        config.setMaxTotal(poolConfig.getMaximumPoolSize());
        config.setTimeBetweenEvictionRunsMillis(poolConfig.getMaxLifetime() / 10);
        config.setMinEvictableIdleTimeMillis(poolConfig.getMaxLifetime());
        // 闲置实例校验标识，如果校验失败会删除当前实例
        config.setTestWhileIdle(Boolean.TRUE);
        config.setZkHosts(solrSourceDTO.getZkHost());
        config.setChroot(solrSourceDTO.getChroot());

        SolrPool pool = new SolrPool(config);
        pool.addObjects(poolConfig.getMinimumIdle());
        log.info("Get solr data source connection(Pool), zkHost : {}, chroot : {}", solrSourceDTO.getZkHost(), solrSourceDTO.getChroot());
        return pool;
    }

    private String getPrimaryKey(ISourceDTO sourceDTO) {
        SolrSourceDTO solrSourceDTO = (SolrSourceDTO) sourceDTO;
        return String.format(SOLR_KEY, solrSourceDTO.getZkHost(), solrSourceDTO.getChroot());
    }
}
