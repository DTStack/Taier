package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("项目删除前检查任务的结果")
public class BatchProjectDelCheckTaskResultVO {

    @ApiModelProperty(value = "任务名", example = "dev")
    private String taskName;

    @ApiModelProperty(value = "任务名对应的被其他项目依赖的任务列表")
    private List<BatchProjectDelCheckParentResultVO> notDeleteTaskVOList;
}
