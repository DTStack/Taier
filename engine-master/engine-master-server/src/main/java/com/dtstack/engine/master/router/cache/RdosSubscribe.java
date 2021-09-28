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

import com.dtstack.engine.master.vo.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * author: toutian
 */
public class RdosSubscribe implements MessageListener {

    private static Logger LOGGER = LoggerFactory.getLogger(SessionCache.class);

    private RedisTemplate<String, Object> redisTemplate;

    private SessionCache sessionCache;

    private List<Consumer<Pair<String,String>>> consumers = new ArrayList<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            byte[] body = message.getBody();//请使用valueSerializer
            Object itemValue = redisTemplate.getValueSerializer().deserialize(body);
            byte[] channel = message.getChannel();
            String topic = redisTemplate.getStringSerializer().deserialize(channel);

            LOGGER.info("receive redis message, topic:{}, value:{}", topic, itemValue);
            if(null == itemValue){
                return;
            }
            if (RdosTopic.SESSION.equals(topic)) {
                sessionCache.remove(itemValue.toString());
            }
            for (Consumer<Pair<String,String>> consumer : consumers) {
                consumer.accept(new Pair<>(topic,itemValue.toString()));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setSessionCache(SessionCache sessionCache) {
        this.sessionCache = sessionCache;
    }

    public void setCallBack(Consumer<Pair<String,String>> consumer){
        consumers.add(consumer);
    }
}