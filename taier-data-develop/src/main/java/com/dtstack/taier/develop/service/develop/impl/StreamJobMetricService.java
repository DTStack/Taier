package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETimeCarry;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.taier.common.metric.stream.StreamMetricBuilder;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.StreamTaskMetricDTO;
import com.dtstack.taier.develop.dto.devlop.TimespanVO;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.utils.TimeUtil;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StreamJobMetricService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamJobMetricService.class);
    @Autowired
    private JobService jobService;

    @Autowired
    private BatchTaskService taskService;
    @Autowired
    private BatchServerLogService serverLogService;

    private static Map<String,List<String>> chartMetricMap = new HashMap<>();

    private static final String TOPIC_LAG_112 = "flink_taskmanager_job_task_operator_flinkx_KafkaConsumer_topic_partition_lag";

    static {
        chartMetricMap.put("data_acquisition_rps",Arrays.asList("data_acquisition_input_rps","data_acquisition_output_rps"));
        chartMetricMap.put("data_acquisition_bps",Arrays.asList("data_acquisition_input_bps","data_acquisition_output_bps"));
        chartMetricMap.put("data_acquisition_record_sum",Arrays.asList("data_acquisition_input_record_sum","data_acquisition_output_record_sum"));
        chartMetricMap.put("data_acquisition_byte_sum",Arrays.asList("data_acquisition_input_byte_sum","data_acquisition_output_byte_sum"));
        chartMetricMap.put("dirtyErrors",Arrays.asList("nErrors","conversionErrors","duplicateErrors","nullErrors","otherErrors"));
    }

    public PrometheusMetricQuery buildPrometheusMetric(Long dtUicTenantId, String componentVersion) {
        Pair<String, String> prometheusHostAndPort = serverLogService.getPrometheusHostAndPort(dtUicTenantId, null, ComputeType.STREAM);
        if (prometheusHostAndPort == null){
            throw new RdosDefineException("promethues配置为空");
        }
        return new PrometheusMetricQuery(String.format("%s:%s", prometheusHostAndPort.getKey(), prometheusHostAndPort.getValue()));
    }


    /**
     * 获取任务指标
     *
     * @param metricDTO 请求实体类
     * @return 任务指标
     */
    public JSONArray getTaskMetrics(StreamTaskMetricDTO metricDTO){
        if (CollectionUtils.isEmpty(metricDTO.getChartNames())) {
            throw new RdosDefineException("chartName不能为空");
        }

        Task task = taskService.getBatchTaskById(metricDTO.getTaskId());

        TimespanVO formatTimespan = formatTimespan(metricDTO.getTimespan());
        if (!formatTimespan.getCorrect()) {
            throw new RdosDefineException(String.format("timespan format error: %s", formatTimespan.getMsg()));
        }
        Long span = formatTimespan.getSpan();
        long endTime = metricDTO.getEnd().getTime();
        long startTime = TimeUtil.getStartTime(endTime, span);
        String jobName = EScheduleJobType.DATA_ACQUISITION.getVal().equals(task.getTaskType()) ? task.getName() :  task.getName() + "_" + task.getId();
        String jobId = jobService.getScheduleJob(task.getJobId()).getEngineJobId();
        Long dtuicTenantId = task.getTenantId();
        PrometheusMetricQuery prometheusMetricQuery = buildPrometheusMetric(dtuicTenantId, task.getComponentVersion());

        JSONArray chartDatas = new JSONArray();
        for (String chartName : metricDTO.getChartNames()) {
            if (chartMetricMap.containsKey(chartName)) {
                List<JSONObject> metricDatas = new ArrayList<>();
                for (String metricName : chartMetricMap.get(chartName)) {
                    IMetric metric = StreamMetricBuilder.buildMetric(metricName, startTime, endTime, jobName, jobId, buildGranularity(span), prometheusMetricQuery, task.getComponentVersion());
                    if (metric != null) {
                        metricDatas.add((JSONObject) metric.getMetric());
                    }
                }

                chartDatas.add(StreamMetricBuilder.mergeMetric(metricDatas, chartName, buildGranularity(span)));
            } else {
                IMetric metric = StreamMetricBuilder.buildMetric(chartName, startTime, endTime, jobName, jobId, buildGranularity(span), prometheusMetricQuery, task.getComponentVersion());
                if (metric != null) {
                    chartDatas.add(metric.getMetric());
                }
            }
        }

        return chartDatas;
    }

    /**
     * 根据时间跨度构建时间粒度，最多返回 300 个点
     * @param timespan 时间跨度
     * @return 时间粒度
     */
    public String buildGranularity(Long timespan) {
        // 计算时间粒度，最多返回 300 个点
        long granularity = timespan / (300 * 1000);
        return (granularity < 1 ? 1 : granularity) + ETimeCarry.SECOND.getType();
    }

    /**
     * 格式化时间跨度
     *
     * @param timespan 时间跨度
     * @return 格式化结果
     */
    public TimespanVO formatTimespan(String timespan) {
        TimespanVO timespanVO = TimeUtil.formatTimespan(timespan);
        // 时间跨度不能超过2y
        if (timespanVO.getCorrect() && timespanVO.getSpan() > 2 * 1000L * ETimeCarry.YEAR.getConvertToSecond()) {
            timespanVO.setCorrect(false);
            timespanVO.setFormatResult(null);
            timespanVO.setMsg("timespan cannot be greater than 2y");
        }
        return timespanVO;
    }
}
