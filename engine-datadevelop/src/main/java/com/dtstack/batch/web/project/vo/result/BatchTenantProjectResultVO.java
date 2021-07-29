package com.dtstack.batch.web.project.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("租户下所有的项目返回信息")
public class BatchTenantProjectResultVO {

    @ApiModelProperty(value = "项目 ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "项目别名", example = "dev")
    private String projectAlias;

    @ApiModelProperty(value = "租户ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "项目类型", example = "1")
    private Integer projectType;

    @ApiModelProperty(value = "项目状态", example = "1")
    private Integer status;

}
