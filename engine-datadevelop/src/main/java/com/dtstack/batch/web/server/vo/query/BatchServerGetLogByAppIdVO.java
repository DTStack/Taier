package com.dtstack.batch.web.server.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据appId获取日志信息")
public class BatchServerGetLogByAppIdVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务实例ID",example = "1", required = true)
    private String jobId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务类型",example = "1", required = true)
    private Integer taskType;
}
