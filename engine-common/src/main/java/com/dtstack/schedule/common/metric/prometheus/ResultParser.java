package com.dtstack.schedule.common.metric.prometheus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.schedule.common.metric.MetricData;
import com.dtstack.schedule.common.metric.MetricResult;
import com.dtstack.schedule.common.metric.Tuple;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Reason:
 * Date: 2018/10/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ResultParser {

    public static MetricResult parseResult(String metricName, String response, String tagNameKey) {

        JSONObject json = JSON.parseObject(response);
        String queryStatus = json.getString("status");
        if (PrometheusConstants.RESPONSE_ERROR_STATUS.equalsIgnoreCase(queryStatus)) {
            throw new RuntimeException(json.getString("error"));
        }

        JSONObject dataNode = json.getJSONObject("data");
        String resultType = dataNode.getString("resultType");
        JSONArray resultArray = dataNode.getJSONArray("result");

        if (MetricResultType.MATRIX.getTypeInfo().equalsIgnoreCase(resultType)) {

            MetricResult metricResult = new MetricResult();
            List<MetricData> metricDataList = Lists.newArrayList();
            metricResult.setMetricDataList(metricDataList);
            metricResult.setMetricName(metricName);

            String finalTagNameKey = tagNameKey;
            resultArray.forEach(item -> {

                String tagName;
                if (Strings.isNullOrEmpty(finalTagNameKey)) {
                    tagName = metricName;
                } else {
                    tagName = ((JSONObject) item).getJSONObject("metric").getString(finalTagNameKey);
                }
                JSONArray details = ((JSONObject) item).getJSONArray("values");

                MetricData<Long, Double> metricData = new MetricData<>();
                List<Tuple<Long, Double>> dps = Lists.newArrayList();

                metricData.setTagName(tagName);
                metricData.setDps(dps);
                metricDataList.add(metricData);

                details.forEach(detail -> {
                    Integer timestamp = ((JSONArray) detail).getDouble(0).intValue();
                    String val = ((JSONArray) detail).getString(1);
                    Tuple<Long, Double> data = new Tuple<>(timestamp * 1000L, MathUtil.getDoubleVal(val));
                    dps.add(data);
                });
            });

            return metricResult;
        } else if (MetricResultType.VERTOR.getTypeInfo().equalsIgnoreCase(resultType)) {
            MetricResult metricResult = new MetricResult();
            List<MetricData> metricDataList = Lists.newArrayList();
            metricResult.setMetricDataList(metricDataList);
            metricResult.setMetricName(metricName);

            String finalTagNameKey = tagNameKey;
            resultArray.forEach(item -> {

                String tagName;
                if (Strings.isNullOrEmpty(finalTagNameKey)) {
                    tagName = metricName;
                } else {
                    tagName = ((JSONObject) item).getJSONObject("metric").getString(finalTagNameKey);
                }
                JSONArray details = ((JSONObject) item).getJSONArray("value");

                MetricData<Long, Double> metricData = new MetricData<>();
                List<Tuple<Long, Double>> dps = Lists.newArrayList();

                metricData.setTagName(tagName);
                metricData.setDps(dps);
                metricDataList.add(metricData);

                Integer timestamp = details.getDouble(0).intValue();
                String val = details.getString(1);
                Tuple<Long, Double> data = new Tuple<>(timestamp * 1000L, MathUtil.getDoubleVal(val));
                dps.add(data);
            });

            return metricResult;
        } else {
            throw new RuntimeException("not support MetricResultType:" + resultType);
        }
    }
}
