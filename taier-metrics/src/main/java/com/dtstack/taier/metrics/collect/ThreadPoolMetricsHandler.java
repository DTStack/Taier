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
 
package com.dtstack.taier.metrics.collect;

import com.dtstack.taier.metrics.collect.collector.InternalLogCollector;
import com.dtstack.taier.metrics.collect.collector.MetricsCollector;
import com.dtstack.taier.metrics.collect.collector.MicroMeterCollector;
import com.dtstack.taier.metrics.collect.collector.SystemMeterCollector;
import com.dtstack.taier.metrics.collect.entity.ThreadPoolStats;
import com.dtstack.taier.metrics.collect.util.ExtensionServiceLoader;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * adapter for ThreadPool Metrics collector
 * @author xingyi
 * @date 2025/9/16
 */
public class ThreadPoolMetricsHandler {

    private static final Map<String, MetricsCollector> COLLECTORS = Maps.newHashMap();

    private ThreadPoolMetricsHandler() {
        List<MetricsCollector> loadedCollectors = ExtensionServiceLoader.get(MetricsCollector.class);
        loadedCollectors.forEach(collector -> COLLECTORS.put(collector.type().toLowerCase(), collector));

        MetricsCollector microMeterCollector = new MicroMeterCollector();
        InternalLogCollector internalLogCollector = new InternalLogCollector();
        SystemMeterCollector systemMeterCollector = new SystemMeterCollector();
        COLLECTORS.put(microMeterCollector.type(), microMeterCollector);
        COLLECTORS.put(internalLogCollector.type(), internalLogCollector);
        COLLECTORS.put(systemMeterCollector.type(), systemMeterCollector);
    }

    public void collect(ThreadPoolStats threadPoolStats, List<String> collectorTypes) {
        for (String collectorType : collectorTypes) {
            MetricsCollector collector = COLLECTORS.get(collectorType.toLowerCase());
            if (collector != null) {
                collector.collect(threadPoolStats);
            }
        }
    }

    public static ThreadPoolMetricsHandler getInstance() {
        return CollectorHandlerHolder.INSTANCE;
    }

    private static class CollectorHandlerHolder {

        private static final ThreadPoolMetricsHandler INSTANCE = new ThreadPoolMetricsHandler();
    }

}
