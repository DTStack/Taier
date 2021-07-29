package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("整库同步-任务同步结果信息")
public class BatchDataSourceMigrationTaskProcessResultVO {
    @ApiModelProperty(value = "成功", example = "1")
    private Long success;

    @ApiModelProperty(value = "失败", example = "0")
    private Long failed;
}