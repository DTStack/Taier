package com.dtstack.batch.web.model.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("汇总返回信息")
public class BatchUsageResultVO {

    @ApiModelProperty(value = "新表数量", example = "1")
    private Integer todayNewTable;

    @ApiModelProperty(value = "新字段数量", example = "1")
    private Integer todayNewColumn;

    @ApiModelProperty(value = "今日不规范模型总数", example = "1")
    private Integer todayBadTable;

    @ApiModelProperty(value = "今日不规范字段总数", example = "1")
    private Integer todayBadColumn;

    @ApiModelProperty(value = "不规范模型总数", example = "1")
    private Integer sumBadTable;

    @ApiModelProperty(value = "不规范字段总数", example = "1")
    private Integer sumBadColumn;
}
