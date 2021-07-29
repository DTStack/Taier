package com.dtstack.batch.web.dirtydata.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据趋势信息")
public class BatchDirtyDateTrendVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "表id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "最近几天", example = "3")
    private Integer recent;

    @ApiModelProperty(value = "field", example = "1")
    private Integer field;

    @ApiModelProperty(value = "间隔", example = "1")
    private Integer interval;

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;
}
