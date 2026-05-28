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
 
package com.dtstack.taier.metrics.prometheus;

import io.micrometer.prometheus.HistogramFlavor;
import io.micrometer.prometheus.PrometheusConfig;

import java.time.Duration;

/**
 * Adapter to convert  to a {@link PrometheusConfig}.
 */
public class PrometheusPropertiesConfigAdapter
        implements
            PrometheusConfig {

    public PrometheusPropertiesConfigAdapter() {
    }

    @Override
    public String prefix() {
        return "management.metrics.export.prometheus";
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public boolean descriptions() {
        return true;
    }

    @Override
    public HistogramFlavor histogramFlavor() {
        return HistogramFlavor.Prometheus;
    }

    @Override
    public Duration step() {
        // TODO 从配置文件中获取
        return Duration
                .ofMinutes(Long.parseLong(System.getProperty("management.metrics.export.prometheus.step", "10")));
    }

}
