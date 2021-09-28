/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.router.cache;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.common.enums.AppType;
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
        sessionCache.set("abc","efg",1L);
        Object o = sessionCache.get("abc", "efg", Long.class);
        Assert.assertNotNull(o);

    }


    @Test
    public void testRemove(){

        sessionCache.remove("abc");
    }
}
