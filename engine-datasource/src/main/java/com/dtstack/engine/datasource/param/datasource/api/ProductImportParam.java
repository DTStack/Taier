package com.dtstack.engine.datasource.param.datasource.api;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("对接外部产品引用参数类")
public class ProductImportParam extends PubSvcBaseParam implements Serializable {

    @ApiModelProperty(value = "数据源主键id list", required = true)
    private List<Long> dataInfoIdList;

    @ApiModelProperty(value = "产品type", example = "1", required = true)
    private Integer appType;

    @ApiModelProperty(value = "dtUic租户id", required = false)
    private Long dtUicTenantId;

    @ApiModelProperty(value = "projectId", required = false)
    private Long  projectId;

}
