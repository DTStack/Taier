package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.param.MetricPO;
import com.dtstack.taier.common.param.PromtheusMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-03 17:24
 * @Description:
 */
public class DAGBackPressureMetrics extends DagMetric{
    public static final String METRIC_QUERY = "%s-%s-%s";
    public static final String METRIC_QUERY_LATENCY = "%s-%s-%s-%s-%s";
    private String taskId;

    private Integer subtaskIndex;

    private static String METRIC_NAME = "flink_taskmanager_job_task_isBackPressured";
    @Override
    public Object formatData(String result) {
        Map<String,Object> metricMap = new HashMap<>();
        if (result == null) {
            return metricMap;
        }
        PromtheusMetrics promtheusMetrics = JSONObject.parseObject(result,PromtheusMetrics.class);
        for(MetricResult metricResult:promtheusMetrics.getData().getResult()){
            MetricPO metricPO = metricResult.getMetric();
            metricMap.putIfAbsent(String.format(METRIC_QUERY,this.getMetricName(),metricPO.getTaskId(),metricPO.getSubtaskIndex()),metricResult.getValue().get(1));
        }
        return metricMap;
    }

    @Override
    public Object formatData(MetricResult metricResult) {
        return null;
    }

    public static String getMetrics() {
        return METRIC_NAME;
    }

    @Override
    public String getMetricName() {
        return METRIC_NAME;
    }

    public static String getMetricQuery() {
        return METRIC_QUERY;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getSubtaskIndex() {
        return subtaskIndex;
    }

    public void setSubtaskIndex(Integer subtaskIndex) {
        this.subtaskIndex = subtaskIndex;
    }

    public static void setMetricName(String metricName) {
        METRIC_NAME = metricName;
    }
}
