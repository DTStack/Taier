package com.dtstack.batch.web.user.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息")
public class BatchRoleUserGetUsersAndPermissionVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "名称", example = "test", required = true)
    private String name;

    @ApiModelProperty(value = "历史拥有用户 ID", example = "3", hidden = true)
    private Long oldOwnerUserId;

}
