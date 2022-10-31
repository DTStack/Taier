/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.metric.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.metric.batch.BaseMetric;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * metric builder
 * @author jiangbo
 */
public class StreamMetricBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StreamMetricBuilder.class);

    private static Map<String,String> metricNameMap = new HashMap<>();

    private static List<String> cumulativeMetricName = new ArrayList<>(4);

    private static final Long GRANULARITY_BASIC_UNIT = 1000L;

    private static Map<String, Long> granularityMap = new HashMap<>(5);

    private static final String SEPARATED = "separated";

    private static final String UNIT = "unit";

    private static final Pattern GRANULARITY_PATTERN = Pattern.compile("(?i)(?<" + SEPARATED + ">\\d+)(?<" + UNIT + ">[a-z]+)");

    static {
        granularityMap.put("s", GRANULARITY_BASIC_UNIT);
        granularityMap.put("m", 60 * GRANULARITY_BASIC_UNIT);
        granularityMap.put("h", 60 * 60 * GRANULARITY_BASIC_UNIT);
        granularityMap.put("d", 24 * 60 * 60 * GRANULARITY_BASIC_UNIT);
        granularityMap.put("w", 7 * 24 * 60 * 60 * GRANULARITY_BASIC_UNIT);

        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_numReadPerSecond","data_acquisition_input_rps");
        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_numWritePerSecond","data_acquisition_output_rps");
        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_byteReadPerSecond","data_acquisition_input_bps");
        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_byteWritePerSecond","data_acquisition_output_bps");
        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_numRead","data_acquisition_input_record_sum");
        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_numWrite","data_acquisition_output_record_sum");
        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_byteRead","data_acquisition_input_byte_sum");
        metricNameMap.put("flink_taskmanager_job_task_operator_chunjun_byteWrite","data_acquisition_output_byte_sum");

        cumulativeMetricName.add("data_acquisition_record_sum");
        cumulativeMetricName.add("data_acquisition_byte_sum");
        cumulativeMetricName.add("data_acquisition_input_record_sum");
        cumulativeMetricName.add("data_acquisition_output_record_sum");
        cumulativeMetricName.add("data_acquisition_input_byte_sum");
        cumulativeMetricName.add("data_acquisition_output_byte_sum");
    }

    public static IMetric buildMetric(String metricName, long startTime, long endTime, String jobName, String jobId,
                                      String granularity, PrometheusMetricQuery prometheusMetricQuery, String componentVersion){
        StreamBaseMetric metric = null;
        switch (metricName){
            case "fail_over_history" : metric = new FailOverHistoryMetric(); break;
            case "data_delay" : metric = new BizDataDelayMetric(); break;
            case "source_input_tps" : metric = new SourceMetric(); break;
            case "sink_output_rps" : metric = new SinkOutputRPSMetric(); break;
            case "source_input_rps" : metric = new SourceMetric(); break;
            case "source_input_bps" : metric = new SourceMetric(); break;
            case "source_dirty_out" : metric = new SourceDirtyDataOutMetric(); break;
            case "source_dirty_data" : metric = new SourceDirtyDataMetric(); break;
            case "data_discard_tps" : metric = new DataDiscardTPSMetric(); break;
            case "data_discard_count" : metric = new DataDiscardCountMetric(); break;
            case "data_acquisition_input_rps" : metric = new DataAcquisitionMetric(); break;
            case "data_acquisition_output_rps" : metric = new DataAcquisitionMetric(); break;
            case "data_acquisition_input_bps" : metric = new DataAcquisitionMetric(); break;
            case "data_acquisition_output_bps" : metric = new DataAcquisitionMetric(); break;
            case "data_acquisition_input_record_sum" : metric = new DataAcquisitionMetric(); break;
            case "data_acquisition_output_record_sum" : metric = new DataAcquisitionMetric(); break;
            case "data_acquisition_input_byte_sum" : metric = new DataAcquisitionMetric(); break;
            case "data_acquisition_output_byte_sum" : metric = new DataAcquisitionMetric(); break;
            case "lastCheckpointDuration" : metric = new CheckPointsHistoryMetric(); break;
            case "nErrors" : metric = new CheckPointsHistoryMetric(); break;
            case "conversionErrors" : metric = new CheckPointsHistoryMetric(); break;
            case "otherErrors" : metric = new CheckPointsHistoryMetric(); break;
            case "duplicateErrors" : metric = new CheckPointsHistoryMetric(); break;
            case "nullErrors" : metric = new CheckPointsHistoryMetric(); break;
            default: break;
        }

        if (metric != null){
            metric.setMetricName(metricName);
            metric.setStartTime(startTime);
            metric.setEndTime(endTime);
            metric.setJobName(jobName);
            metric.setJobId(jobId);
            metric.setGranularity(granularity);
            metric.setPrometheusMetricQuery(prometheusMetricQuery);
            metric.setComponentVersion(componentVersion);
        }

        return metric;
    }

    /**
     * 合并指标到一个图中
     */
    public static JSONObject mergeMetric(List<JSONObject> metricDatas,String chartName, String granularity){
        JSONObject chartData = new JSONObject();
        chartData.put("chartName",chartName);

        JSONArray data = new JSONArray();

        Set<Long> timeSet = new HashSet<>();
        for (JSONObject metricData : metricDatas) {
            JSONArray currentData = metricData.getJSONArray("data");
            for (int i = 0; i < currentData.size(); i++) {
                timeSet.add(currentData.getJSONObject(i).getLong("time"));
            }
        }

        List<Long> timeList = new ArrayList<>(timeSet);
        Collections.sort(timeList);
        dealSeparatedTime(timeList, granularity);

        if(CollectionUtils.isNotEmpty(metricDatas)){
            for (Long time : timeList) {
                JSONObject item = new JSONObject();
                for (JSONObject metricData : metricDatas) {
                    JSONArray currentData = metricData.getJSONArray("data");
                    for (Object currentDatum : currentData) {
                        if (((JSONObject) currentDatum).getLong("time").equals(time)) {
                            item.putAll((JSONObject) currentDatum);
                            break;
                        }
                    }
                }

                Set<String> keys = new HashSet<>(item.keySet());
                for (String key : keys) {
                    item.put(metricNameMap.getOrDefault(key,key),item.get(key));
                    if(metricNameMap.containsKey(key)){
                        item.remove(key);
                    }
                }

                item.put("time",time);
                data.add(item);
            }
        }

        dealSeparatedData(data, cumulativeMetricName.contains(chartName));
        chartData.put("data", data);
        return chartData;
    }

    /**
     * 补充 Promethus 异常导致点消失的情况
     *
     * @param timeList
     * @return
     */
    private static void dealSeparatedTime(List<Long> timeList, String granularity) {
        Matcher match = GRANULARITY_PATTERN.matcher(granularity.trim());
        long seperated = 20 * granularityMap.get("s");
        try {
            if (match.find()) {
                seperated = Long.valueOf(match.group(SEPARATED)) * granularityMap.get(match.group(UNIT).toLowerCase());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        for (int i = 1; i < timeList.size(); i++) {
            if (timeList.get(i - 1) + seperated == timeList.get(i)) {
                continue;
            }
            timeList.add(timeList.get(i - 1) + seperated);
            Collections.sort(timeList);
        }
    }

    /**
     * 补充 Promethus 异常导致点消失的情况
     *
     * @param data
     * @param isCumulative 是否累计值
     * @return
     */
    private static void dealSeparatedData(JSONArray data, Boolean isCumulative) {
        Map<String, Integer> preIndex = new HashMap<>();
        Map<String, Integer> endIndex = new HashMap<>();
        for (int i = 0, length = data.size(); i < length; i++) {
            JSONObject index = data.getJSONObject(i);
            Set<String> indexKeys = index.keySet();

            int finalI = i;
            indexKeys.forEach(key -> {
                if (preIndex.containsKey(key)) {
                    endIndex.put(key, finalI);
                    return;
                }
                preIndex.put(key, finalI);
            });
        }

        preIndex.remove("time");
        for (Map.Entry<String, Integer> entry : preIndex.entrySet()) {
            doCompareSeparatedKeys(data, isCumulative, entry.getKey(), preIndex.get(entry.getKey()), endIndex.get(entry.getKey()));
        }
    }

    private static void doCompareSeparatedKeys(JSONArray data, Boolean isCumulative, String key, Integer preIndex, Integer endIndex) {
        if (null == endIndex) {
            endIndex = data.size() - 1;
        }

        for (int i = preIndex; i <= endIndex; i++) {
            if (null != data.getJSONObject(i).get(key)) {
                continue;
            }
            if (i == 0 || !isCumulative) {
                data.getJSONObject(i).put(key, "0");
                continue;
            }

            data.getJSONObject(i).put(key, data.getJSONObject(i - 1).get(key));
        }
    }
}
