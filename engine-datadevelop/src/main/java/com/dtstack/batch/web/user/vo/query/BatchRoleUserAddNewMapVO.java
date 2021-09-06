package com.dtstack.batch.web.user.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("添加用户的Map信息")
public class BatchRoleUserAddNewMapVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "活跃状态", required = true)
    private Boolean active;

    @ApiModelProperty(value = "邮箱", example = "true", required = true)
    private String email;

    @ApiModelProperty(value = "手机号", example = "17731900898")
    private String phone;

    @ApiModelProperty(value = "uic 用户 ID", example = "1", required = true)
    private Long userId;

    @ApiModelProperty(value = "用户名称", example = "admin@dtstack.com", required = true)
    private String userName;

}
