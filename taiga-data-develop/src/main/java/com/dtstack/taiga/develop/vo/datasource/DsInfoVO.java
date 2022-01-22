package com.dtstack.taiga.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SLOGAN:改变世界！改变未来！
 *
 * @author tz
 * @Description:
 * @Date: 2021/12/30 11:19
 */
@Data
@ApiModel("数据源列表信息")
public class DsInfoVO {

    @ApiModelProperty("数据源Id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源名称", example = "mysql")
    private String dataName;

    @ApiModelProperty(value = "数据源类型枚举")
    private Integer dataTypeCode;
}
