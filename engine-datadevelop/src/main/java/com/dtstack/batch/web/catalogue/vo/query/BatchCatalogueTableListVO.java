package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("目录表列表信息")
public class BatchCatalogueTableListVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "表名称", example = "test", required = true)
    private String tableName;

    @ApiModelProperty(value = "项目id", hidden = true, required = true)
    private Long appointProjectId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "script类型", example = "1", required = true)
    private Integer scriptType;
}
