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

package com.dtstack.taier.scheduler.jobdealer.cache;

import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.enums.RdosTaskStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * data 数据分片及空闲检测
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class ShardManager implements Runnable {

    private static final long DATA_CLEAN_INTERVAL = 1000;
    private ScheduledExecutorService scheduledService = null;
    private Map<String, Integer> shard;
    private String jobResource;

    public ShardManager(String jobResource) {
        this.jobResource = jobResource;
        this.shard = new ConcurrentHashMap<>();
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(jobResource + this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                DATA_CLEAN_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public Integer putJob(String jobId, Integer status) {
        return shard.put(jobId, status);
    }

    public Integer removeJob(String jobId) {
        return shard.remove(jobId);
    }

    public Map<String, Integer> getShard() {
        return shard;
    }

    public String getJobResource() {
        return jobResource;
    }

    @Override
    public void run() {
        shard.entrySet().removeIf(jobWithStatus -> RdosTaskStatus.needClean(jobWithStatus.getValue()));
    }

}
