package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskQueryTasksVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务名称", example = "test", required = true)
    private String name;

    @ApiModelProperty(value = "任务拥有人 ID", example = "1")
    private Long ownerId;

    @ApiModelProperty(value = "开始时间", example = "176312314")
    private Long startTime;

    @ApiModelProperty(value = "截止时间", example = "176312314")
    private Long endTime;

    @ApiModelProperty(value = "调度状态", example = "1")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "任务类别 list", example = "")
    private String taskType;

    @ApiModelProperty(value = "周期类别 list", example = "0,1")
    private String taskPeriodId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "展示页面", example = "10", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "查询类型", example = "", required = true)
    private String searchType;
}
