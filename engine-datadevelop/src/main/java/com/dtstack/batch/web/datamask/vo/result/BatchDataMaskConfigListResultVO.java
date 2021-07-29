package com.dtstack.batch.web.datamask.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("数据脱敏管理结果信息")
public class BatchDataMaskConfigListResultVO {
    @ApiModelProperty(value = "关联表数量", example = "10")
    private Integer relatedNum;

    @ApiModelProperty(value = "修改用户名称", example = "admin")
    private String modifyUserName;

    @ApiModelProperty(value = "规则名称名称", example = "project_name")
    private String ruleName;

    @ApiModelProperty(value = "项目名称", example = "project_name")
    private String projectName;

    @ApiModelProperty(value = "项目别名", example = "project_alia")
    private String projectAlias;

    @ApiModelProperty(value = "脱敏名称", example = "aka")
    private String name;

    @ApiModelProperty(value = "脱敏表id", example = "1")
    private Long tableId;

    @ApiModelProperty(value = "脱敏表字段名称", example = "id")
    private String columnName;

    @ApiModelProperty(value = "脱敏规则id", example = "1")
    private Long ruleId;

    @ApiModelProperty(value = "脱敏样例", example = "*")
    private String example;

    @ApiModelProperty(value = "修改用户id", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "开启/关闭脱敏", example = "0")
    private Integer enable = 0;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "脱敏id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}
