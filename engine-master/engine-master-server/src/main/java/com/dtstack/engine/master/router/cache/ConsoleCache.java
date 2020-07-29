package com.dtstack.engine.master.router.cache;

import com.alibaba.druid.pool.DruidDataSource;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用户缓存 集群信息、hadoopConf、HiveJdbc连接池
 *
 * @author toutian
 */
public class ConsoleCache implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ConsoleCache.class);

    private final String CACHE_PREFIX_CONSOLE = "console_tenant_id_";

    private int expire = 30 * 60;//seconds

    private Cache<String, Map<String, Object>> uaCache = CacheBuilder.newBuilder().expireAfterWrite(expire, TimeUnit.SECONDS).maximumSize(1000).build();

    private RedisTemplate<String, Object> redisTemplate;

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * hadoopConf 缓存
     */
    private static volatile Map configurationMap = Maps.newConcurrentMap();

    /***
     * yarnConf 缓存
     */
    private static volatile Map yarnConfigurationMap = Maps.newConcurrentMap();

    /**
     * hive-jdbc 缓存
     */
    private static volatile Map comboPooledDataSourceMap = Maps.newConcurrentMap();

    /**
     * libra-jdbc 缓存
     */
    private static volatile Map<Long, DruidDataSource> libraComboPooledDataSourceMap = Maps.newConcurrentMap();


    /**
     * tidb-jdbc 缓存
     */
    private static volatile Map<String, DruidDataSource> tidbComboPooledDataSourceMap = Maps.newConcurrentMap();

    private static volatile Map<String, String> tidbCacheKey = Maps.newConcurrentMap();

    public void set(String tenantId, String key, Object value) {
        try {
            String cacheKey = getCacheKey(tenantId);
            if(ConsoleUtil.CacheKey.TIDB.name().equals(key)){
                tidbCacheKey.put(tenantId,cacheKey);
            }
            Map<String, Object> data = uaCache.getIfPresent(cacheKey);
            if (data == null) {
                data = Maps.newConcurrentMap();
            }
            data.put(key, value);
            uaCache.put(cacheKey, data);
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(data), expire, TimeUnit.SECONDS);
        } catch (Throwable e) {
            logger.error("{}", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String tenantId, String key, Class<?> cla) {
        try {
            String cacheKey = getCacheKey(tenantId);
            Map<String, Object> data = uaCache.getIfPresent(cacheKey);
            if (data == null) {
                Object result = redisTemplate.opsForValue().get(cacheKey);
                if (result != null && StringUtils.isNotBlank(result.toString())) {
                    data = objectMapper.readValue(result.toString(), Map.class);
                    if (data != null) {
                        if (PublicUtil.isJavaBaseType(cla)) {
                            return (T) PublicUtil.classConvter(cla, data.get(key));
                        } else if (Map.class.equals(cla)) {
                            return (T) data.get(key);
                        } else {
                            return (T) PublicUtil.objectToObject(data.get(key), cla);
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            return (T) data.get(key);
        } catch (Throwable e) {
            logger.error("{}", e);
        }
        return null;
    }

    public void publishRemoveMessage(String tenantId) {
        redisTemplate.convertAndSend(RdosTopic.CONSOLE, tenantId);
    }

    public void remove(String tenantId) {
        try {
            String cacheKey = getCacheKey(tenantId);
            uaCache.asMap().remove(cacheKey);
            if (StringUtils.isNotBlank(tenantId) && !tenantId.contains(".")) {
                configurationMap.remove(Long.valueOf(tenantId));
                yarnConfigurationMap.remove(Long.valueOf(tenantId));
                comboPooledDataSourceMap.remove(Long.valueOf(tenantId));

                DruidDataSource dataSource = (DruidDataSource) comboPooledDataSourceMap.remove(Long.valueOf(tenantId));
                if (dataSource != null) {
                    dataSource.close();
                }

                DruidDataSource libraComboPooledDataSource = libraComboPooledDataSourceMap.remove(Long.valueOf(tenantId));
                if (libraComboPooledDataSource != null) {
                    libraComboPooledDataSource.close();
                }
            }
            for (String key : tidbCacheKey.keySet()) {
                //tidb 是tenantId.userId
                if (key.startsWith(tenantId)) {
                    uaCache.asMap().remove(tidbCacheKey.get(key));
                    redisTemplate.delete(tidbCacheKey.get(key));
                    DruidDataSource tiDBPoolDataSource = tidbComboPooledDataSourceMap.remove(tidbCacheKey.get(key));
                    if(Objects.nonNull(tiDBPoolDataSource)){
                        tiDBPoolDataSource.close();
                    }
                }
            }

            if (redisTemplate.hasKey(cacheKey)) {
                redisTemplate.delete(cacheKey);
            }
        } catch (Throwable e) {
            logger.error("{}", e);
        }
    }

    private String getCacheKey(Object key) {
        return CACHE_PREFIX_CONSOLE + key.toString();
    }


    public void setExpire(int expire) {
        this.expire = expire;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static Map getConfigurationMap() {
        return configurationMap;
    }


    public static Map getYarnConfigurationMap() {
        return yarnConfigurationMap;
    }

    public static Map getComboPooledDataSourceMap() {
        return comboPooledDataSourceMap;
    }

    public static Map<Long, DruidDataSource> getLibraComboPooledDataSourceMap() {
        return libraComboPooledDataSourceMap;
    }

    public static Map<String, DruidDataSource> getTidbComboPooledDataSourceMap() {
        return tidbComboPooledDataSourceMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == this.redisTemplate) {
            throw new RdosDefineException("AuthCache'redisTemplate is null");
        }
    }
}

