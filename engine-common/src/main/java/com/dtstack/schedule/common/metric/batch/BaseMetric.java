package com.dtstack.schedule.common.metric.batch;


import com.dtstack.schedule.common.metric.MetricData;
import com.dtstack.schedule.common.metric.MetricResult;
import com.dtstack.schedule.common.metric.QueryInfo;
import com.dtstack.schedule.common.metric.Tuple;
import com.dtstack.schedule.common.metric.prometheus.PrometheusMetricQuery;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author toutian
 */
public abstract class BaseMetric implements IMetric {

    private String metricName;

    protected String jobId;

    private long startTime;

    private long endTime;

    protected String granularity;

    private PrometheusMetricQuery prometheusMetricQuery;

    protected static Map<String, String> metricNameMap = new HashMap<>();

    static {
        metricNameMap.put("conversionErrors", "flink_taskmanager_job_task_operator_flinkx_conversionErrors");
        metricNameMap.put("duplicateErrors", "flink_taskmanager_job_task_operator_flinkx_duplicateErrors");
        metricNameMap.put("nErrors", "flink_taskmanager_job_task_operator_flinkx_nErrors");
        metricNameMap.put("nullErrors", "flink_taskmanager_job_task_operator_flinkx_nullErrors");
        metricNameMap.put("numRead", "flink_taskmanager_job_task_operator_flinkx_numRead");
        metricNameMap.put("numWrite", "flink_taskmanager_job_task_operator_flinkx_numWrite");
        metricNameMap.put("otherErrors", "flink_taskmanager_job_task_operator_flinkx_otherErrors");
        metricNameMap.put("byteRead", "flink_taskmanager_job_task_operator_flinkx_byteRead");
        metricNameMap.put("byteWrite", "flink_taskmanager_job_task_operator_flinkx_byteWrite");
        metricNameMap.put("readDuration", "flink_taskmanager_job_task_operator_flinkx_readDuration");
        metricNameMap.put("writeDuration", "flink_taskmanager_job_task_operator_flinkx_writeDuration");
        metricNameMap.put("endLocation", "flink_taskmanager_job_task_operator_flinkx_endLocation");
        metricNameMap.put("startLocation", "flink_taskmanager_job_task_operator_flinkx_startLocation");
    }

    protected abstract QueryInfo buildQueryInfo();

    @Override
    public String getChartName() {
        return null;
    }

    @Override
    public Object getMetric() {
        QueryInfo queryInfo = buildQueryInfo();
        queryInfo.setGranularity(granularity);
        MetricResult metricResult = prometheusMetricQuery.queryRange(metricNameMap.get(metricName), startTime, endTime, queryInfo, getTagName());
        return formatData(metricResult);
    }

    protected Long formatData(MetricResult metricResult) {
        long count = 0L;
        if (metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())) {
            for (MetricData metricData : metricResult.getMetricDataList()) {
                if (CollectionUtils.isNotEmpty(metricData.getDps())) {
                    Object dp = metricData.getDps().get(metricData.getDps().size() - 1);
                    count = Math.max(count, ((Tuple<Long, Double>) dp).getTwo().longValue());
                }
            }
        }
        return count;
    }

    public void setPrometheusMetricQuery(PrometheusMetricQuery prometheusMetricQuery) {
        this.prometheusMetricQuery = prometheusMetricQuery;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public String getTagName() {
        return null;
    }
}
