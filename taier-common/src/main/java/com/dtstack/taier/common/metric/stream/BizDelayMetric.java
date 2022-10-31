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

import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.MetricData;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.Tuple;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.taier.common.metric.prometheus.func.CommonFunc;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class BizDelayMetric implements IMetric {

    private String jobId;

    private PrometheusMetricQuery prometheusMetricQuery;

    public BizDelayMetric(String jobId, PrometheusMetricQuery prometheusMetricQuery) {
        this.jobId = jobId;
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

    @Override
    public Object getMetric() {
        QueryInfo queryInfo = buildQueryInfo();
        MetricResult metricResult = prometheusMetricQuery.query(getMetricName(), null, queryInfo, null);
        return formatData(metricResult);
    }

    @Override
    public String getChartName() {
        return null;
    }

    private QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        Filter filter = new Filter();

        filter.setType("=");
        filter.setFilter(jobId);
        filter.setTagk("job_id");

        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);

        queryInfo.setFilters(filterList);

        List<String> byLabel = Lists.newArrayList();
        byLabel.add("__name__");

        CommonFunc sumFunc = new CommonFunc("max");
        queryInfo.addAggregator(sumFunc);

        return queryInfo;
    }

    public Double formatData(MetricResult metricResult) {
        Double delay = 0.0;
        if (metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())) {
            for (MetricData metricData : metricResult.getMetricDataList()) {
                for (Object dp : metricData.getDps()) {
                    delay = ((Tuple<Long, Double>) dp).getTwo();
                }
            }
        }
        return delay;
    }

    public String getMetricName() {
        return "flink_taskmanager_job_task_operator_dtEventDelay";
    }
}
