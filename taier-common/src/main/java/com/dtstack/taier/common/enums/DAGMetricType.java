package com.dtstack.taier.common.enums;


import com.dtstack.taier.common.metric.stream.DAGBackPressureMetrics;
import com.dtstack.taier.common.metric.stream.DAGBytesInMetric;
import com.dtstack.taier.common.metric.stream.DAGBytesOutMetric;
import com.dtstack.taier.common.metric.stream.DAGNumRecordsInMetric;
import com.dtstack.taier.common.metric.stream.DAGNumRecordsOutMetric;
import com.dtstack.taier.common.metric.stream.DagLatencyMarkerMetric;

/**
 * @company:www.dtstack.com
 * @Author:shiFang
 * @Date:2020-09-03 20:15
 * @Description:
 */
public enum DAGMetricType {
    /**
     * 延迟标记
     */
    LATENCY_MARKER(0, DagLatencyMarkerMetric.class, DagLatencyMarkerMetric.getMetrics()),

    /**
     * 反压
     */
    BACK_PRESSURE(1, DAGBackPressureMetrics.class, DAGBackPressureMetrics.getMetrics()),

    /**
     * 读数量条数
     */
    NUM_RECORDS_IN(2, DAGNumRecordsInMetric.class, DAGNumRecordsInMetric.getMetrics()),

    /**
     * 写数量条数
     */
    NUM_RECORDS_OUT(3, DAGNumRecordsOutMetric.class, DAGNumRecordsOutMetric.getMetrics()),

    /**
     * 读字节数
     */
    BYTES_RECORDS_IN(4, DAGBytesInMetric.class, DAGBytesInMetric.getMetrics()),

    /**
     * 写字节数
     */
    BYTES_RECORDS_OUT(5, DAGBytesOutMetric.class, DAGBytesOutMetric.getMetrics());

    private String metricName;

    private Integer type;

    private Class<?> aClass;

    public Integer getType() {
        return type;
    }

    public Class<?> getaClass() {
        return aClass;
    }


    public String getMetricName() {
        return metricName;
    }

    DAGMetricType(Integer type, Class<?> aClass, String metricName) {
        this.type = type;
        this.aClass = aClass;
        this.metricName = metricName;
    }
}
