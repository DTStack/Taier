package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据源查询列表信息")
public class BatchDataSourceQueryListVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "数据源类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "模糊查询名称", example = "table_name")
    private String name;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "条数限制", example = "100")
    private Integer limit;
}
