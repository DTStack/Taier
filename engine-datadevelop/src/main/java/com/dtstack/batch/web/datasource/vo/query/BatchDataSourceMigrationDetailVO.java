package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("整库同步详情信息")
public class BatchDataSourceMigrationDetailVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "整库同步id", example = "1", required = true)
    private Long migrationId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;
}
