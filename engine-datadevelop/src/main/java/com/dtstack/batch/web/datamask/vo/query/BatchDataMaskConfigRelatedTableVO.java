package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("脱敏表关系信息")
public class BatchDataMaskConfigRelatedTableVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "脱敏id", example = "1", required = true)
    private Long configId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long pjId;

    @ApiModelProperty(value = "脱敏表名称", example = "table_name", required = true)
    private String tableName;

    @ApiModelProperty(value = "脱敏表字段名称", example = "col_name", required = true)
    private String columnName;

    @ApiModelProperty(value = "批量开启/关闭脱敏 0-正常 1-禁用", required = true)
    private List<Integer> enable;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "页大小", example = "10")
    private Integer pageSize;
}
