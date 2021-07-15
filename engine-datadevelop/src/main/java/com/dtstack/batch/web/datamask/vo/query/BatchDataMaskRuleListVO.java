package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏规则列表信息")
public class BatchDataMaskRuleListVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "脱敏规则名称", example = "aka", required = true)
    private String name;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long pjId;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "页大小", example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "用户id", example = "false")
    private Boolean isRoot;
}
