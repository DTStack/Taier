package com.dtstack.batch.web.dirtydata.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据预览信息")
public class BatchDirtyDataOverViewVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "表id", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "最近几天", example = "3")
    private Integer recentTime;
}
