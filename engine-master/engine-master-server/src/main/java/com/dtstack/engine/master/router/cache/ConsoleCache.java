package com.dtstack.engine.master.router.cache;

import com.dtstack.engine.common.exception.RdosDefineException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 * @author toutian
 */
public class ConsoleCache implements InitializingBean {

    private RedisTemplate<String, Object> redisTemplate;

    public void publishRemoveMessage(String tenantId) {
        redisTemplate.convertAndSend(RdosTopic.CONSOLE, tenantId);
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == this.redisTemplate) {
            throw new RdosDefineException("AuthCache'redisTemplate is null");
        }
    }
}

