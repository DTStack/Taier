package com.dtstack.taier.common.metric.stream;


import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;

public abstract class DataDelayMetric implements IMetric {

    protected String jobId;

    private PrometheusMetricQuery prometheusMetricQuery;

    @Override
    public String getChartName() {
        return null;
    }

    protected DataDelayMetric(String jobId, PrometheusMetricQuery prometheusMetricQuery) {
        this.jobId = jobId;
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

    protected abstract QueryInfo buildQueryInfo();

    @Override
    public Object getMetric() {
        QueryInfo queryInfo = buildQueryInfo();
        MetricResult metricResult = prometheusMetricQuery.query(getMetricName(),null,queryInfo,getTagName());
        return formatData(metricResult);
    }

    public abstract Object formatData(MetricResult metricResult);

    public String getTagName(){
        return "topic";
    }

    public abstract String getMetricName();
}
