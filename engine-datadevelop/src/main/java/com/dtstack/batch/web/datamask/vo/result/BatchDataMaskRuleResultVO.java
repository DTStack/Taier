package com.dtstack.batch.web.datamask.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("脱敏规则结果信息")
public class BatchDataMaskRuleResultVO {
    @ApiModelProperty(value = "脱敏规则名称", example = "aka")
    private String name;

    @ApiModelProperty(value = "脱敏样例", example = "*")
    private String example;

    @ApiModelProperty(value = "脱敏类型", example = "1")
    private Integer maskType;

    @ApiModelProperty(value = "脱敏替换字符串", example = "*")
    private String replaceStr = "*";

    @ApiModelProperty(value = "起始位", example = "1")
    private Integer beginPos = 0;

    @ApiModelProperty(value = "结束位", example = "1")
    private Integer endPos = 0;

    @ApiModelProperty(value = "修改用户id", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id", example = "0")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}
