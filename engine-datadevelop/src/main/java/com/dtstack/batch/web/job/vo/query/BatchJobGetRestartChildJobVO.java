package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取重跑的数据节点信息")
public class BatchJobGetRestartChildJobVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "只有一个子结点", example = "false")
    private Boolean isOnlyNextChild = false;

    @ApiModelProperty(value = "实例key", example = "1", required = true)
    private String jobKey;

    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long taskId;
}
