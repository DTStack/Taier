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
