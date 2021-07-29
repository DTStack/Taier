package com.dtstack.batch.web.server.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据类型获取日志返回信息")
public class BatchServerLogByAppLogTypeResultVO {

    @ApiModelProperty(value = "信息", example = "msg")
    private String msg;

    @ApiModelProperty(value = "下载地址", example = "/api/")
    private String download;
}
