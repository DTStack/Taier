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

package com.dtstack.taiga.rdbs.common.executor;

import com.dtstack.taiga.pluginapi.CustomThreadFactory;
import com.dtstack.taiga.pluginapi.JobClient;
import com.dtstack.taiga.pluginapi.logstore.AbstractLogStore;
import com.dtstack.taiga.pluginapi.logstore.LogStoreFactory;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时更新任务的执行修改时间和清理过期任务
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * author: toutian
 */

public class StatusUpdateDealer {

    private static final Logger LOG = LoggerFactory.getLogger(StatusUpdateDealer.class);

    private final static int MODIFY_CHECK_INTERVAL = 2 * 1000;
    private final static int TIMEOUT_CHECK_INTERVAL = 30 * 1000;

    private Map<String, JobClient> jobCache;
    private ModifyCheckJob modifyCheckJob;
    private TimeoutCheckJob timeoutCheckJob;

    private ScheduledExecutorService scheduledService;

    public StatusUpdateDealer(Map<String, JobClient> jobCache) {
        this.jobCache = jobCache;
        modifyCheckJob = new ModifyCheckJob();
        timeoutCheckJob = new TimeoutCheckJob();
        scheduledService = new ScheduledThreadPoolExecutor(2, new CustomThreadFactory(this.getClass().getSimpleName()));
    }

    public void start() {
        scheduledService.scheduleWithFixedDelay(
                modifyCheckJob,
                MODIFY_CHECK_INTERVAL,
                MODIFY_CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
        scheduledService.scheduleWithFixedDelay(
                timeoutCheckJob,
                TIMEOUT_CHECK_INTERVAL,
                TIMEOUT_CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    private class ModifyCheckJob implements Runnable {
        @Override
        public void run() {
            try {
                AbstractLogStore logStore = LogStoreFactory.getLogStore();
                if (null != logStore && MapUtils.isNotEmpty(jobCache)) {
                    logStore.updateModifyTime(jobCache.keySet());
                }
            } catch (Throwable e) {
                LOG.error("", e);
            }
        }
    }

    private class TimeoutCheckJob implements Runnable {
        @Override
        public void run() {
            try {
                AbstractLogStore logStore = LogStoreFactory.getLogStore();
                if (null != logStore) {
                    logStore.timeOutDeal();
                }
            } catch (Throwable e) {
                LOG.error("", e);
            }
        }
    }
}
