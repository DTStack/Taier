package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务操作信息")
public class BatchTaskRecordResultVO {

    @ApiModelProperty(value = "操作时间")
    private Long operateTime;

    @ApiModelProperty(value = "操作")
    private String operatorName;

    @ApiModelProperty(value = "操作类别")
    private String operateType;
}
