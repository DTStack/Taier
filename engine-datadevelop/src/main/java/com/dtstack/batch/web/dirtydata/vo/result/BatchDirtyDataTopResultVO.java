package com.dtstack.batch.web.dirtydata.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据top结果信息")
public class BatchDirtyDataTopResultVO {
    @ApiModelProperty(value = "脏数据任务名称", example = "task_name")
    private String taskName;

    @ApiModelProperty(value = "脏数据表名称", example = "table_name")
    private String tableName;

    @ApiModelProperty(value = "总数量", example = "99")
    private Long totalNum;

    @ApiModelProperty(value = "最大数", example = "99")
    private Long maxNum;

    @ApiModelProperty(value = "最近数", example = "1")
    private Long recentNum;
}