package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("脱敏规则信息")
public class BatchDataMaskRuleVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "脱敏规则名称", example = "aka", required = true)
    private String name;

    @ApiModelProperty(value = "脱敏样例", example = "*", required = true)
    private String example;

    @ApiModelProperty(value = "脱敏类型 0全部脱敏 1部分脱敏", example = "1", required = true)
    private Integer maskType;

    @ApiModelProperty(value = "脱敏替换字符串", example = "*")
    private String replaceStr = "*";

    @ApiModelProperty(value = "起始位", example = "1")
    private Integer beginPos = 0;

    @ApiModelProperty(value = "结束位", example = "1")
    private Integer endPos = 0;

    @ApiModelProperty(value = "修改用户id", example = "1", required = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型 RDOS(1) DQ(2), API(3) TAG(4) MAP(5) CONSOLE(6) STREAM(7) DATASCIENCE(8)", example = "1", required = true)
    private Integer appType;

    @ApiModelProperty(value = "脱敏id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1", required = true)
    private Integer isDeleted = 0;
}
