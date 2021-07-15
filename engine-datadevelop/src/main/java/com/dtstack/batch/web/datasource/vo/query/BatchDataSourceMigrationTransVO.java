package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("整库同步转换信息")
public class BatchDataSourceMigrationTransVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "数据源id", example = "1", required = true)
    private Long sourceId;

    @ApiModelProperty(value = "转换field列表", required = true)
    private List<BatchDataSourceMigrationTransformFieldVO> transformFields;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;
}
