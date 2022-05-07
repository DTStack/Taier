package com.dtstack.taier.develop.service.develop.impl;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.DAGMetricType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.taier.common.metric.stream.DagMetric;
import com.dtstack.taier.common.metric.stream.prometheus.DAGPrometheusMetricQuery;
import com.dtstack.taier.common.thread.RdosThreadFactory;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopTaskMapper;
import com.dtstack.taier.develop.dto.devlop.FlinkTaskDTO;
import com.dtstack.taier.develop.dto.devlop.SubJobVerticeDTO;
import com.dtstack.taier.develop.dto.devlop.TaskVerticesDTO;
import com.dtstack.taier.develop.enums.develop.DAGShownType;
import com.dtstack.taier.develop.utils.develop.common.HdfsOperator;
import com.dtstack.taier.scheduler.service.ScheduleJobExpandService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class FlinkTaskVertexGraphService {

    private static final Integer CORE_POOL_NUMBER = 16;

    private static ExecutorService executorService = Executors.newFixedThreadPool(CORE_POOL_NUMBER, new RdosThreadFactory("stream_metrics_query"));


    @Autowired
    private DevelopTaskMapper developTaskMapper;

    @Autowired
    private StreamJobMetricService streamJobMetricService;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkTaskVertexGraphService.class);


    public static final String METRIC_QUERY = "%s-%s-%s";

    public static final String METRIC_QUERY_LATENCY = "%s-%s-%s-%s-%s";

    /**
     * 根据taskId查询任务的dag以及指标
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    public FlinkTaskDTO getTaskJson(Long taskId) {
        Task task = developTaskMapper.selectById(taskId);
        String dagJson = scheduleJobExpandService.getJobGraphJson(task.getJobId());
        if (StringUtils.isEmpty(dagJson)) {
            return new FlinkTaskDTO();
        }
        FlinkTaskDTO flinkTask = JSONObject.parseObject(dagJson, FlinkTaskDTO.class);
        sortJobGraph(flinkTask);
        PrometheusMetricQuery prometheusMetricQuery = streamJobMetricService.buildPrometheusMetric(task.getTenantId(), task.getComponentVersion());
        DAGPrometheusMetricQuery dagPrometheusMetricQuery = new DAGPrometheusMetricQuery(prometheusMetricQuery);
        //利用dag执行查询操作
        setMetricForJson(dagPrometheusMetricQuery, flinkTask);
        return flinkTask;
    }

    private void setMetricForJson(DAGPrometheusMetricQuery dagPrometheusMetricQuery, FlinkTaskDTO flinkTask) {
        Map<String, String> sourceMap = generateSourceMap(flinkTask);
        Map<String, Object> metricMap = null;
        try {
            metricMap = queryAllMetrics(flinkTask.getJobId(), dagPrometheusMetricQuery);
        } catch (Exception e) {
            LOGGER.error("获取metricMap异常:{}", e.getMessage(), e);
            throw new DtCenterDefException("获取metricMap异常", e);
        }
        LOGGER.info("flink job jobId {}, metric num is {}", flinkTask.getJobId(), metricMap.size());
        //开始组装参数
        for (TaskVerticesDTO taskVertice : flinkTask.getTaskVertices()) {
            for (int index = 0; index < taskVertice.getParallelism(); index++) {
                for (SubJobVerticeDTO subJobVertice : taskVertice.getSubJobVertices()) {
                    subJobVertice.setParallelism(taskVertice.getParallelism());
                    //汇总单个operator的输入输出
                    Long recordIn = MapUtils.getLong(metricMap, String.format(METRIC_QUERY, DAGMetricType.NUM_RECORDS_IN.getMetricName(), subJobVertice.getId(), index));
                    Long recordOut = MapUtils.getLong(metricMap, String.format(METRIC_QUERY, DAGMetricType.NUM_RECORDS_OUT.getMetricName(), subJobVertice.getId(), index));
                    subJobVertice.getRecordsReceivedMap().putIfAbsent(index, PublicUtil.getLongVal(recordIn));
                    subJobVertice.getRecordsSentMap().putIfAbsent(index, PublicUtil.getLongVal(recordOut));
                    for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
                        //获取单个operator 对于多个不同source的延迟信息
                        String key = String.format(METRIC_QUERY_LATENCY, DAGMetricType.LATENCY_MARKER.getMetricName(), index, DAGShownType.AVERAGE.getValue(), subJobVertice.getId(), entry.getKey());
                        Double latency = PublicUtil.formatDouble(MapUtils.getDouble(metricMap, key));
                        HashMap<String, Double> map = subJobVertice.getDelayMap().get(index);
                        if (map == null) {
                            map = new HashMap<>();
                        }
                        map.putIfAbsent(entry.getValue(), latency);
                        subJobVertice.getDelayMap().putIfAbsent(index, map);
                        subJobVertice.getDelayMapList().putIfAbsent(index + "-" + entry.getValue(), latency);
                    }

                }
                //获取输入输出的字节信息
                Long bytesIn = MapUtils.getLong(metricMap, String.format(METRIC_QUERY, DAGMetricType.BYTES_RECORDS_IN.getMetricName(), taskVertice.getJobVertexId(), index));
                Long bytesOut = MapUtils.getLong(metricMap, String.format(METRIC_QUERY, DAGMetricType.BYTES_RECORDS_OUT.getMetricName(), taskVertice.getJobVertexId(), index));
                //获取背压信息
                Double backPressure = PublicUtil.formatDouble(MapUtils.getDouble(metricMap, String.format(METRIC_QUERY, DAGMetricType.BACK_PRESSURE.getMetricName(), taskVertice.getJobVertexId(), index)));
                taskVertice.getBackPressureMap().putIfAbsent(index, backPressure);
                taskVertice.getInBytes().putIfAbsent(index, PublicUtil.getLongVal(bytesIn));
                taskVertice.getOutBytes().putIfAbsent(index, PublicUtil.getLongVal(bytesOut));
            }
        }
        //组装字节点参数到父节点用于前端展示
        formatMetrics(flinkTask, sourceMap);
    }


    /**
     * 组装参数
     *
     * @param flinkTask
     * @param sourceMap
     */
    private void formatMetrics(FlinkTaskDTO flinkTask, Map<String, String> sourceMap) {
        for (TaskVerticesDTO taskVertice : flinkTask.getTaskVertices()) {
            Long recordsIn = 0L;
            Long recordsOut = 0L;
            Long bytesIn = 0L;
            Long bytesOut = 0L;
            Map<String, Double> delayMap = taskVertice.getDelayMap();
            for (int index = 0; index < taskVertice.getParallelism(); index++) {
                //每个dag的输入输出为所有并行度第一个算子的输入之和
                Integer subJobSize = taskVertice.getSubJobVertices().size();
                bytesIn += taskVertice.getInBytes().get(index) == null ? 0 : taskVertice.getInBytes().get(index);
                bytesOut += taskVertice.getOutBytes().get(index) == null ? 0 : taskVertice.getOutBytes().get(index);
                Long subrecordsIn = taskVertice.getSubJobVertices().get(0).getRecordsSentMap().get(index) == null ? 0L : taskVertice.getSubJobVertices().get(0).getRecordsSentMap().get(index);
                Long subrecordsOut = taskVertice.getSubJobVertices().get(subJobSize - 1).getRecordsReceivedMap().get(index) == null ? 0L : taskVertice.getSubJobVertices().get(subJobSize - 1).getRecordsReceivedMap().get(index);
                recordsIn += subrecordsIn;
                recordsOut += subrecordsOut;
                //获取单个task对不同的soucre task的延迟汇总
                for (Map.Entry<String, String> entry : sourceMap.entrySet()) {
                    String key = entry.getKey() + "-" + entry.getValue();
                    if (delayMap.get(key) == null) {
                        delayMap.put(key, taskVertice.getSubJobVertices().get(subJobSize - 1).getDelayMap().get(index).get(key));
                    } else {
                        Double origin = delayMap.get(key) * (index);
                        Double newValue = (origin + taskVertice.getSubJobVertices().get(subJobSize - 1).getDelayMap().get(index).get(key)) / (index + 1);
                        delayMap.put(key, PublicUtil.formatDouble(newValue));
                    }
                }
            }
            taskVertice.setBytesReceived(HdfsOperator.unitConverter(bytesIn));
            taskVertice.setBytesSent(HdfsOperator.unitConverter(bytesOut));
            taskVertice.setRecordsReceived(recordsIn);
            taskVertice.setRecordsSent(recordsOut);
        }
    }

    /**
     * 查询所有的参数信息
     *
     * @param jobId
     * @param prometheusMetricQuery
     * @return
     * @throws Exception
     */
    public Map<String, Object> queryAllMetrics(String jobId, DAGPrometheusMetricQuery prometheusMetricQuery) throws Exception {
        LOGGER.info("flink task metric query start ,start time is {}", System.currentTimeMillis());
        Map<String, Object> metricMap = new HashMap<>();
        List<Service> services = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(DAGMetricType.values().length);
        for (DAGMetricType dagMetricType : DAGMetricType.values()) {
            DagMetric dagMetric = (DagMetric) dagMetricType.getaClass().newInstance();
            dagMetric.setJobId(jobId);
            dagMetric.setPrometheusMetricQuery(prometheusMetricQuery);
            Service service = new Service(latch, dagMetric);
            services.add(service);
        }
        List<Future<?>> futureList = Lists.newArrayList();
        for (Service service : services) {
            Future<Object> future = executorService.submit(service);
            futureList.add(future);
        }
        latch.await();
        LOGGER.info("flink task metric query end ,end time is {}", System.currentTimeMillis());
        for (Future<?> future : futureList) {
            metricMap.putAll((Map<String, Object>) future.get());
        }
        return metricMap;
    }

    static class Service implements Callable {
        private CountDownLatch latch;
        private DagMetric dagMetric;

        public Service(CountDownLatch latch, DagMetric dagMetric) {
            this.latch = latch;
            this.dagMetric = dagMetric;
        }

        @Override
        public Object call() {
            try {
                return dagMetric.getMetric();
            } finally {
                if (latch != null) {
                    latch.countDown();
                }
            }
        }

    }


    /**
     * 获取任务中的source task
     *
     * @param flinkTask
     * @return
     */
    private Map<String, String> generateSourceMap(FlinkTaskDTO flinkTask) {
        Map<String, String> sourceMap = new HashMap<>();
        flinkTask.getTaskVertices().forEach(taskVertices -> {
            if (CollectionUtils.isEmpty(taskVertices.getInputs())) {
                sourceMap.putIfAbsent(taskVertices.getJobVertexId(), taskVertices.getSubJobVertices().get(0).getName());
            }
        });
        return sourceMap;
    }


    private void sortJobGraph(FlinkTaskDTO flinkTask) {
        int dagSize = flinkTask.getTaskVertices().size();
        Set<String> sourceOutputs = new HashSet<>();
        int index = 0;
        for (TaskVerticesDTO taskVerticesDTO : flinkTask.getTaskVertices()) {
            if (CollectionUtils.isEmpty(taskVerticesDTO.getInputs())) {
                taskVerticesDTO.setIndexLevel(index);
                dagSize--;
                sourceOutputs.addAll(taskVerticesDTO.getOutput());
            }
        }
        index++;
        while (dagSize > 0) {
            Set<String> sourceOutputsNew = new HashSet<>();
            for (TaskVerticesDTO taskVerticesDTO : flinkTask.getTaskVertices()) {
                if ((!CollectionUtils.isEmpty(taskVerticesDTO.getInputs())) && (taskVerticesDTO.getIndexLevel() == null)) {
                    Set<String> sourceInputs = new HashSet<>();
                    sourceInputs.addAll(taskVerticesDTO.getInputs());
                    if (PublicUtil.checkIntersection(sourceOutputs, sourceInputs)) {
                        taskVerticesDTO.setIndexLevel(index);
                        dagSize--;
                        sourceOutputsNew.addAll(taskVerticesDTO.getOutput());
                    }
                }
            }
            index++;
            sourceOutputs = sourceOutputsNew;
        }
        Collections.sort(flinkTask.getTaskVertices(), new Comparator<TaskVerticesDTO>() {
            @Override
            public int compare(TaskVerticesDTO o1, TaskVerticesDTO o2) {
                return o1.getIndexLevel() - o2.getIndexLevel();
            }
        });
    }


}
