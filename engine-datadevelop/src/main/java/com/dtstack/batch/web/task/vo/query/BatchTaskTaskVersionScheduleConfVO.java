package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhangwan
 */
@Data
@ApiModel("任务信息")
public class BatchTaskTaskVersionScheduleConfVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务版本 ID", example = "1", required = true)
    private Long versionId;

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;

}
