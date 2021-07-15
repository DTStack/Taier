package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("sql或者脚本执行日志信息")
public class BatchExecuteRunLogResultVO<T> {

    @ApiModelProperty(value = "任务 ID", example = "3")
    private String jobId;

    @ApiModelProperty(value = "sql", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "消息", example = "test")
    private String msg;

    @ApiModelProperty(value = "下载路径")
    private String download;

    @ApiModelProperty(value = "引擎类别", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "是否需要重新获取日志", example = "false")
    private Boolean retryLog;

}
