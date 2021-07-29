package com.dtstack.batch.web.role.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("删除角色信息")
public class BatchRoleDeleteVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "角色ID", example = "1", required = true)
    private Long roleId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long userId;
}
