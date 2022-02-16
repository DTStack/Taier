package com.dtstack.taier.develop.bo.datasource;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/18
 */
public class DsInfoIdParam extends PubSvcBaseParam {

   @ApiModelProperty("数据源Id")
   private Long dataInfoId;

   public Long getDataInfoId() {
      return dataInfoId;
   }

   public void setDataInfoId(Long dataInfoId) {
      this.dataInfoId = dataInfoId;
   }
}
