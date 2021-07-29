package com.dtstack.engine.datasource.param.datasource;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("搜索数据源类型参数")
public class DsTypeSearchParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "数据源分类主键id", required = true)
    private Long classifyId;

    @ApiModelProperty("数据源名称搜索")
    private String search;

}
