package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取所有项目信息")
public class BatchProjectGetAllProjectsVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "total 为false,查出该userId有参加的项目", example = "false", required = true)
    private Boolean total;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;
}
