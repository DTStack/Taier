package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.DAGMetricType;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.param.MetricPO;
import com.dtstack.taier.common.param.PromtheusMetrics;

import java.util.HashMap;
import java.util.Map;

import static com.dtstack.taier.common.metric.stream.DAGBackPressureMetrics.METRIC_QUERY;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-11 20:10
 * @Description:
 */
public class DAGBytesOutMetric extends DagMetric{

    private static String METRIC_NAME = "flink_taskmanager_job_task_numBytesOut";


    @Override
    public Object formatData(String result) {
        Map<String,Object> metricMap = new HashMap<>();
        if (result == null) {
            return metricMap;
        }
        PromtheusMetrics promtheusMetrics = JSONObject.parseObject(result,PromtheusMetrics.class);
        for(MetricResult metricResult:promtheusMetrics.getData().getResult()){
            MetricPO metricPO = metricResult.getMetric();
            metricMap.putIfAbsent(String.format(METRIC_QUERY, DAGMetricType.BYTES_RECORDS_OUT.getMetricName(),metricPO.getTaskId(),metricPO.getSubtaskIndex()),metricResult.getValue().get(1));
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
