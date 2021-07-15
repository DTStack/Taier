package com.dtstack.batch.web.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分区信息")
public class PartitionResultVO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "上次DDL时间")
    private Long lastDDLTime;

    @ApiModelProperty(value = "存储大小")
    private String storeSize;

    @ApiModelProperty(value = "part ID")
    private Long partId;

    @ApiModelProperty(value = "分区下文件数量")
    private Long fileCount;

}
