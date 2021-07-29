package com.dtstack.batch.web.function.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("函数目录结果信息")
public class BatchFunctionCatalogueVO {
    @ApiModelProperty(value = "父目录")
    private BatchFunctionCatalogueVO parentCatalogue;

    @ApiModelProperty(value = "节点名称", example = "a")
    private String nodeName;

    @ApiModelProperty(value = "节点父id", example = "3")
    private Long nodePid;

    @ApiModelProperty(value = "目录层级 0:一级 1:二级 n:n+1级", example = "1")
    private Integer level;

    @ApiModelProperty(value = "创建用户")
    private Long createUserId;

    @ApiModelProperty(value = "engine类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "节点值", example = "1")
    private Integer orderVal;

    @ApiModelProperty(value = "目录类型", example = "1")
    private Integer catalogueType;
}