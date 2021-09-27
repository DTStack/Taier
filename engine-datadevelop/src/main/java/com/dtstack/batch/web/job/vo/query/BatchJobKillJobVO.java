package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("杀死任务实例信息")
public class BatchJobKillJobVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "业务开始日期", example = "1609084800", required = true)
    private Long bizStartDay;

    @ApiModelProperty(value = "业务结束日期", example = "1609084800", required = true)
    private Long bizEndDay;

    @ApiModelProperty(value = "类型", example = "0周期任务；1补数据实例", required = true)
    private Integer type;

    @ApiModelProperty(value = "调度周期", required = true)
    private String taskPeriodId;

    @ApiModelProperty(value = "任务状态", required = true)
    private String jobStatuses;

    @ApiModelProperty(value = "任务id列表", example = "[1,2,3]")
    private List<Long> taskIds;
}
