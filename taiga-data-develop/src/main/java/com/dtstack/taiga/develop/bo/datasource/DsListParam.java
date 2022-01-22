package com.dtstack.taiga.develop.bo.datasource;

import com.dtstack.taiga.dao.domain.po.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 全阅
 * @Description:
 * @Date: 2021/3/8 19:14
 */
@Data
@ApiModel("数据源列表查询参数")
public class DsListParam extends BasePageParam {

    @ApiModelProperty("搜索参数")
    private String search;

    @ApiModelProperty(value = "数据源类型")
    private List<String> dataTypeList;

    @ApiModelProperty("是否显示默认数据库，0为不显示，1为显示")
    private Integer isMeta;

    @ApiModelProperty("连接状态")
    private List<Integer> status;
}
