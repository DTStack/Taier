package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("sql或者脚本执行结果信息")
public class BatchExecuteDataResultVO<T> {

    @ApiModelProperty(value = "任务 ID", example = "3")
    private String jobId;

    @ApiModelProperty(value = "sql", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "执行结果")
    private List<T> result;

    @ApiModelProperty(value = "引擎类别", example = "1")
    private Integer engineType;

}
