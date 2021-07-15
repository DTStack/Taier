package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("sql或者脚本执行状态信息")
public class BatchExecuteStatusResultVO<T> {

    @ApiModelProperty(value = "任务 ID", example = "3")
    private String jobId;

    @ApiModelProperty(value = "sql", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "引擎类别", example = "1")
    private Integer engineType;

}
