package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.metric.Filter;
import com.dtstack.taier.common.metric.MetricData;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.Tuple;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.taier.common.metric.prometheus.func.CommonFunc;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class TopicPartNumMetric extends DataDelayMetric {

    private String metricName = "flink_taskmanager_job_task_operator_topic_partition_dtTopicPartitionLag";

    public TopicPartNumMetric(String jobId, PrometheusMetricQuery prometheusMetricQuery) {
        super(jobId, prometheusMetricQuery);
    }

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();

        Filter filter = new Filter();
        filter.setType("=");
        filter.setFilter(jobId);
        filter.setTagk("job_id");

        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);

        queryInfo.setFilters(filterList);
        // prometheus 没有去重，此处先聚合再count
        List<String> byLabelDistinct = Lists.newArrayList();
        byLabelDistinct.add("topic");
        byLabelDistinct.add("partition");
        CommonFunc sumFunc = new CommonFunc("sum");
        sumFunc.setByLabel(byLabelDistinct);
        queryInfo.addAggregator(sumFunc);

        List<String> byLabel = Lists.newArrayList();
        byLabel.add("topic");

        CommonFunc countFunc = new CommonFunc("count");
        countFunc.setByLabel(byLabel);

        queryInfo.addAggregator(countFunc);

        return queryInfo;
    }

    @Override
    public Object formatData(MetricResult metricResult) {
        List<JSONObject> data = new ArrayList<>();
        if (metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())) {
            for (MetricData metricData : metricResult.getMetricDataList()) {
                JSONObject item = new JSONObject();
                item.put("partitionId", metricData.getTagName());
                item.put("partCount", ((Tuple<Long, Long>) metricData.getDps().get(0)).getTwo());
                data.add(item);
            }
        }
        return data;
    }

    @Override
    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
}
