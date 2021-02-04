package com.dtstack.engine.master.router.cache;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.schedule.common.enums.AppType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: newman
 * Date: 2020/12/31 1:47 下午
 * Description: 测试
 * @since 1.0.0
 */
public class TestSessionCache extends AbstractTest {

    @Autowired
    private RedisTemplate redisTemplate;

    private SessionCache sessionCache = new SessionCache();


    @Before
    public void setUp(){

        sessionCache.setRedisTemplate(redisTemplate);
        sessionCache.setAppType(AppType.CONSOLE);
        sessionCache.setExpire(1000);
    }

    @Test
    public void testSet(){

        sessionCache.set("abc","efg",1L);

    }


    @Test
    public void testGet(){

        Object o = sessionCache.get("abc", "efg", Long.class);
        Assert.assertNotNull(o);

    }


    @Test
    public void testRemove(){

        sessionCache.remove("abc");
    }
}
