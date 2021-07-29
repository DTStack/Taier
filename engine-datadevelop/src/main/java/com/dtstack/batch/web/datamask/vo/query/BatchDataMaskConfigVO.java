package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("数据脱敏管理信息")
public class BatchDataMaskConfigVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "脱敏名称", example = "aka", required = true)
    private String name;

    @ApiModelProperty(value = "脱敏表id", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "脱敏表字段名称", example = "id", required = true)
    private String columnName;

    @ApiModelProperty(value = "脱敏规则id", example = "1", required = true)
    private Long ruleId;

    @ApiModelProperty(value = "脱敏样例", example = "*", required = true)
    private String example;

    @ApiModelProperty(value = "修改用户id", example = "1", required = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "开启/关闭脱敏 0-正常 1-禁用", example = "0", required = true)
    private Integer enable = 0;

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
