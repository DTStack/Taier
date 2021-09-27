package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取可创建项目的database")
public class BatchProjectGetRetainDBVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "是否过滤backup库", example = "false", required = true)
    private Boolean backupFilter;

    @ApiModelProperty(value = "engineType", example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;
}
