package com.dtstack.engine.datasource.param.datasource;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
public class DataTypeParam extends PubSvcBaseParam {

   @ApiModelProperty("数据源类型编码")
   private String dataType;
}
