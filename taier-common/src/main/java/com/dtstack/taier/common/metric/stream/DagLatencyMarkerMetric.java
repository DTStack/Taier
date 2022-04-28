package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.param.MetricPO;
import com.dtstack.taier.common.param.PromtheusMetrics;

import java.util.HashMap;
import java.util.Map;

import static com.dtstack.taier.common.metric.stream.DAGBackPressureMetrics.METRIC_QUERY_LATENCY;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-03 15:36
 * @Description:
 */

public class DagLatencyMarkerMetric extends DagMetric{

    private String operatorId;

    private String sourceId;

    private String sourceName;

    private Double quantile;

    private Integer operatorSubtaskIndex;

    private static String METRIC_NAME = "flink_taskmanager_job_latency_source_id_operator_id_operator_subtask_index_latency";

    @Override
    public Object formatData(String result) {
        Map<String,Object> metricMap = new HashMap<>();
        if (result == null) {
            return metricMap;
        }
        PromtheusMetrics promtheusMetrics = JSONObject.parseObject(result,PromtheusMetrics.class);
        for(MetricResult metricResult:promtheusMetrics.getData().getResult()){
            MetricPO metricPO = metricResult.getMetric();
            metricMap.putIfAbsent(String.format(METRIC_QUERY_LATENCY,this.getMetricName(),metricPO.getOperatorSubtaskIndex(),metricPO.getQuantile(),metricPO.getOperatorId(),metricPO.getSourceId()),metricResult.getValue().get(1));
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
