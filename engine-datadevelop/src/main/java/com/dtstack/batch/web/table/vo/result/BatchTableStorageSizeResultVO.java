package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("表容量信息")
public class BatchTableStorageSizeResultVO{

    @ApiModelProperty(value = "项目 ID", example = "13")
    private Long projectId;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectname;

    @ApiModelProperty(value = "表 ID", example = "11")
    private Long tableId;

    @ApiModelProperty(value = "表 名称", example = "user")
    private String tableName;

    @ApiModelProperty(value = "表大小", example = "0")
    private Long size = 0L;

}