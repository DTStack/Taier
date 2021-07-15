package com.dtstack.batch.web.function.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("函数查询结果信息")
public class BatchFunctionQueryResultVO {
    @ApiModelProperty(value = "创建用户")
    private BatchFunctionUserVO createUser;

    @ApiModelProperty(value = "修改用户")
    private BatchFunctionUserVO modifyUser;

    @ApiModelProperty(value = "函数名称", example = "name")
    private String name;

    @ApiModelProperty(value = "main函数类名", example = "class_name")
    private String className;

    @ApiModelProperty(value = "函数用途", example = "name")
    private String purpose;

    @ApiModelProperty(value = "函数命令格式", example = "test")
    private String commandFormate;

    @ApiModelProperty(value = "函数参数说明", example = "name")
    private String paramDesc;

    @ApiModelProperty(value = "父文件夹id", example = "1")
    private Long nodePid;

    @ApiModelProperty(value = "创建用户id", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "修改用户id", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "函数类型 0自定义 1系统 2存储过程", example = "0")
    private Integer type;

    @ApiModelProperty(value = "engine类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "函数资源名称", example = "test_name")
    private String resourceName;

    @ApiModelProperty(value = "存储过程sql", example = "test_name")
    private String sqlText;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "关联资源的id")
    private Long resources = 0L;
}