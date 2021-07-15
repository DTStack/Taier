package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("表关系信息")
public class BatchTableRelationAddVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "表名 list", required = true)
    private List<String> tables;

    @ApiModelProperty(value = "相关id", example = "1", required = true)
    private Long relationId;

    @ApiModelProperty(value = "是否是任务，0-否，1-是", example = "1", required = true)
    private Integer relationType = 0;

    @ApiModelProperty(value = "类型", example = "1", required = true)
    private Integer type = 0;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

}
