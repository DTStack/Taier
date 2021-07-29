package com.dtstack.batch.web.dirtydata.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据同步任务信息")
public class BatchDirtyDataSyncTaskVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务名称", example = "task_name", required = true)
    private String taskName;
}
