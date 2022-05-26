package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EMetricTag;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETimeCarry;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.taier.common.metric.stream.CustomMetric;
import com.dtstack.taier.common.metric.stream.StreamMetricBuilder;
import com.dtstack.taier.common.metric.stream.prometheus.CustomPrometheusMetricQuery;
import com.dtstack.taier.common.metric.stream.prometheus.ICustomMetricQuery;
import com.dtstack.taier.common.param.MetricResultVO;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.StreamMetricSupport;
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
import java.util.Objects;

@Service
public class StreamJobMetricService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamJobMetricService.class);
    @Autowired
    private JobService jobService;

    @Autowired
    private BatchTaskService taskService;
    @Autowired
    private BatchServerLogService serverLogService;

    @Autowired
    private StreamMetricSupportService streamMetricSupportService;

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
     * 根据任务类型获取支持的 prometheus 指标
     *
     * @param taskId 任务id
     * @return 指标 key 集合
     */
    public List<String> getMetricsByTaskType(Long taskId) {
        Task streamTask = taskService.getOne(taskId);
        List<String> metric = streamMetricSupportService.getMetricKeyByType(streamTask.getTaskType(), streamTask.getComponentVersion());
        // 公共的 key，数据库暂时只维护一份 1.10 的指标
        List<String> commonMetric = streamMetricSupportService.getMetricKeyByType(99, "1.12");
        metric.addAll(commonMetric);
        return metric;
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
        ScheduleJob scheduleJob = jobService.getScheduleJob(task.getJobId());
        JSONArray chartDatas = new JSONArray();
        if(Objects.isNull(scheduleJob)) {
            return chartDatas;
        }
        String jobId = scheduleJob.getEngineJobId();
        Long dtuicTenantId = task.getTenantId();
        PrometheusMetricQuery prometheusMetricQuery = buildPrometheusMetric(dtuicTenantId, task.getComponentVersion());
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

    /**
     * 查询指定指标信息
     *
     * @param dtUicTenantId UIC 租户 id
     * @param taskId        任务id
     * @param end           结束时间
     * @param timespan      时间跨度
     * @param chartName     指标key
     * @return 指标详细信息
     */
    public List<MetricResultVO> queryTaskMetrics(Long dtUicTenantId, Long taskId, Long end, String timespan, String chartName) {
        Task streamTask = taskService.getOne(taskId);
        StreamMetricSupport metric = streamMetricSupportService.getMetricByValue(chartName, streamTask.getComponentVersion());
        EMetricTag metricTag = EMetricTag.getByTagVal(metric.getMetricTag());
        String tagValue;
        if (metricTag.equals(EMetricTag.JOB_ID)) {
            // 任务由 engine 提交上去后的 任务id
            tagValue = jobService.getScheduleJob(streamTask.getJobId()).getEngineJobId();
        } else {
            tagValue = streamTask.getJobId();
        }
        Pair<String, String> prometheusHostAndPort = serverLogService.getPrometheusHostAndPort(dtUicTenantId, null, ComputeType.STREAM);
        if (prometheusHostAndPort == null){
            throw new RdosDefineException("promethues配置为空");
        }
        ICustomMetricQuery<List<MetricResultVO>> prometheusMetricQuery = new CustomPrometheusMetricQuery<>(String.format("%s:%s", prometheusHostAndPort.getKey(), prometheusHostAndPort.getValue()));
        TimespanVO formatTimespan = formatTimespan(timespan);
        if (!formatTimespan.getCorrect()) {
            throw new RdosDefineException(String.format("timespan format error: %s", formatTimespan.getMsg()));
        }
        Long span = formatTimespan.getSpan();
        long startTime = end - span;
        CustomMetric<List<MetricResultVO>> listCustomMetric = CustomMetric.buildCustomMetric(chartName, startTime, end, metricTag, tagValue, buildGranularity(span), prometheusMetricQuery);
        return listCustomMetric.getMetric(Integer.MAX_VALUE);
    }
}
