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

package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.ComponentConfig;
import com.dtstack.engine.dao.ComponentConfigDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

/**
 * @author yuebai
 * @date 2021-02-18
 */
@Component
public class ComponentConfigOldCovertService implements ApplicationListener<ApplicationStartedEvent> {

    private final static Logger logger = LoggerFactory.getLogger(ComponentConfigOldCovertService.class);

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DataSource dataSource;

    String querySql = "select cc.id as component_id, cc.component_config, ce.cluster_id, cc.component_type_code, cc.component_template\n" +
            "from console_component cc\n" +
            "         left join console_engine ce on cc.engine_id = ce.id\n" +
            "limit %s,%s;";

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        String key = "componentConfigConvertOld";
        try {
            if (!isCanConvertOldData(key)) {
                return;
            }
            convertOldData();
        } catch (Exception e) {
            logger.error("component config covert old data error ", e);
        } finally {
            logger.info("component config covert is done ");
            redisTemplate.delete(key);
        }

    }

    private void convertOldData() throws SQLException {
        ResultSet resultSet = null;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery("show create table console_component");
            boolean isOldComponentConfig = false;
            while (resultSet.next()) {
                String createSql = resultSet.getString(2);
                isOldComponentConfig = createSql.contains("component_config");
            }
            if (isOldComponentConfig) {
                for (int i = 0; i < 100; i++) {
                    String sql = String.format(querySql, 50 * i, 50);
                    //将旧数据迁移指新表
                    resultSet = statement.executeQuery(sql);
                    boolean next = resultSet.next();
                    if (!next) {
                        logger.info("component config covert is empty so break ");
                        break;
                    }
                    while (next) {
                        String componentConfigStr = resultSet.getString("component_config");
                        String componentTemplateStr = resultSet.getString("component_template");
                        Long componentId = resultSet.getLong("component_id");
                        Long clusterId = resultSet.getLong("cluster_id");
                        int componentTypeCode = resultSet.getInt("component_type_code");
                        CompletableFuture.runAsync(() -> {
                            try {
                                componentConfigService.deepOldClientTemplate(componentConfigStr, componentTemplateStr, componentId, clusterId, componentTypeCode);
                            } catch (Exception e) {
                                logger.error("component config covert {}  old data error ", componentId, e);
                            }
                        });
                        next = resultSet.next();
                    }
                }
            }
        } finally {
            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException exception) {
                    logger.error("component config covert close error ", exception);
                }
            }
        }
    }

    private boolean isCanConvertOldData(String key) {
        if (redisTemplate.hasKey(key)) {
            logger.info("component config {} convert is not belong this ", key);
            return false;
        }
        ComponentConfig componentConfig = componentConfigDao.listFirst();
        if (null != componentConfig) {
            logger.info("component config {} already has data so stop ", componentConfig.getId());
            return false;
        }
        String execute = redisTemplate.execute((RedisCallback<String>) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            SetParams setParams = SetParams.setParams();
            setParams.nx().ex(2 * 60);
            return commands.set(key, "-1", setParams);
        });
        return StringUtils.isNotBlank(execute);
    }
}
