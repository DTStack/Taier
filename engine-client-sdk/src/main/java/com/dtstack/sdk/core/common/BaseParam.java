package com.dtstack.sdk.core.common;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @Auther: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/17 11:28
 * @Description: SDK 基类
 */
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
class BaseParam {
    /**
     * 通道 -- 默认是 RDOS_SDK
     */
    @Setter
    @ApiModelProperty(hidden = true)
    private String channel = SdkAppType.Others.getName();

    /**
     * SDK 版本
     */
    @Setter
    @ApiModelProperty(hidden = true)
    private String sdkVersion = Constant.SDK_VERSION;

    /**
     * 当前时间戳
     */
    @ApiModelProperty(hidden = true)
    private Long timestamp = System.currentTimeMillis();

    /**
     * 鉴权方式
     */
    @Setter
    @ApiModelProperty(hidden = true)
    private String signType = SignType.DEFAULT.name();
}
