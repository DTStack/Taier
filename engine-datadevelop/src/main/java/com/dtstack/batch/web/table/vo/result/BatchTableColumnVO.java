package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * date: 2021/6/9 4:24 下午
 * author: zhaiyue
 */
@Data
@ApiModel("表字段信息")
public class BatchTableColumnVO {

    @ApiModelProperty(value = "字段名")
    private String key;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "是否是分区字段")
    private Boolean part = false;

    @ApiModelProperty(value = "小数点右边的指定列的位数")
    private Integer scale;

    @ApiModelProperty(value = "指定列的指定列大小")
    private Integer precision;

}
