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


import com.dtstack.taier.common.enums.EMetricTag;
import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.stream.prometheus.ICustomMetric;
import com.dtstack.taier.common.metric.stream.prometheus.ICustomMetricQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 metric
 *
 * @author ：wangchuan
 * date：Created in 下午2:06 2021/4/16
 * company: www.dtstack.com
 */
public class CustomMetric<T> implements ICustomMetric<T> {

    // metric 名称
    private String metricName;

    // 开始时间
    private long startTime;

    // 结束时间
    private long endTime;

    // tag类别
    private EMetricTag metricTag;

    // tag 值
    private String tagValue;

    // 时间粒度
    private String granularity;

    // 构建好的 prometheus 指标查询类
    private ICustomMetricQuery<T> customPrometheusMetricQuery;

    /**
     * 构造查询条件
     *
     * @return 查询条件信息
     */
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        Filter filter = new Filter();
        filter.setType(metricTag.getType());
        filter.setFilter(String.format(metricTag.getFilter(), tagValue));
        filter.setTagk(metricTag.getTagName());
        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);
        queryInfo.setFilters(filterList);
        queryInfo.setGranularity(granularity);
        return queryInfo;
    }

    /**
     * 构造方法私有化
     */
    private CustomMetric() {
    }

    /**
     * 构建自定义指标查询器
     *
     * @param metricName                  metric 名称
     * @param startTime                   开始时间
     * @param endTime                     结束时间
     * @param metricTag                   tag类别
     * @param tagValue                    tag值，用于确定metric与当前任务绑定关系
     * @param granularity                 时间粒度
     * @param customPrometheusMetricQuery 构建好的 prometheus 指标查询类
     * @param <T>                         customPrometheusMetricQuery 的范型
     * @return 自定义指标查询器
     */
    public static <T> CustomMetric<T> buildCustomMetric(String metricName, long startTime, long endTime, EMetricTag metricTag, String tagValue, String granularity, ICustomMetricQuery<T> customPrometheusMetricQuery) {
        CustomMetric<T> customMetric = new CustomMetric<>();
        customMetric.metricName = metricName;
        customMetric.startTime = startTime;
        customMetric.endTime = endTime;
        customMetric.metricTag = metricTag;
        customMetric.tagValue = tagValue;
        customMetric.customPrometheusMetricQuery = customPrometheusMetricQuery;
        if (StringUtils.isNotBlank(granularity)) {
            customMetric.granularity = granularity;
        } else {
            customMetric.granularity = "1m";
        }
        return customMetric;
    }

    @Override
    public T getMetric(Integer maxPoint) {
        return customPrometheusMetricQuery.queryRange(metricName, startTime, endTime, buildQueryInfo(), maxPoint);
    }
}
