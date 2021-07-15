package com.dtstack.batch.web.user.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取租户下的用户结果信息")
public class BatchUserGetUsersInTenantVO {

    @ApiModelProperty(value = "用户ID", example = "0")
    private Long userId;

    @ApiModelProperty(value = "用户名", example = "zhangsan@dtstack.com")
    private String userName;
}
