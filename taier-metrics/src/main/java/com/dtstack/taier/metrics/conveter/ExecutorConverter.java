/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.metrics.conveter;

import com.dtstack.taier.metrics.ExecutorWrapper;
import com.dtstack.taier.metrics.PerformanceProvider;
import com.dtstack.taier.metrics.ThreadPoolStatProvider;
import com.dtstack.taier.metrics.adapter.ExecutorAdapter;
import com.dtstack.taier.metrics.collect.entity.ThreadPoolStats;

import java.util.concurrent.TimeUnit;

/**
 * ExecutorConverter related
 **/
public class ExecutorConverter {

    private ExecutorConverter() {
    }

    public static ThreadPoolStats toMetrics(ExecutorWrapper wrapper) {
        ExecutorAdapter<?> executor = wrapper.getExecutor();
        if (executor == null) {
            return null;
        }
        ThreadPoolStatProvider provider = wrapper.getThreadPoolStatProvider();
        PerformanceProvider performanceProvider = provider.getPerformanceProvider();
        PerformanceProvider.PerformanceSnapshot performanceSnapshot = performanceProvider.getSnapshotAndReset();
        ThreadPoolStats poolStats = convertCommon(executor);
        poolStats.setPoolName(wrapper.getThreadPoolName());
        poolStats.setPoolAliasName(wrapper.getThreadPoolAliasName());
        poolStats.setRunTimeoutCount(provider.getRunTimeoutCount());
        poolStats.setQueueTimeoutCount(provider.getQueueTimeoutCount());
        poolStats.setRejectCount(provider.getRejectedTaskCount());

        poolStats.setTps(performanceSnapshot.getTps());
        poolStats.setAvg(performanceSnapshot.getAvg());
        poolStats.setMaxRt(performanceSnapshot.getMaxRt());
        poolStats.setMinRt(performanceSnapshot.getMinRt());
        return poolStats;
    }

    private static ThreadPoolStats convertCommon(ExecutorAdapter<?> executor) {
        ThreadPoolStats poolStats = new ThreadPoolStats();
        poolStats.setCorePoolSize(executor.getCorePoolSize());
        poolStats.setMaximumPoolSize(executor.getMaximumPoolSize());
        poolStats.setPoolSize(executor.getPoolSize());
        poolStats.setActiveCount(executor.getActiveCount());
        poolStats.setLargestPoolSize(executor.getLargestPoolSize());
        poolStats.setQueueType(executor.getQueueType());
        poolStats.setQueueCapacity(executor.getQueueCapacity());
        poolStats.setQueueSize(executor.getQueueSize());
        poolStats.setQueueRemainingCapacity(executor.getQueueRemainingCapacity());
        poolStats.setTaskCount(executor.getTaskCount());
        poolStats.setCompletedTaskCount(executor.getCompletedTaskCount());
        poolStats.setWaitTaskCount(executor.getQueueSize());
        poolStats.setRejectHandlerName(executor.getRejectHandlerType());
        poolStats.setKeepAliveTime(executor.getKeepAliveTime(TimeUnit.MILLISECONDS));
        return poolStats;
    }
}
