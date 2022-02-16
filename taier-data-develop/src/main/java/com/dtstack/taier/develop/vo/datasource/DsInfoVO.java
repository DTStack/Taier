package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * SLOGAN:改变世界！改变未来！
 *
 * @author tz
 * @Description:
 * @Date: 2021/12/30 11:19
 */
@ApiModel("数据源列表信息")
public class DsInfoVO {

    @ApiModelProperty("数据源Id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源名称", example = "mysql")
    private String dataName;

    @ApiModelProperty(value = "数据源类型枚举")
    private Integer dataTypeCode;

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(Integer dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }
}
