package com.dtstack.engine.master.router.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * author: toutian
 */
public class RdosSubscribe implements MessageListener {

    private static Logger LOGGER = LoggerFactory.getLogger(SessionCache.class);

    private RedisTemplate<String, Object> redisTemplate;

    private SessionCache sessionCache;

    private ConsoleCache consoleCache;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            byte[] body = message.getBody();//请使用valueSerializer
            Object itemValue = redisTemplate.getValueSerializer().deserialize(body);
            byte[] channel = message.getChannel();
            String topic = redisTemplate.getStringSerializer().deserialize(channel);

            LOGGER.info("receive redis message, topic:{}, value:{}", topic, itemValue);

            if (RdosTopic.SESSION.equals(topic)) {
                sessionCache.remove(itemValue.toString());
            }
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setSessionCache(SessionCache sessionCache) {
        this.sessionCache = sessionCache;
    }

    public void setConsoleCache(ConsoleCache consoleCache) {
        this.consoleCache = consoleCache;
    }
}
