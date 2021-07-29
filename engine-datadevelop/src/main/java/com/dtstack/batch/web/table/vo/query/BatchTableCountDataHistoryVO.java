package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表统计信息")
public class BatchTableCountDataHistoryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", example = "1",  required = true)
    @JsonProperty("pId")
    private Long pId;

    @ApiModelProperty(value = "查询时间", example = "1",  required = true)
    private Integer fastTime;

    @ApiModelProperty(value = "时间范围 开始", example = "1607669752",  required = true)
    private Long start;

    @ApiModelProperty(value = "时间范围 结束", example = "1608965752",  required = true)
    private Long end;

}
