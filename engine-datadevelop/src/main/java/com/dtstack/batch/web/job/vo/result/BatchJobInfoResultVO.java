package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务实例详情")
public class BatchJobInfoResultVO {
    @ApiModelProperty(value = "任务实例Id", example = "bd0619ba")
    private String jobId;

    @ApiModelProperty(value = "开始时间", example = "1525942614000")
    private Long exeStartTime;

    @ApiModelProperty(value = "时间", example = "1525942614000")
    private Integer exeTime;

    @ApiModelProperty(value = "总数", example = "1")
    private Integer totalCount;

    @ApiModelProperty(value = "脏数据数量", example = "1")
    private Integer dirtyNum;
}
