package com.dtstack.batch.web.project.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取项目用户信息")
public class BatchProjectGetProjectTaskUsersVO extends DtInsightAuthParam {

   @ApiModelProperty(value = "项目 ID", hidden = true)
   private Long projectId;

   @ApiModelProperty(value = "租户 ID", hidden = true)
   private Long tenantId;

   @ApiModelProperty(value = "用户 ID", hidden = true)
   private Long userId;

   @ApiModelProperty(value = "用户名称", required = true, example = "test")
   private String name;

   @ApiModelProperty(value = "当前页", required = true, example = "1")
   private Integer currentPage;

   @ApiModelProperty(value = "展示条数", required = true, example = "10")
   private Integer pageSize;

}
