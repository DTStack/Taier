/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.worker.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to initialize system resource metrics.
 */
public class SystemResourcesMetricsAnalyzer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SystemResourcesMetricsAnalyzer.class);

    private final Map<String, Object> metrics = new HashMap<>();

    private SystemResourcesCounter systemResourcesCounter;
    private HardwareAbstractionLayer hardwareAbstractionLayer;

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void instantiateSystemMetrics(long probeInterval) {
        try {
            systemResourcesCounter = new SystemResourcesCounter(probeInterval);
            systemResourcesCounter.start();

            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(probeInterval));

            SystemInfo systemInfo = new SystemInfo();
            hardwareAbstractionLayer = systemInfo.getHardware();
        } catch (NoClassDefFoundError ex) {
            LOG.warn(
                    "Failed to initialize system resource metrics because of missing class definitions." +
                            " Did you forget to explicitly add the oshi-core optional dependency?",
                    ex);
        } catch (Exception e) {
            LOG.error("instantiateSystemMetrics error:", e);
        }
    }

    private void instantiateMemoryMetrics(GlobalMemory memory) {
        metrics.<String, Long>put("Available", memory.getAvailable());
        metrics.<String, Long>put("Total", memory.getTotal());
    }

    private void instantiateSwapMetrics(GlobalMemory memory) {
        metrics.<String, Long>put("Used", memory.getSwapUsed());
        metrics.<String, Long>put("Total", memory.getSwapTotal());
    }

    private void instantiateCPUMetrics(SystemResourcesCounter usageCounter) {
        metrics.put("Usage", usageCounter.getCpuUsage());
        metrics.put("Idle", usageCounter.getCpuIdle());
        metrics.put("Sys", usageCounter.getCpuSys());
        metrics.put("User", usageCounter.getCpuUser());
        metrics.put("IOWait", usageCounter.getIOWait());
        metrics.put("Nice", usageCounter.getCpuNice());
        metrics.put("Irq", usageCounter.getCpuIrq());
        metrics.put("SoftIrq", usageCounter.getCpuSoftIrq());

        metrics.put("Load1min", usageCounter.getCpuLoad1());
        metrics.put("Load5min", usageCounter.getCpuLoad5());
        metrics.put("Load15min", usageCounter.getCpuLoad15());

        for (int i = 0; i < usageCounter.getProcessorsCount(); i++) {
            metrics.put(String.format("UsageCPU%d", i), usageCounter.getCpuUsagePerProcessor(i));
        }
    }

    private void instantiateNetworkMetrics(SystemResourcesCounter usageCounter) {
        for (int i = 0; i < usageCounter.getNetworkInterfaceNames().length; i++) {
            String name = usageCounter.getNetworkInterfaceNames()[i];
            metrics.put(name + "_ReceiveRate", usageCounter.getReceiveRatePerInterface(i));
            metrics.put(name + "_SendRate", usageCounter.getSendRatePerInterface(i));
        }
    }

    @Override
    public void run() {
        try {
            instantiateMemoryMetrics(hardwareAbstractionLayer.getMemory());
            instantiateSwapMetrics(hardwareAbstractionLayer.getMemory());
            instantiateCPUMetrics(systemResourcesCounter);
            instantiateNetworkMetrics(systemResourcesCounter);
        } catch (Throwable e) {
            LOG.error("SystemResourcesMetricsAnalyzer running error:", e);
        }
    }
}
