package com.dtstack.taier.common.metric.stream.prometheus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.metric.prometheus.MetricResultType;
import com.dtstack.taier.common.metric.prometheus.PrometheusConstants;
import com.dtstack.taier.common.param.MetricResultVO;
import com.dtstack.taier.common.param.MetricValueVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义 prometheus 结果解析器
 *
 * @author ：wangchuan
 * date：Created in 下午2:16 2021/4/16
 * company: www.dtstack.com
 */
public class CustomResultParser {

    /**
     * 解析 prometheus 返回值信息
     *
     * @param result   prometheus response
     * @param maxPoint 最大点位
     * @return 点阵合集
     */
    public static List<MetricResultVO> parseResult(String result, Integer maxPoint) {
        JSONObject jsonResult;
        try {
            jsonResult = JSON.parseObject(result);
        } catch (Exception e) {
            throw new RdosDefineException("prometheus response is not json format...");
        }
        String queryStatus = jsonResult.getString("status");
        if (PrometheusConstants.RESPONSE_ERROR_STATUS.equalsIgnoreCase(queryStatus)) {
            throw new RdosDefineException(String.format("prometheus response error: %s", jsonResult.getString("error")));
        }
        JSONObject dataNode = jsonResult.getJSONObject("data");
        String resultType = dataNode.getString("resultType");
        JSONArray resultJsonArray = dataNode.getJSONArray("result");
        if (MetricResultType.MATRIX.getTypeInfo().equalsIgnoreCase(resultType)) {
            List<MetricResultVO> metricResultVOS = Lists.newArrayList();
            for (int i = 0; i < resultJsonArray.size(); i++) {
                JSONObject singleMetric = resultJsonArray.getJSONObject(i);
                if (Objects.isNull(singleMetric)) {
                    continue;
                }
                MetricResultVO metricResultVO = buildMetricResultVO(singleMetric);
                JSONArray values = singleMetric.getJSONArray("values");
                List<MetricValueVO> metricValueVOS = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(values)) {
                    for (int j = 0; j < values.size(); j++) {
                        JSONArray valueArray = values.getJSONArray(j);
                        if (CollectionUtils.isNotEmpty(valueArray)) {
                            metricValueVOS.add(buildMetricValueVO(valueArray));
                        }
                    }
                }
                if (Objects.nonNull(maxPoint)) {
                    // 只取指定点位数量，从后往前取
                    metricValueVOS = metricValueVOS.subList(Math.max(metricValueVOS.size() - maxPoint, 0), metricValueVOS.size());
                }
                metricResultVO.setValues(metricValueVOS);
                metricResultVOS.add(metricResultVO);
            }
            return metricResultVOS;
        } else if (MetricResultType.VERTOR.getTypeInfo().equalsIgnoreCase(resultType)) {
            List<MetricResultVO> metricResultVOS = Lists.newArrayList();
            for (int i = 0; i < resultJsonArray.size(); i++) {
                JSONObject singleMetric = resultJsonArray.getJSONObject(i);
                if (Objects.isNull(singleMetric)) {
                    continue;
                }
                MetricResultVO metricResultVO = buildMetricResultVO(singleMetric);
                JSONArray values = singleMetric.getJSONArray("value");
                List<MetricValueVO> metricValueVOS = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(values)) {
                    metricValueVOS.add(buildMetricValueVO(values));
                }
                metricResultVO.setValues(metricValueVOS);
                metricResultVOS.add(metricResultVO);
            }
            return metricResultVOS;
        } else {
            throw new RdosDefineException("not support MetricResultType:" + resultType);
        }
    }

    /**
     * 构建单条 prometheus 点阵
     *
     * @param singleMetric prometheus 单个点阵信息
     * @return 单条线 点阵信息
     */
    private static MetricResultVO buildMetricResultVO(JSONObject singleMetric) {
        JSONObject metric = singleMetric.getJSONObject("metric");
        // json -> map
        Map<String, String> metricMap = JSONObject.parseObject(metric.toJSONString(), new TypeReference<Map<String, String>>() {
        });
        MetricResultVO metricResultVO = new MetricResultVO();
        metricResultVO.setMetric(metricMap);
        return metricResultVO;
    }

    /**
     * 构建单个 prometheus 点位信息
     *
     * @param value prometheus response value
     * @return 单个点位指标指标
     */
    private static MetricValueVO buildMetricValueVO(JSONArray value) {
        // 去掉 prometheus 返回时间精度 精确到秒
        long time = value.getDouble(0).intValue() * 1000L;
        String metricValueString = value.getString(1);
        // prometheus 指标
        Double metricValueDouble = StringUtils.isBlank(metricValueString) ? 0L : Double.parseDouble(metricValueString);
        MetricValueVO metricValueVO = new MetricValueVO();
        metricValueVO.setTime(time);
        metricValueVO.setValue(metricValueDouble);
        return metricValueVO;
    }
}
