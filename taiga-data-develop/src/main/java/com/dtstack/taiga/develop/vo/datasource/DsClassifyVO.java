package com.dtstack.taiga.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据源分类类目模型
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@Data
@ApiModel("数据源分类类目模型")
public class DsClassifyVO implements Serializable {

    @ApiModelProperty("类目主键id")
    private Long classifyId;

    @ApiModelProperty("数据源类目编码")
    private String classifyCode;

    @ApiModelProperty("类目名称")
    private String classifyName;

    @ApiModelProperty("类型栏排序字段")
    private Integer sorted;


}
