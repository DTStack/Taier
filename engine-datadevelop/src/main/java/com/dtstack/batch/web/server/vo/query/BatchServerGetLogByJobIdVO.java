package com.dtstack.batch.web.server.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据jobId获取日志信息")
public class BatchServerGetLogByJobIdVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务实例ID",example = "1", required = true)
    private String jobId;

    @ApiModelProperty(value = "页数",example = "1", required = true)
    private Integer pageInfo;
}
