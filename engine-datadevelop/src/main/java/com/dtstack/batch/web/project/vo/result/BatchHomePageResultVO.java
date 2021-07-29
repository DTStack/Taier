package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("主页返回信息")
public class BatchHomePageResultVO {

    @ApiModelProperty(value = "总项目数量", example = "1")
    private Integer totalProjects;

    @ApiModelProperty(value = "总失败数量", example = "1")
    private Integer totalFailJobs;

    @ApiModelProperty(value = "总数据大小", example = "1")
    private String totalDataSize;
}
