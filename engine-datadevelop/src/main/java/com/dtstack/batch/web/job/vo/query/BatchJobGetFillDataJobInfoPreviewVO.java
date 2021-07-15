package com.dtstack.batch.web.job.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询出所有的补数据名称信息")
public class BatchJobGetFillDataJobInfoPreviewVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "运行日期", example = "1609084800", required = true)
    private Long runDay;

    @ApiModelProperty(value = "实例名称", example = "jobName", required = true)
    private String jobName;

    @ApiModelProperty(value = "biz开始日期", example = "1609084800")
    private Long bizStartDay;

    @ApiModelProperty(value = "biz结束日期", example = "1609084800")
    private Long bizEndDay;

    @ApiModelProperty(value = "biz日期", example = "1609084800", required = true)
    private Long bizDay;

    @ApiModelProperty(value = "责任人ID", example = "1", required = true)
    private Long dutyUserId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "总页数", example = "1", required = true)
    private Integer pageSize;

}
