package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONArray;
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

public class PartDataTrendMetric extends DataDelayMetric {

    private long startTime;

    private long endTime;

    protected String topicName;

    private String partId;

    protected String granularity;

    private PrometheusMetricQuery prometheusMetricQuery;

    private String metricName = "flink_taskmanager_job_task_operator_topic_partition_dtTopicPartitionLag";

    public PartDataTrendMetric(String jobId, PrometheusMetricQuery prometheusMetricQuery) {
        super(jobId, prometheusMetricQuery);
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

    @Override
    protected QueryInfo buildQueryInfo() {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setGranularity(granularity);

        Filter filter = new Filter();
        filter.setType("=");
        filter.setFilter(jobId);
        filter.setTagk("job_id");

        Filter topicFilter = new Filter();
        topicFilter.setType("=");
        topicFilter.setFilter(topicName);
        topicFilter.setTagk("topic");

        Filter partitionFilter = new Filter();
        partitionFilter.setType("=");
        partitionFilter.setFilter(partId);
        partitionFilter.setTagk("partition");


        List<Filter> filterList = new ArrayList<>();
        filterList.add(filter);
        filterList.add(topicFilter);
        filterList.add(partitionFilter);

        // 按任务标示对结果进行聚合，由于tm failover情况下，会取到多个tm的结果集
        List<String> byLabel = Lists.newArrayList();
        byLabel.add("instance");
        byLabel.add("job");
        byLabel.add("operator_name");
        byLabel.add("job_name");
        byLabel.add("__name__");

        CommonFunc sumFunc = new CommonFunc("sum");
        sumFunc.setByLabel(byLabel);

        queryInfo.addAggregator(sumFunc);
        queryInfo.setFilters(filterList);
        return queryInfo;
    }

    @Override
    public Object getMetric() {
        QueryInfo queryInfo = buildQueryInfo();
        MetricResult metricResult = prometheusMetricQuery.queryRange(getMetricName(),startTime,endTime,queryInfo,getTagName());
        return formatData(metricResult);
    }

    @Override
    public JSONArray formatData(MetricResult metricResult) {
        JSONArray data = new JSONArray();
        if(metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())){
            for (MetricData metricData : metricResult.getMetricDataList()) {
                for (Object dp : metricData.getDps()) {
                    JSONObject item = new JSONObject();
                    item.put("data",((Tuple<Long, Long>)dp).getTwo());
                    item.put("time",((Tuple<Long, Long>)dp).getOne());
                    data.add(item);
                }
            }
        }

        return data;
    }

    @Override
    public String getMetricName() {
        return metricName;
    }

    @Override
    public String getTagName(){
        return "partition";
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
}
