package com.dtstack.batch.web.apply.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("申请资源/权限-查询信息")
public class BatchApplyQueryVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "权限管理 - 列表类型", example = "1", required = true)
    private Integer listType = 0;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "页大小", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "资源名称", example = "name", required = true)
    private String resourceName;

    @ApiModelProperty(value = "开始时间", example = "1609472875000")
    private Long startTime;

    @ApiModelProperty(value = "截止时间", example = "1609991275000")
    private Long endTime;

    @ApiModelProperty(value = "所属项目id", example = "1", required = true)
    private Long belongProjectId;

    @ApiModelProperty(value = "排序字段", example = "gmt_modified")
    private String sortColumn = "gmt_modified";

    @ApiModelProperty(value = "排序", example = "desc")
    private String sort = "desc";

    @ApiModelProperty(value = "申请用户id", example = "1", required = true)
    private Long applyUserId;

    @ApiModelProperty(value = "申请状态", required = true)
    private List<Integer> status;

    @ApiModelProperty(value = "表类型", example = "1", required = true)
    private Integer tableType;

    @ApiModelProperty(value = "是否为root", example = "false")
    private Boolean isRoot = false;
}
