package com.dtstack.batch.web.model.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("查询全部模型字段")
public class BatchModelColumnPageQueryVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "总页数", example = "15")
    private Integer pageSize;

    @ApiModelProperty(value = "模糊查询", example = "name")
    private String name;

    @ApiModelProperty(value = "用户名", example = "admin")
    private String userName;

    @ApiModelProperty(value = "类型", example = "5")
    private Integer type;

    @ApiModelProperty(value = "指标类型", example = "5")
    private Integer columnType;

    @ApiModelProperty(value = "指标命名", example = "指标命名")
    private String columnName;

    @ApiModelProperty(value = "指标名称", example = "指标名称")
    private String columnNameZh;

    @ApiModelProperty(value = "数据类型", example = "int")
    private String dataType;

    @ApiModelProperty(value = "指标口径", example = "desc")
    private String modelDesc;

    @ApiModelProperty(value = "最近修改人id", example = "5")
    private Long modifyUserId;

    @ApiModelProperty(value = "创建者用户id", example = "5")
    private Long createUserId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "ID", example = "0")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}
