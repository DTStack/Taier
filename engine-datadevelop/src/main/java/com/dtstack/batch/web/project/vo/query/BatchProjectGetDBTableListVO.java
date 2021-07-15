package com.dtstack.batch.web.project.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取当前租户下指定引擎类型db的表信息信息")
public class BatchProjectGetDBTableListVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "引擎类型", example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "db名称", example = "dev", required = true)
    private String dbName;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

}
