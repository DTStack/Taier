package com.dtstack.batch.web.user.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("添加用户权限信息")
public class BatchRoleUserAddNewVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "角色Id列表", required = true)
    private List<Long> roleIds;

    @ApiModelProperty(value = "目标用户列表", required = true)
    private List<BatchRoleUserAddNewMapVO> targetUsers;
}
