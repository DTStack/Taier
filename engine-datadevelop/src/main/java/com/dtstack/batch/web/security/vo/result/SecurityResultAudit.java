package com.dtstack.batch.web.security.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @author: aka
 * @date: 2021/1/19 14:59
 */
@Data
@ApiModel("安全升级日志")
public class SecurityResultAudit {
    @ApiModelProperty(name = "操作人", example = "admin")
    private String operator;

    @ApiModelProperty(name = "操作人在当前app下的userId", example = "1")
    private Long operatorId;

    @ApiModelProperty(name = "操作人在当前app下的租户id", example = "1")
    private Long tenantId;

    @ApiModelProperty(name = "操作人在当前app下的项目id", example = "1")
    private Long projectId;

    @ApiModelProperty(name = "是否不记录日志", example = "false")
    private Boolean ignoreLog;

    @ApiModelProperty(name = "参数列表")
    private Map<String,Object> securityDataMap = new HashMap<>(4);

    @ApiModelProperty(name = "结果")
    private String result;
}