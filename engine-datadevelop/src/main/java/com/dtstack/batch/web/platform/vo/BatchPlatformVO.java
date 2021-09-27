package com.dtstack.batch.web.platform.vo;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("回调参数")
public class BatchPlatformVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "加密验证", required = true)
    private String sign;

    @ApiModelProperty(value = "事件",example = "LOG_OUT", required = true)
    private String eventCode;

    @ApiModelProperty(value = "uic 用户id", example = "1")
    private Long userId;

    @ApiModelProperty(hidden = true)
    private String token;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "原来的租户所有者",example = "1")
    private Long oldOwnerUicUserId;

    @ApiModelProperty(value = "现在的租户所有者",example = "1")
    private Long newOwnerUicUserId;

    @ApiModelProperty(value = "uic 租户ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "uic 租户ID 不同接口入参不一样", example = "1")
    private Long dtUicTenantId;

    @ApiModelProperty(value = "置为管理员/取消管理员 true：置为管理员 false：取消管理员", example = "true")
    private Boolean isAdmin;
}
