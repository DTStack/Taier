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
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.stream.prometheus.DAGPrometheusMetricQuery;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-03 15:01
 * @Description:
 */
public abstract class DagMetric implements IMetric {

    private String jobId;

    private DAGPrometheusMetricQuery prometheusMetricQuery;


    public QueryInfo buildQueryInfo() {
        List<Filter> filters = Lists.newArrayList();
        Filter filter = new Filter();
        filter.setFilter(jobId);
        filter.setType("=");
        filter.setTagk("job_id");
        filters.add(filter);
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setFilters(filters);
        return queryInfo;
    }

    /**
     * 根据返回参数组装metricMap
     *
     * @param result
     * @return
     */
    public abstract Object formatData(String result);

    public abstract Object formatData(MetricResult metricResult);

    public abstract String getMetricName();

    /**
     * 执行查询操作
     *
     * @return
     */
    @Override
    public Object getMetric() {
        QueryInfo queryInfo = buildQueryInfo();
        String result = prometheusMetricQuery.queryResult(getMetricName(), null, queryInfo, getMetricName());
        return formatData(result);
    }

    @Override
    public String getChartName() {
        return null;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public DAGPrometheusMetricQuery getPrometheusMetricQuery() {
        return prometheusMetricQuery;
    }

    public void setPrometheusMetricQuery(DAGPrometheusMetricQuery prometheusMetricQuery) {
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

}
