package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取组件版本号")
public class BatchTaskGetComponentVersionVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "任务类型")
    private Integer taskType;
}
