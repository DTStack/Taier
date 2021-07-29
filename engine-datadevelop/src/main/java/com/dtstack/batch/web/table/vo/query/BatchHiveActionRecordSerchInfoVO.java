package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("表操作记录信息")
public class BatchHiveActionRecordSerchInfoVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "展示条数", example = "10",  required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页数", example = "1",  required = true)
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "操作", example = "新增",  required = true)
    private String operate;

    @ApiModelProperty(value = "操作人 ID", example = "3",  required = true)
    private Long actionUserId;

    @ApiModelProperty(value = "时间范围开始", example = "1609326301",  required = true)
    private Long startTime;

    @ApiModelProperty(value = "时间范围截止", example = "1609326301",  required = true)
    private Long endTime;

    @ApiModelProperty(value = "表 ID", example = "3",  required = true)
    private Long tableId;

    @ApiModelProperty(value = "sql", example = "select * from user",  required = true)
    private String sql;
}
