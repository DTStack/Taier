package com.dtstack.engine.datasource.param.datasource;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("数据源类型和版本统一入参")
public class DsTypeVersionParam extends PubSvcBaseParam {

    @ApiModelProperty("数据源类型 如MySql, Oracle")
    private String dataType;

    @ApiModelProperty("数据源版本, 可为空")
    private String dataVersion;


}
