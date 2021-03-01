package com.dtstack.engine.master.router.cache;

import com.dtstack.engine.master.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: newman
 * Date: 2020/12/31 10:59 上午
 * Description: 测试
 * @since 1.0.0
 */
public class TestAdosSubscribe extends AbstractTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testOnMessage(){

        RdosSubscribe subscribe = new RdosSubscribe();
        subscribe.setRedisTemplate(redisTemplate);
        SessionCache sessionCache = new SessionCache();
        subscribe.setSessionCache(sessionCache);
        subscribe.onMessage(new Message() {
            @Override
            public byte[] getBody() {
                return new byte[0];
            }

            @Override
            public byte[] getChannel() {
                return new byte[0];
            }
        },null);
    }




}
