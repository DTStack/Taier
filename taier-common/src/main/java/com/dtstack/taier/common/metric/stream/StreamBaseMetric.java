package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.metric.MetricData;
import com.dtstack.taier.common.metric.MetricResult;
import com.dtstack.taier.common.metric.QueryInfo;
import com.dtstack.taier.common.metric.Tuple;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 */
public abstract class StreamBaseMetric implements IMetric {

    private String metricName;

    private long startTime;

    private long endTime;

    protected String jobName;

    protected String jobId;

    protected String granularity;

    private PrometheusMetricQuery prometheusMetricQuery;

    private String componentVersion;

    private static Map<String, String> metricNameMap = new HashMap<>();

    private final static Map<String, String> FLINK_112_METRIC_NAME_MAP = new HashMap<>();

    private static List<String> specialDealmetricName = new ArrayList<>();

    static {
        metricNameMap.put("fail_over_history", "flink_jobmanager_job_numRestarts");
        metricNameMap.put("data_delay", "flink_taskmanager_job_task_operator_dtEventDelay");
        metricNameMap.put("source_input_tps", "flink_taskmanager_job_task_operator_dtNumRecordsInRate");
        metricNameMap.put("sink_output_rps", "flink_taskmanager_job_task_operator_dtNumRecordsOutRate");
        metricNameMap.put("source_input_rps", "flink_taskmanager_job_task_operator_dtNumRecordsInResolveRate");
        metricNameMap.put("source_input_bps", "flink_taskmanager_job_task_operator_dtNumBytesInRate");
        metricNameMap.put("source_dirty_out", "flink_taskmanager_job_task_operator_dtNumDirtyRecordsOut");
        metricNameMap.put("source_dirty_data", "flink_taskmanager_job_task_operator_dtDirtyData");
        metricNameMap.put("data_discard_tps", "flink_taskmanager_job_task_operator_numLateRecordsDropped");
        metricNameMap.put("data_discard_count", "flink_taskmanager_job_task_operator_numLateRecordsDropped");
        metricNameMap.put("data_acquisition_input_rps", "flink_taskmanager_job_task_operator_flinkx_numReadPerSecond");
        metricNameMap.put("data_acquisition_output_rps", "flink_taskmanager_job_task_operator_flinkx_numWritePerSecond");
        metricNameMap.put("data_acquisition_input_bps", "flink_taskmanager_job_task_operator_flinkx_byteReadPerSecond");
        metricNameMap.put("data_acquisition_output_bps", "flink_taskmanager_job_task_operator_flinkx_byteWritePerSecond");
        metricNameMap.put("data_acquisition_input_record_sum", "flink_taskmanager_job_task_operator_flinkx_numRead");
        metricNameMap.put("data_acquisition_output_record_sum", "flink_taskmanager_job_task_operator_flinkx_numWrite");
        metricNameMap.put("data_acquisition_input_byte_sum", "flink_taskmanager_job_task_operator_flinkx_byteRead");
        metricNameMap.put("data_acquisition_output_byte_sum", "flink_taskmanager_job_task_operator_flinkx_byteWrite");
        metricNameMap.put("lastCheckpointDuration", "flink_jobmanager_job_lastCheckpointDuration");
        metricNameMap.put("nErrors", "flink_taskmanager_job_task_operator_flinkx_nErrors");
        metricNameMap.put("conversionErrors", "flink_taskmanager_job_task_operator_flinkx_conversionErrors");
        metricNameMap.put("duplicateErrors", "flink_taskmanager_job_task_operator_flinkx_duplicateErrors");
        metricNameMap.put("nullErrors", "flink_taskmanager_job_task_operator_flinkx_nullErrors");
        metricNameMap.put("otherErrors", "flink_taskmanager_job_task_operator_flinkx_otherErrors");
        // 添加原有所有参数
        FLINK_112_METRIC_NAME_MAP.putAll(metricNameMap);
        // 添加 flink1.12 改动参数
        FLINK_112_METRIC_NAME_MAP.put("source_input_tps", "flink_taskmanager_job_task_operator_flinkx_numReadPerSecond");
        FLINK_112_METRIC_NAME_MAP.put("source_input_rps", "flink_taskmanager_job_task_operator_flinkx_numReadPerSecond");
        FLINK_112_METRIC_NAME_MAP.put("sink_output_rps", "flink_taskmanager_job_task_operator_flinkx_numWritePerSecond");
        FLINK_112_METRIC_NAME_MAP.put("source_input_bps", "flink_taskmanager_job_task_operator_flinkx_byteReadPerSecond");

        specialDealmetricName.add("source_input_tps");
        specialDealmetricName.add("sink_output_rps");
        specialDealmetricName.add("source_input_rps");
        specialDealmetricName.add("source_input_bps");
        specialDealmetricName.add("source_dirty_out");
        specialDealmetricName.add("source_dirty_data");
    }


    protected abstract QueryInfo buildQueryInfo();

    @Override
    public String getChartName() {
        return null;
    }

    @Override
    public Object getMetric() {
        QueryInfo queryInfo = buildQueryInfo();
        // 区分 flink 版本
        String originMetricName = FLINK_112_METRIC_NAME_MAP.get(metricName);

        // 返回空数据
        if (originMetricName == null || queryInfo == null) {
            JSONObject metricData = new JSONObject();
            metricData.put("chartName", metricName);
            metricData.put("data", new JSONArray());
            return metricData;
        }
        if (granularity != null && StringUtils.isNotBlank(granularity)) {
            queryInfo.setGranularity(granularity);
        }


        MetricResult metricResult = prometheusMetricQuery.queryRange(originMetricName, startTime, endTime, queryInfo, getTagName());
        return formatData(metricResult, metricName);
    }

    private Object formatData(MetricResult metricResult, String metricName) {
        List<JSONObject> datas = new ArrayList<>();

        if (metricResult != null && CollectionUtils.isNotEmpty(metricResult.getMetricDataList())) {
            for (MetricData data : metricResult.getMetricDataList()) {
                JSONObject metricData = new JSONObject();
                metricData.put("chartName", metricName);
                JSONArray array = new JSONArray();

                String tagName = data.getTagName();
                if (specialDealmetricName.contains(metricName) && tagName != null) {
                    tagName = tagName.contains(":") ? tagName.substring(tagName.indexOf(":") + 1) : tagName;
                } else {
                    tagName = metricName;
                }

                tupleToJson(tagName, array, data.getDps());

                metricData.put("data", array);

                datas.add(metricData);
            }
        }

        return StreamMetricBuilder.mergeMetric(datas, metricName, this.granularity);
    }

    private void tupleToJson(String key, JSONArray formatData, List list) {
        JSONObject item;
        for (Object datum : list) {
            item = new JSONObject();
            item.put("time", ((Tuple<Long, Double>) datum).getOne());
            item.put(key, df.format(((Tuple<Long, Double>) datum).getTwo()));
            formatData.add(item);
        }
    }

    public String getTagName() {
        return null;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    protected void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    protected void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
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

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }
}
