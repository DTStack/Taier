package com.dtstack.batch.web.user.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("修改成员角色信息")
public class BatchRoleUserUpdateUserRoleVO extends DtInsightAuthParam {

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

    @ApiModelProperty(value = "目标用户ID", example = "0", required = true)
    private Long targetUserId;
}
