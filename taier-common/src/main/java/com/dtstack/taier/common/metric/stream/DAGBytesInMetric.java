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

package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.DAGMetricType;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.param.MetricPO;
import com.dtstack.taier.common.param.PromtheusMetrics;

import java.util.HashMap;
import java.util.Map;

import static com.dtstack.taier.common.metric.stream.DAGBackPressureMetrics.METRIC_QUERY;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 20:10
 * @Description:
 */
public class DAGBytesInMetric extends DagMetric{

    private static String METRIC_NAME = "flink_taskmanager_job_task_numBytesIn";


    @Override
    public Object formatData(String result) {
        Map<String,Object> metricMap = new HashMap<>();
        if (result == null) {
            return metricMap;
        }
        PromtheusMetrics promtheusMetrics = JSONObject.parseObject(result,PromtheusMetrics.class);
        for(MetricResult metricResult:promtheusMetrics.getData().getResult()){
            MetricPO metricPO = metricResult.getMetric();
            metricMap.putIfAbsent(String.format(METRIC_QUERY, DAGMetricType.BYTES_RECORDS_IN.getMetricName(),metricPO.getTaskId(),metricPO.getSubtaskIndex()),metricResult.getValue().get(1));
        }
        return metricMap;
    }

    @Override
    public Object formatData(MetricResult metricResult) {
        return null;
    }

    public static String getMetrics() {
        return METRIC_NAME;
    }

    @Override
    public String getMetricName() {
        return METRIC_NAME;
    }

}
