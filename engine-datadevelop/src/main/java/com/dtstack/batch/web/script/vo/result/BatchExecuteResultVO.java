package com.dtstack.batch.web.script.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("运行sql脚本返回信息")
public class BatchExecuteResultVO<T> {

    @ApiModelProperty(value = "任务实例Id", example = "1")
    private String jobId;

    @ApiModelProperty(value = "sql 文本", example = "select * from test")
    private String sqlText;

    @ApiModelProperty(value = "实例信息", example = "msg...")
    private String msg;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "结果列表")
    private List<T> result;

    @ApiModelProperty(value = "是否继续", example = "false")
    private Boolean isContinue = false;

    @ApiModelProperty(value = "下载地址", example = "/api")
    private String download;

    @ApiModelProperty(value = "引擎类型", example = "1")
    private Integer engineType;
}
