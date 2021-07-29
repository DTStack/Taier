package com.dtstack.batch.web.user.vo.result;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息")
public class BatchUserRolePermissionResultVO {

    @ApiModelProperty(value = "用户 ID", example = "3")
    private Long userId;

    @ApiModelProperty(value = "用户 名称", example = "admin")
    private String userName;

    @ApiModelProperty(value = "是否为访客", example = "true")
    private Boolean isCustomer;

}
