package com.dtstack.batch.web.tenant.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("租户更新的出参信息")
public class TenantUpdateResultVO {

    @ApiModelProperty(value = "租户名称", example = "DTStack租户")
    private String tenantName;

    @ApiModelProperty(value = "UIC租户 ID", example = "1")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "创建用户 ID", example = "1")
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
