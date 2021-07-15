package com.dtstack.batch.web.resource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("资源信息")
public class BatchResourcePageQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "资源修改用户 ID", example = "1", required = true)
    private Long resourceModifyUserId;

    @ApiModelProperty(value = "开始时间", example = "2020-12-30 11:42:14", required = true)
    private Timestamp startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-12-30 11:42:14", required = true)
    private Timestamp endTime;

    @ApiModelProperty(value = "总页数", example = "10", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "资源名称", example = "资源1", required = true)
    private String resourceName;

    @ApiModelProperty(value = "排序规则", example = "desc", required = true)
    private String sort = "desc";
}
