package com.dtstack.taier.common.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * -
 *
 * @author ：wangchuan
 * date：Created in 上午11:57 2021/4/16
 * company: www.dtstack.com
 */
public class MetricValueVO {

    @ApiModelProperty(value = "点位时间戳信息", example = "1618478639000")
    private Long time;

    @ApiModelProperty(value = "点位指标", example = "1.10")
    private Double value;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
