package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/18
 */
@Data
public class DsInfoIdParam extends PubSvcBaseParam {

   @ApiModelProperty("数据源Id")
   private Long dataInfoId;

}
