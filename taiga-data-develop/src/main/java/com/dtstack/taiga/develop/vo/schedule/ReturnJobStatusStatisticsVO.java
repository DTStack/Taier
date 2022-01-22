package com.dtstack.taiga.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/24 1:50 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnJobStatusStatisticsVO {
    @ApiModelProperty(value = "状态key",example = "FAILED")
    private String statusKey;

    @ApiModelProperty(value = "状态数量",example = "30")
    private Long count;

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
