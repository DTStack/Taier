package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskSetOwnerUserVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 拥有者 ID", example = "37", required = true)
    private Long ownerUserId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;
    
}
