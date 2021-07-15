package com.dtstack.batch.web.datamask.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏管理-表列表结果信息")
public class BatchDataMaskConfigTableListResultVO {
    @ApiModelProperty(value = "表信息id", example = "1")
    private Long id;

    @ApiModelProperty(value = "表名称", example = "test")
    private String tableName;

    @ApiModelProperty(value = "表类型", example = "1s")
    private Integer tableType;
}
