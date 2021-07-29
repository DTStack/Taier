package com.dtstack.batch.web.dirtydata.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据同步任务结果信息")
public class BatchDirtyDataSyncTaskResultVO {
    @ApiModelProperty(value = "脏数据同步任务id", example = "1")
    private Long id;

    @ApiModelProperty(value = "脏数据任务名称", example = "task_name")
    private String taskName;
}