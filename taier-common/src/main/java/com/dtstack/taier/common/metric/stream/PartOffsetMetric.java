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
import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.MetricData;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.Tuple;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class PartOffsetMetric extends DataDelayMetric {

    private String topicName;

    public PartOffsetMetric(String jobId, PrometheusMetricQuery prometheusMetricQuery) {
        super(jobId, prometheusMetricQuery);
    }

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();

        Filter filter = new Filter();
        filter.setType("=");
        filter.setFilter(jobId);
        filter.setTagk("job_id");

        Filter topicFilter = new Filter();
        topicFilter.setType("=");
        topicFilter.setFilter(topicName);
        topicFilter.setTagk("topic");

        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);
        filterList.add(topicFilter);

        queryInfo.setFilters(filterList);
        return queryInfo;
    }

    @Override
    public Object formatData(MetricResult metricResult) {
        List<JSONObject> data = new ArrayList<>();
        if (metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())){
            for (MetricData metricData : metricResult.getMetricDataList()) {
                JSONObject item = new JSONObject();
                item.put("partitionId",metricData.getTagName());
                item.put("currentLocation",((Tuple<Long, Long>)metricData.getDps().get(0)).getTwo());
                data.add(item);
            }
        }
        return data;
    }

    @Override
    public String getMetricName() {
        return "flink_taskmanager_job_task_operator_KafkaConsumer_topic_partition_currentOffsets";
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String getTagName(){
        return "partition";
    }
}
