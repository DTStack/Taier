package com.dtstack.batch.web.dirtydata.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据top30信息")
public class BatchDirtyDataTopVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "最近几天", example = "3")
    private Integer recent;

    @ApiModelProperty(value = "脏数据产生前30", example = "30")
    private Integer topN;
}
