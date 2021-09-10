package com.dtstack.engine.common.param;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @Auther: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/17 11:28
 * @Description: SDK 基类
 */
@Data
@ToString
class BaseParam {
    /**
     * 通道 -- 默认是 RDOS_SDK
     */
    @ApiModelProperty(hidden = true)
    private String channel = "DAGScheduleX";

    /**
     * SDK 版本
     */
    @ApiModelProperty(hidden = true)
    private String sdkVersion = "v1.0";

    /**
     * 当前时间戳
     */
    @ApiModelProperty(hidden = true)
    private Long timestamp = System.currentTimeMillis();

    /**
     * 鉴权方式
     */
    @ApiModelProperty(hidden = true)
    private String signType = "DEFAULT";
}
