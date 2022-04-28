package com.dtstack.taier.common.param;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * metric 指标详情信息
 *
 * @author ：wangchuan
 * date：Created in 上午11:55 2021/4/16
 * company: www.dtstack.com
 */
public class MetricResultVO {

    @ApiModelProperty(value = "指标相关信息", example = "{\"host\":\"127.0.0.1\"}")
    private Map<String, String> metric;

    @ApiModelProperty(value = "时间-指标集合", example = "{\"host\":\"127.0.0.1\"}")
    private List<MetricValueVO> values;

    public Map<String, String> getMetric() {
        return metric;
    }

    public List<MetricValueVO> getValues() {
        return values;
    }

    public void setMetric(Map<String, String> metric) {
        this.metric = metric;
    }

    public void setValues(List<MetricValueVO> values) {
        this.values = values;
    }
}
