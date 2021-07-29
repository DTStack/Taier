package com.dtstack.sdk.core.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/17 11:36
 * @Description: 带鉴权 token 基类
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class DtInsightAuthParam extends BaseParam {
    /**
     * token 鉴权信息，如果填写会覆盖掉注册的 token 信息
     */
    @ApiModelProperty(hidden = true)
    private String token;

    /**
     * 自定义的签名方式
     */
    protected void calSign(){

    }
}
