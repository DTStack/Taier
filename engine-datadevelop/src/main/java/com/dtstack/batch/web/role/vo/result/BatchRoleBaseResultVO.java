package com.dtstack.batch.web.role.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("权限基础信息")
public class BatchRoleBaseResultVO {

    @ApiModelProperty(value = "租户 ID",example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID",example = "1")
    private Long projectId;

    @ApiModelProperty(value = "角色名称",example = "访客")
    private String roleName;

    @ApiModelProperty(value = "角色类型",example = "1")
    private Integer roleType;

    @ApiModelProperty(value = "角色值",example = "1")
    private Integer roleValue;

    @ApiModelProperty(value = "角色排序",example = "desc")
    private String roleDesc;

    @ApiModelProperty(value = "修改用户ID",example = "5")
    private Long modifyUserId;
}
