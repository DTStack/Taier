package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.param.MetricPO;
import com.dtstack.taier.common.param.PromtheusMetrics;

import java.util.HashMap;
import java.util.Map;

import static com.dtstack.taier.common.metric.stream.DAGBackPressureMetrics.METRIC_QUERY;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-03 16:55
 * @Description:
 */
public class DAGNumRecordsInMetric extends DagMetric {

    private String operatorId;

    private Integer subtaskIndex;

    private static String METRIC_NAME = "flink_taskmanager_job_task_operator_numRecordsIn";

    @Override
    public Object formatData(String result) {
        Map<String, Object> metricMap = new HashMap<>();
        if (result == null) {
            return metricMap;
        }
        PromtheusMetrics promtheusMetrics = JSONObject.parseObject(result, PromtheusMetrics.class);
        for (MetricResult metricResult : promtheusMetrics.getData().getResult()) {
            MetricPO metricPO = metricResult.getMetric();
            metricMap.putIfAbsent(String.format(METRIC_QUERY, this.getMetricName(), metricPO.getOperatorId(), metricPO.getSubtaskIndex()), metricResult.getValue().get(1));
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
}
