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

package com.dtstack.taier.common.jobdealer;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.logstore.AbstractLogStore;
import com.dtstack.taier.pluginapi.logstore.LogStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sishu.yss on 2018/2/26.
 */
@Component
@DependsOn("environmentContext")
public class TaskLogStoreDealer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskLogStoreDealer.class);

    private final static int CHECK_INTERVAL = 18000000;

    private AbstractLogStore logStore;
    private ScheduledExecutorService scheduledService;
    private Map<String, String> dbConfig = new HashMap<>();

    @Autowired
    private EnvironmentContext environmentContext;

    @PostConstruct
    public void init() {
        dbConfig.put(ConfigConstant.JDBCURL, environmentContext.getJdbcUrl());
        dbConfig.put(ConfigConstant.USERNAME, environmentContext.getJdbcUser());
        dbConfig.put(ConfigConstant.PASSWORD, environmentContext.getJdbcPassword());
        dbConfig.put(ConfigConstant.INITIAL_SIZE, String.valueOf(environmentContext.getInitialPoolSize()));
        dbConfig.put(ConfigConstant.MINIDLE, String.valueOf(environmentContext.getMinPoolSize()));
        dbConfig.put(ConfigConstant.MAXACTIVE, String.valueOf(environmentContext.getMaxPoolSize()));

        logStore = LogStoreFactory.getLogStore(dbConfig);

        this.scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("TaskLogStoreDealer start again...");
            logStore.clearJob();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

}
