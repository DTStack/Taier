package com.dtstack.batch.web.datasource.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("整库同步字段转换信息")
public class BatchDataSourceMigrationTransformFieldVO {
    @ApiModelProperty(value = "目标字段", example = "id")
    private String convertDest;

    @ApiModelProperty(value = "源字段", example = "id")
    private String convertSrc;

    @ApiModelProperty(value = "转换类型 1表名 2字段名 3段类型", example = "id")
    private Integer convertType;

    @ApiModelProperty(value = "转换方式 1字符替换 2添加前缀 3添加后缀", example = "1")
    private Integer convertObject;
}
