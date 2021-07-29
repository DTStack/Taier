package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("sql返回信息")
public class BatchSqlResultVO<T> {

    @ApiModelProperty(value = "sql对应的id", example = "1")
    private String sqlId;

    @ApiModelProperty(value = "类型", example = "1")
    private Integer type;

    @ApiModelProperty(value = "结果")
    private List<T> result;

    @ApiModelProperty(value = "信息", example = "1")
    private String msg;

    @ApiModelProperty(value = "sql文本", example = "1")
    private String sqlText;
}
