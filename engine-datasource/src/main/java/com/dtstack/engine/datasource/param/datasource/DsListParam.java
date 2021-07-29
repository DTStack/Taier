package com.dtstack.engine.datasource.param.datasource;

import com.dtstack.engine.datasource.param.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据源列表查询参数")
public class DsListParam extends BasePageParam {

    @ApiModelProperty("搜索参数")
    private String search;

    @ApiModelProperty(value = "数据源类型")
    private List<String> dataTypeList;

    @ApiModelProperty(value = "产品类型")
    private List<Integer> appTypeList;

    @ApiModelProperty(value = "是否显示默认数据库，0为不显示，1为显示")
    private Integer isMeta;

    @ApiModelProperty(value = "连接状态")
    private List<Integer> status;
}
