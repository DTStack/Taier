package com.dtstack.batch.web.job.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("补数据信息")
public class BatchJobFillTaskDataVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "任务的json数据", example = "[{\"task\":39}]", required = true)
    private String taskJson;

    @ApiModelProperty(value = "补数据名称", example = "P_test_asdasd_2020_12_29_37_20", required = true)
    private String fillName;

    @ApiModelProperty(value = "开始日期", example = "1609084800", required = true)
    private Long fromDay;

    @ApiModelProperty(value = "结束日期", example = "1609171199", required = true)
    private Long toDay;

    @ApiModelProperty(value = "开始时间", example = "2020-10-10", required = true)
    private String concreteStartTime;

    @ApiModelProperty(value = "结束时间", example = "2020-10-10", required = true)
    private String concreteEndTime;
}
