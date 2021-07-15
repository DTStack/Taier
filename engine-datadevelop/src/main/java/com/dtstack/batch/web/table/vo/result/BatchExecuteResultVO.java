package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("执行结果信息")
public class BatchExecuteResultVO<T> {

    @ApiModelProperty(value = "任务 ID", example = "3")
    private String jobId;

    @ApiModelProperty(value = "sql", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "消息", example = "test")
    private String msg;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "执行结果")
    private List<T> result;

    @ApiModelProperty(value = "是否继续", example = "false")
    private Boolean isContinue = false;

    @ApiModelProperty(value = "下载路径")
    private String download;

    @ApiModelProperty(value = "引擎类别", example = "1")
    private Integer engineType;

}
