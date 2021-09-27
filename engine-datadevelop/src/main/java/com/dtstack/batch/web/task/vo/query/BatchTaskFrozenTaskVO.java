package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("任务调度信息")
public class BatchTaskFrozenTaskVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 ID list", required = true)
    private List<Long> taskIdList;

    @ApiModelProperty(value = "调度状态", example = "0", required = true)
    private Integer scheduleStatus = 0;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;
    
}
