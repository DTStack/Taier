package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("通过ID更新状态")
public class BatchJobUpdateStatusByIdVO extends DtInsightAuthParam {

     @ApiModelProperty(value = "任务实例Id", example = "1", required = true)
     private String jobId;

     @ApiModelProperty(value = "任务实例状态", example = "1", required = true)
     private Integer status;
}
