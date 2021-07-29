package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务hover信息")
public class BatchTaskHoverTaskVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private String taskId;

    @ApiModelProperty(value = "产品类型", example = "1", required = true)
    private Integer appType;
    
}
