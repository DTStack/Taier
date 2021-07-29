package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表字段信息")
public class BatchTaskGetChildTasksVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务ID", example = "1")
    private Long taskId;

}
