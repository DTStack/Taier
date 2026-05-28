/*
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *     
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
package com.dtstack.taier.metrics.monitor;

import com.dtstack.taier.metrics.ExecutorWrapper;
import com.dtstack.taier.metrics.collect.ThreadPoolMetricsHandler;
import com.dtstack.taier.metrics.collect.entity.ThreadPoolStats;
import com.dtstack.taier.metrics.conveter.ExecutorConverter;
import com.dtstack.taier.metrics.factory.TpThreadFactory;
import com.dtstack.taier.metrics.registry.TpRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xingyi
 * @date 2025/9/17
 */
@Slf4j
public class TpMonitor {

    private static ScheduledExecutorService monitorExecutor;

    private List<String> supportMonitorMetricsType;

    public void interval(long monitorInterval, long monitorDelay, List<String> supportMonitorMetricsType) {

        monitorExecutor = new ScheduledThreadPoolExecutor(1,
                new TpThreadFactory("Taier-ThreadPool-Monitor"));
        this.supportMonitorMetricsType = supportMonitorMetricsType;

        monitorExecutor.scheduleWithFixedDelay(this::run, monitorDelay, monitorInterval, TimeUnit.SECONDS);
    }

    public void run() {
        Set<String> executorNames = TpRegistry.getAllExecutorNames();
        try {
            collectMetrics(executorNames);
        } catch (Exception e) {
            log.error("DynamicTp monitor, run error", e);
        }
    }

    private void collectMetrics(Set<String> executorNames) {
        executorNames.forEach(x -> {
            ExecutorWrapper wrapper = TpRegistry.getExecutorWrapper(x);
            doCollect(ExecutorConverter.toMetrics(wrapper));
        });
    }

    private void doCollect(ThreadPoolStats threadPoolStats) {
        try {
            ThreadPoolMetricsHandler.getInstance().collect(
                    threadPoolStats, this.supportMonitorMetricsType);
        } catch (Exception e) {
            log.error("DynamicTp monitor, metrics collect error.", e);
        }
    }

    public void destroy() {
        monitorExecutor.shutdownNow();
    }

}
