package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务血缘关系信息")
public class BatchTaskTaskDisplayOffSpringVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "级别", example = "4", required = true)
    private Integer level;

    @ApiModelProperty(value = "类型", example = "2", required = true)
    private Integer type;

    @ApiModelProperty(value = "app类型", example = "1", required = true)
    private Integer appType;

}
