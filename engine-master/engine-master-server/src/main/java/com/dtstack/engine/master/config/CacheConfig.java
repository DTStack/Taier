package com.dtstack.engine.master.config;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.cache.RdosSubscribe;
import com.dtstack.engine.master.router.cache.RdosTopic;
import com.dtstack.engine.master.router.cache.SessionCache;
import com.dtstack.schedule.common.enums.AppType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public SessionCache sessionCache(RedisTemplate<String, Object> redisTemplate) {
        SessionCache sessionCache = new SessionCache();
        sessionCache.setExpire(environmentContext.getRdosSessionExpired());
        sessionCache.setRedisTemplate(redisTemplate);
        sessionCache.setAppType(AppType.DAGSCHEDULEX);
        return sessionCache;
    }

    @Bean
    public ConsoleCache consoleCache(RedisTemplate<String, Object> redisTemplate) {
        ConsoleCache consoleCache = new ConsoleCache();
        consoleCache.setRedisTemplate(redisTemplate);
        return consoleCache;
    }

    @Bean
    public RdosSubscribe rdosSubscribe(RedisTemplate redisTemplate, SessionCache sessionCache) {
        RdosSubscribe rdosSubscribe = new RdosSubscribe();
        rdosSubscribe.setRedisTemplate(redisTemplate);
        rdosSubscribe.setSessionCache(sessionCache);
        return rdosSubscribe;
    }

    @Bean
    public RedisMessageListenerContainer messageContainer(JedisConnectionFactory jedisConnectionFactory, RdosSubscribe rdosSubscribe) {
        RedisMessageListenerContainer messageContainer = new RedisMessageListenerContainer();
        messageContainer.setConnectionFactory(jedisConnectionFactory);
        messageContainer.addMessageListener(rdosSubscribe, sessionTopic());
        return messageContainer;
    }

    @Bean
    public Topic sessionTopic() {
        return new ChannelTopic(RdosTopic.SESSION);
    }
}
