package com.dtstack.engine.datasource.param.datasource.api;

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
@ApiModel("外部对接数据源列表查询参数")
public class DsServiceListParam extends BasePageParam {

    @ApiModelProperty(value = "产品type", example = "1", required = true)
    private Integer appType;

    @ApiModelProperty(value = "数据源类型", example = "如Mysql, Oracle, Hive")
    private List<String> dataTypeList;

    @ApiModelProperty(value = "数据源类型编码列表", example = "[1,2]")
    private List<Integer> dataTypeCodeList;

    @ApiModelProperty("搜索参数")
    private String search;

    @ApiModelProperty("数据源名称")
    private String dataName;

    @ApiModelProperty("租户主键id")
    private Long dsTenantId;

    @ApiModelProperty("租户 dtuic id")
    private Long dsDtuicTenantId;

    @ApiModelProperty("项目id projectId")
    private Long projectId;

    @ApiModelProperty("是否为meta数据源")
    private Integer isMeta;

    @ApiModelProperty("数据库名称列表")
    private List<String> schemaNameList;


}
