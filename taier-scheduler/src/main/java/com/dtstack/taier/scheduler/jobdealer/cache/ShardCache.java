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

import com.dtstack.taier.dao.domain.ScheduleJobCache;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.scheduler.jobdealer.JobStatusDealer;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
@Component
public class ShardCache implements ApplicationContextAware {

    private static Logger LOGGER = LoggerFactory.getLogger(ShardCache.class);

    private ApplicationContext applicationContext;

    private Map<String, ShardManager> jobResourceShardManager = new ConcurrentHashMap<>();

    private ShardManager getShardManager(String jobId) {
        ScheduleJobCacheService ScheduleJobCacheService = applicationContext.getBean(ScheduleJobCacheService.class);
        ScheduleJobCache engineJobCache = ScheduleJobCacheService.getByJobId(jobId);
        if (engineJobCache == null) {
            return null;
        }
        return jobResourceShardManager.computeIfAbsent(engineJobCache.getJobResource(), jr -> {
            ShardManager shardManager = new ShardManager(engineJobCache.getJobResource());
            JobStatusDealer jobStatusDealer = new JobStatusDealer();
            jobStatusDealer.setJobResource(engineJobCache.getJobResource());
            jobStatusDealer.setShardManager(shardManager);
            jobStatusDealer.setShardCache(this);
            jobStatusDealer.setApplicationContext(applicationContext);
            jobStatusDealer.start();
            return shardManager;
        });
    }

    public boolean updateLocalMemTaskStatus(String jobId, Integer status) {
        if (jobId == null || status == null) {
            throw new IllegalArgumentException("jobId or status must not null.");
        }
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            shardManager.putJob(jobId, status);
            return true;
        }
        return removeWithForeach(jobId);
    }


    public boolean updateLocalMemTaskStatus(String jobId, Integer status, Consumer<String> consumer) {
        if (jobId == null || status == null) {
            throw new IllegalArgumentException("jobId or status must not null.");
        }
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            shardManager.putJob(jobId, status);
            return true;
        }
        consumer.accept(jobId);
        return removeWithForeach(jobId);
    }

    public boolean removeIfPresent(String jobId) {
        if (jobId == null) {
            throw new IllegalArgumentException("jobId must not null.");
        }
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            shardManager.removeJob(jobId);
            return true;
        }
        return removeWithForeach(jobId);
    }

    private boolean removeWithForeach(String jobId) {
        LOGGER.warn("jobId:{} stackTrace:{}", jobId, ExceptionUtil.stackTrack());
        for (ShardManager shardManager : jobResourceShardManager.values()) {
            if (shardManager.getShard().remove(jobId) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
