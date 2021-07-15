package com.dtstack.batch.web.tenant.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("添加/删除租户信息")
public class BatchTenantAddOrUpdateVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户名称", example = "DTStack租户", required = true)
    private String tenantName;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "创建用户 ID", example = "1", required = true)
    private Long createUserId;

    @ApiModelProperty(value = "租户排序规则", example = "desc")
    private String tenantDesc;

    @ApiModelProperty(value = "租户状态", example = "0")
    private Integer status;

    @ApiModelProperty(value = "租户 ID", example = "0")
    private Long id;

    @ApiModelProperty(value = "创建时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-23 11:42:14")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}
