package com.dtstack.batch.web.user.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息")
public class BatchRoleUserHandoverOwnerVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "旧的用户ID", example = "3", required = true)
    private Long oldOwnerUserId;

    @ApiModelProperty(value = "新的用户ID", example = "5", required = true)
    private Long newOwnerUserId;

    @ApiModelProperty(value = "项目 ID", required = true)
    private Long projectId;

    @ApiModelProperty(value = "租户 ID", required = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户 ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "是否root用户",  required = true)
    private Boolean isRoot;

}
