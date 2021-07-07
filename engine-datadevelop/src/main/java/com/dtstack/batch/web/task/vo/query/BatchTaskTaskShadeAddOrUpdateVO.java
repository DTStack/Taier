package com.dtstack.batch.web.task.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务血缘关系信息")
public class BatchTaskTaskShadeAddOrUpdateVO {

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "级别", example = "3", required = true)
    private Integer level;

    @ApiModelProperty(value = "类别", example = "1", required = true)
    private Integer directType;

}
