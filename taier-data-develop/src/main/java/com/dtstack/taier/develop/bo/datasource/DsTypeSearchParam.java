package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/18
 */
@Data
@ApiModel("搜索数据源类型参数")
public class DsTypeSearchParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源分类主键id", required = true)
    private Long classifyId;

    @ApiModelProperty("数据源类目名称搜索")
    private String search;

}
