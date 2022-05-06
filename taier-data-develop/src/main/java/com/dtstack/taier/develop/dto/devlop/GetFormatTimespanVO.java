package com.dtstack.taier.develop.dto.devlop;

import io.swagger.annotations.ApiModelProperty;

/**
 * 获取格式化后的时间跨度
 *
 * @author ：wangchuan
 * date：Created in 上午11:05 2021/4/16
 * company: www.dtstack.com
 */
public class GetFormatTimespanVO {

    @ApiModelProperty(value = "时间跨度", example = "1y1w1d1h1m1s", required = true)
    private String timespan;

    public String getTimespan() {
        return timespan;
    }

    public void setTimespan(String timespan) {
        this.timespan = timespan;
    }
}
