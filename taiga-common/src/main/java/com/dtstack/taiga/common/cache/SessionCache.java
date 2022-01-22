package com.dtstack.taiga.common.cache;

import com.dtstack.taiga.common.exception.DtCenterDefException;
import com.dtstack.taiga.common.util.PublicUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sishu.yss
 */
public class SessionCache implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(SessionCache.class);

    /**
     * 过期时间，单位seconds
     */
    private int expire = 30 * 60;

    private Cache<String, Map<String, Object>> uaCache = CacheBuilder.newBuilder().expireAfterWrite(expire, TimeUnit.SECONDS).maximumSize(1000).build();

    private RedisTemplate<String, Object> redisTemplate;


    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void set(String sessionId, String key, Object value) {
        try {
            String cacheKey = getCacheKey(sessionId);
            Map<String, Object> data = uaCache.getIfPresent(cacheKey);
            if (data == null) {
                data = Maps.newConcurrentMap();
            }
            data.put(key, value);
            uaCache.put(cacheKey, data);
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(data), expire, TimeUnit.SECONDS);
        } catch (Throwable e) {
            logger.error("SessionCache set error", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String sessionId, String key, Class<?> cla) {
        try {
            String cacheKey = getCacheKey(sessionId);
            Map<String, Object> data = uaCache.getIfPresent(cacheKey);
            if (data == null) {
                Object result = redisTemplate.opsForValue().get(cacheKey);
                if (result != null && StringUtils.isNotBlank(result.toString())) {
                    data = objectMapper.readValue(result.toString(), Map.class);
                    if (data != null) {
                        if (PublicUtil.isJavaBaseType(cla)) {
                            return (T) PublicUtil.ClassConvter(cla, data.get(key));
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
            logger.error("SessionCache get error", e);
        }
        return null;
    }

    public void publishRemoveMessage(String sessionId) {
        redisTemplate.convertAndSend(RdosTopic.SESSION, sessionId);
    }

    public void remove(String sessionId) {
        try {
            String cacheKey = getCacheKey(sessionId);
            uaCache.asMap().remove(cacheKey);
            if (redisTemplate.hasKey(cacheKey)) {
                redisTemplate.delete(cacheKey);
            }
        } catch (Throwable e) {
            logger.error("SessionCache remove error", e);
        }
    }

    private String getCacheKey(Object key) {
        return key.toString();
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == this.redisTemplate) {
            throw new DtCenterDefException("AuthCache'redisTemplate is null");
        }
    }
}

