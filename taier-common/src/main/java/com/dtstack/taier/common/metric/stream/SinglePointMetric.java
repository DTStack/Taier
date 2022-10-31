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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 查询单个点位的 prometheus 的指标
 *
 * @author ：wangchuan
 * date：Created in 下午1:17 2021/4/15
 * company: www.dtstack.com
 */
public class SinglePointMetric implements IMetric {

    // job_id
    private String jobId;

    // 查询某个时间，为null则查询当前时间
    private Long time;

    // 指标名称
    private String metricName;

    // prometheus
    private PrometheusMetricQuery prometheusMetricQuery;

    // 用于区分结果集，根据 prometheus 返回结果 metric 的字段区分 相同 tag 的值会放入一组中
    private String tagName = "operator_name";

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

    public static SinglePointMetric buildSinglePointMetric(String metricName, String jobId, Long time, PrometheusMetricQuery prometheusMetricQuery) {
        SinglePointMetric singlePointMetric = new SinglePointMetric();
        singlePointMetric.setJobId(jobId);
        singlePointMetric.setTime(time);
        singlePointMetric.setPrometheusMetricQuery(prometheusMetricQuery);
        singlePointMetric.setMetricName(metricName);
        return singlePointMetric;
    }

    @Override
    public Map<String, List<Double>> getMetric() {
        Map<String, List<Double>> metricResult = Maps.newHashMap();
        MetricResult result = prometheusMetricQuery.query(getMetricName(), null, buildQueryInfo(), getTagName());
        List<MetricData> metricDataList = result.getMetricDataList();
        if (CollectionUtils.isNotEmpty(metricDataList)) {
            for (MetricData metricData : metricDataList) {
                String tagName = metricData.getTagName();
                if (StringUtils.isBlank(tagName)) {
                    continue;
                }
                List<Double> doubles = metricResult.get(tagName);
                if (Objects.isNull(doubles)) {
                    metricResult.put(tagName, Lists.newArrayList());
                }
                List<Tuple<Long, Double>> dps = metricData.getDps();
                if (CollectionUtils.isNotEmpty(dps)) {
                    for (Tuple<Long, Double> dp : dps) {
                        metricResult.get(tagName).add(dp.getTwo());
                    }
                }
            }
        }
        return metricResult;
    }

    @Override
    public String getChartName() {
        return null;
    }

    /**
     * 获取所有 tag 的结果和
     *
     * @return metric 指标和
     */
    public Double getCountValue() {
        Map<String, List<Double>> inputRecordMetric = getMetric();
        double value = 0;
        for (Map.Entry<String, List<Double>> entry : inputRecordMetric.entrySet()) {
            List<Double> valueList = entry.getValue();
            for (Double v : valueList) {
                value = value + v;
            }
        }
        return value;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public PrometheusMetricQuery getPrometheusMetricQuery() {
        return prometheusMetricQuery;
    }

    public void setPrometheusMetricQuery(PrometheusMetricQuery prometheusMetricQuery) {
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
