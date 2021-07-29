package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("运行报告返回信息")
public class BatchScheduleJobExeStaticsResultVO {

    @ApiModelProperty(value = "任务类型 0 sql，1 mr' 2 sync", example = "1")
    private Integer taskType = 0;

    @ApiModelProperty(value = "cron数量", example = "1")
    private Integer cronExeNum = 0;

    @ApiModelProperty(value = "补数据数量", example = "1")
    private Integer fillDataExeNum = 0;

    @ApiModelProperty(value = "失败数量", example = "1")
    private Integer failNum = 0;

    @ApiModelProperty(value = "任务实例列表")
    private List<BatchJobInfoResultVO> jobInfoList = new ArrayList<>();
}
