package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取不在项目的UIC用户")
public class BatchProjectGetUicNotInProjectResultVO  {

    @ApiModelProperty(value = "uic 用户 ID")
    private Long userId;

    @ApiModelProperty(value = "账号名称")
    private String userName;

    @ApiModelProperty(value = "姓名")
    private String fullName;

    @ApiModelProperty(value = "是否是平台管理员")
    private Boolean appRoot;

    @ApiModelProperty(value = "是否是租户管理员")
    private Boolean tenantAdmin;

    @ApiModelProperty(value = "是否是租户所有者")
    private Boolean tenantOwner;

}
