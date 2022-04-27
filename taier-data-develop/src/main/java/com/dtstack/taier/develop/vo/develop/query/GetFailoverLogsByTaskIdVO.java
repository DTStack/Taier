package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

public class GetFailoverLogsByTaskIdVO extends DtInsightAuthParam {

    @NotNull(message = "taskId not null")
    @ApiModelProperty(value = "任务ID", example = "111", required = true)
    private Long taskId;
}
