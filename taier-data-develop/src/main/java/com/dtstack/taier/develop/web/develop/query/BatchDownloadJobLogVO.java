package com.dtstack.taier.develop.web.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("下载job日志信息")
public class BatchDownloadJobLogVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "job id", example = "79275d9f", required = true)
    private String jobId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "行数", example = "1000")
    private Integer byteNum;

}
