package com.dtstack.task.runner.config;

import com.dtstack.dtcenter.common.cache.*;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.lock.RedLock;
import com.dtstack.task.common.env.EnvironmentContext;
import com.dtstack.task.common.exception.RdosDefineException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author toutian
 */
@EnableCaching
@Configuration
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
    @Autowired
    private EnvironmentContext environmentContext;

    List<RedisNode> getRedisNodes() {
        List<RedisNode> nodes = new ArrayList<>();
        String sentinel = environmentContext.getRedisSentinel();
        if (StringUtils.isNotBlank(sentinel)) {
            String[] split = sentinel.split(",");
            for (String node : split) {
                String[] nodeInfo = node.split(":");
                if (nodeInfo.length != 2) {
                    throw new RdosDefineException("redis哨兵项配置错误");
                } else {
                    nodes.add(new RedisNode(nodeInfo[0].trim(), Integer.valueOf(nodeInfo[1].trim())));
                }
            }
        }
        return nodes;
    }

    Set<String> getSentinelAddress() {
        List<RedisNode> redisNodes = getRedisNodes();
        Set<String> set = new HashSet<>();
        for (RedisNode redisNode : redisNodes) {
            set.add(redisNode.getHost() + ":" + redisNode.getPort());
        }
        return set;
    }

    RedisSentinelConfiguration sentinelConfiguration() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master("mymaster");
        List<RedisNode> nodes = getRedisNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        } else {
            for (RedisNode sn : nodes) {
                sentinelConfig.sentinel(sn.getHost(), sn.getPort());
            }
            JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", getSentinelAddress());
            logger.info("redis sentinel master = {}", String.valueOf(sentinelPool.getCurrentHostMaster()));
        }
        return sentinelConfig;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(environmentContext.getMaxIdle());
        jedisPoolConfig.setMaxTotal(environmentContext.getMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(environmentContext.getMaxWaitMills());
        return jedisPoolConfig;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory factory;
        RedisSentinelConfiguration redisSentinelConfiguration = sentinelConfiguration();
        if (redisSentinelConfiguration == null) {
            factory = new JedisConnectionFactory();
            factory.setHostName(environmentContext.getRedisUrl());
            factory.setPort(environmentContext.getRedisPort());
        } else {
            factory = new JedisConnectionFactory(redisSentinelConfiguration);
        }
        if (StringUtils.isNotBlank(environmentContext.getRedisPassword())) {
            factory.setPassword(environmentContext.getRedisPassword());
        }
        factory.setDatabase(environmentContext.getRedisDB());
        factory.setTimeout(environmentContext.getRedisTimeout());
        factory.setUsePool(true);
        factory.setPoolConfig(jedisPoolConfig);
        return factory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        return stringRedisTemplate;
    }

    /*
     * 据shared与否的设置,Spring分别通过CacheManager.create()或new CacheManager()方式来创建一个ehcache基地.
     */
    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new FileSystemResource(System.getProperty("user.dir") + "/conf/ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    /**
     * ehcache 主要的管理器
     */
    @Bean
    public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean) {
        return new EhCacheCacheManager(bean.getObject());
    }

    @Primary
    @Bean(name = AuthEhCache.AUTH_EHCACHE)
    public EhCacheFactoryBean ehCacheFactoryBean(EhCacheCacheManager ehCacheCacheManager) {
        EhCacheFactoryBean bean = new EhCacheFactoryBean();
        bean.setCacheName(AuthEhCache.AUTH_EHCACHE);
        bean.setCacheManager(ehCacheCacheManager.getCacheManager());
        return bean;
    }

    @Bean(name = "authCache")
    public AuthCache authCache(RedisTemplate<String, Object> redisTemplate, EhCacheFactoryBean ehCacheFactoryBean) {
        AuthCache authCache = new AuthCache();
        authCache.setRedisTemplate(redisTemplate);
        authCache.setEhCache((net.sf.ehcache.Cache) ehCacheFactoryBean.getObject());
        authCache.setName(AuthEhCache.AUTH_EHCACHE);
        authCache.setActiveCount(environmentContext.getCacheActiveCount());
        authCache.setActiveRedis(environmentContext.getCacheActiveRedis());
        authCache.setLiveTime(environmentContext.getCacheLiveTime());
        authCache.setAppType(AppType.RDOS);
        return authCache;
    }

    @Primary
    @Bean(name = "ehRedisCacheManager")
    public SimpleCacheManager simpleCacheManager(AuthCache authCache) {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(Lists.newArrayList(authCache));
        return simpleCacheManager;
    }

    @Bean
    public SessionCache sessionCache(RedisTemplate<String, Object> redisTemplate) {
        SessionCache sessionCache = new SessionCache();
        sessionCache.setExpire(environmentContext.getRdosSessionExpired());
        sessionCache.setRedisTemplate(redisTemplate);
        sessionCache.setAppType(AppType.RDOS);
        return sessionCache;
    }

    @Bean
    public ConsoleCache consoleCache(RedisTemplate<String, Object> redisTemplate) {
        ConsoleCache consoleCache = new ConsoleCache();
        consoleCache.setExpire(environmentContext.getRdosSessionExpired());
        consoleCache.setRedisTemplate(redisTemplate);
        return consoleCache;
    }

    @Bean
    public RdosSubscribe rdosSubscribe(RedisTemplate redisTemplate, SessionCache sessionCache, AuthCache authCache, ConsoleCache consoleCache) {
        RdosSubscribe rdosSubscribe = new RdosSubscribe();
        rdosSubscribe.setAuthCache(authCache);
        rdosSubscribe.setRedisTemplate(redisTemplate);
        rdosSubscribe.setSessionCache(sessionCache);
        rdosSubscribe.setConsoleCache(consoleCache);
        return rdosSubscribe;
    }

    @Bean
    public RedisMessageListenerContainer messageContainer(JedisConnectionFactory jedisConnectionFactory, RdosSubscribe rdosSubscribe) {
        RedisMessageListenerContainer messageContainer = new RedisMessageListenerContainer();
        messageContainer.setConnectionFactory(jedisConnectionFactory);
        messageContainer.addMessageListener(rdosSubscribe, sessionTopic());
        messageContainer.addMessageListener(rdosSubscribe, authTopic());
        messageContainer.addMessageListener(rdosSubscribe, consoleTopic());
        return messageContainer;
    }

    @Bean
    @Scope(value = "prototype")
    public RedLock createRedLock(StringRedisTemplate redisTemplate) {
        RedLock redLock = new RedLock(redisTemplate);
        return redLock;
    }


    @Bean
    public Topic sessionTopic() {
        return new ChannelTopic(RdosTopic.SESSION);
    }

    @Bean
    public Topic authTopic() {
        return new ChannelTopic(RdosTopic.AUTH);
    }

    @Bean
    public Topic consoleTopic() {
        return new ChannelTopic(RdosTopic.CONSOLE);
    }

}
