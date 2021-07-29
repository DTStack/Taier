package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("数据源查询信息")
public class BatchDataSourceQueryVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "数据源类型", example = "[1]")
    private List<Integer> types;

    @ApiModelProperty(value = "数据源名称/描述 (模糊查询)", example = "dev_TIDB")
    private String name;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "当前页数", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "展示条数", example = "10", required = true)
    private Integer pageSize;
}
