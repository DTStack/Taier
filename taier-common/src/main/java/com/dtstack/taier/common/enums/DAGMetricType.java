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

package com.dtstack.taier.common.enums;


import com.dtstack.taier.common.metric.stream.DAGBackPressureMetrics;
import com.dtstack.taier.common.metric.stream.DAGBytesInMetric;
import com.dtstack.taier.common.metric.stream.DAGBytesOutMetric;
import com.dtstack.taier.common.metric.stream.DAGNumRecordsInMetric;
import com.dtstack.taier.common.metric.stream.DAGNumRecordsOutMetric;
import com.dtstack.taier.common.metric.stream.DagLatencyMarkerMetric;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-03 20:15
 * @Description:
 */
public enum DAGMetricType {
    /**
     * 延迟标记
     */
    LATENCY_MARKER(0, DagLatencyMarkerMetric.class, DagLatencyMarkerMetric.getMetrics()),

    /**
     * 反压
     */
    BACK_PRESSURE(1, DAGBackPressureMetrics.class, DAGBackPressureMetrics.getMetrics()),

    /**
     * 读数量条数
     */
    NUM_RECORDS_IN(2, DAGNumRecordsInMetric.class, DAGNumRecordsInMetric.getMetrics()),

    /**
     * 写数量条数
     */
    NUM_RECORDS_OUT(3, DAGNumRecordsOutMetric.class, DAGNumRecordsOutMetric.getMetrics()),

    /**
     * 读字节数
     */
    BYTES_RECORDS_IN(4, DAGBytesInMetric.class, DAGBytesInMetric.getMetrics()),

    /**
     * 写字节数
     */
    BYTES_RECORDS_OUT(5, DAGBytesOutMetric.class, DAGBytesOutMetric.getMetrics());

    private String metricName;

    private Integer type;

    private Class<?> aClass;

    public Integer getType() {
        return type;
    }

    public Class<?> getaClass() {
        return aClass;
    }


    public String getMetricName() {
        return metricName;
    }

    DAGMetricType(Integer type, Class<?> aClass, String metricName) {
        this.type = type;
        this.aClass = aClass;
        this.metricName = metricName;
    }
}
