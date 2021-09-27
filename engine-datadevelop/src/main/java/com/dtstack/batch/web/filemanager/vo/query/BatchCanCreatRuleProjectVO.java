package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用于查询可以创建规则的项目
 */
@Data
@ApiModel("用于查询可以创建规则的项目")
public class BatchCanCreatRuleProjectVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(example = "xxx", value = "项目名称 模糊查询")
    private String projectName;

    @ApiModelProperty(value = "是否是超管", hidden = true)
    private Boolean isRoot;

}
