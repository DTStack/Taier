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

package com.dtstack.taier.common.metric;

import com.dtstack.taier.common.param.MetricPO;

import java.util.List;

/**
 * metric 查询返回结果对象
 * Date: 2018/10/9
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class MetricResult {

    private String metricName;

    private List<MetricData> metricDataList;

    private MetricPO metric;

    private List<Object> value;

    public MetricPO getMetric() {
        return metric;
    }

    public void setMetric(MetricPO metric) {
        this.metric = metric;
    }

    public List<Object> getValue() {
        return value;
    }

    public void setValue(List<Object> value) {
        this.value = value;
    }

    public List<MetricData> getMetricDataList() {
        return metricDataList;
    }

    public void setMetricDataList(List<MetricData> metricDataList) {
        this.metricDataList = metricDataList;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
}
