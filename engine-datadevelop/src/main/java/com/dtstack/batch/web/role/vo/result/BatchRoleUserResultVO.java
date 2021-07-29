package com.dtstack.batch.web.role.vo.result;

import com.dtstack.batch.web.user.vo.result.BatchUserBaseResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("用户角色返回信息")
public class BatchRoleUserResultVO {

    @ApiModelProperty(value = "角色信息")
    private BatchRoleBaseResultVO role;

    @ApiModelProperty(value = "角色ID", example = "1")
    private Long roleId;

    @ApiModelProperty(value = "用户信息")
    private BatchUserBaseResultVO user;

    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "租户 ID",example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID",example = "1")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", example = "1")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台类别", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "脱敏id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}
