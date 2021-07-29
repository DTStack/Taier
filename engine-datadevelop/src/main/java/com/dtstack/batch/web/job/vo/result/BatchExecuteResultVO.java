package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("运行sql返回信息")
public class BatchExecuteResultVO<T> {

    @ApiModelProperty(value = "是否继续", example = "false")
    private Boolean isContinue = false;

    @ApiModelProperty(value = "下载", example = "1")
    private String download;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "发送到引擎生成的jobid", example = "1")
    private String  jobId;

    @ApiModelProperty(value = "引擎类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "结果")
    private List<T> result;

    @ApiModelProperty(value = "信息", example = "1")
    private String msg;

    @ApiModelProperty(value = "sql文本", example = "1")
    private String sqlText;
}
