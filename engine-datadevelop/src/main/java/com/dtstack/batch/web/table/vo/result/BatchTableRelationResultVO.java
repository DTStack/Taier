package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表关系信息")
public class BatchTableRelationResultVO {

    @ApiModelProperty(value = "relation ID", example = "32")
    private Long relationId;

    @ApiModelProperty(value = "名称", example = "test")
    private String name;

    @ApiModelProperty(value = "创建人", example = "admin")
    private String createUser;

    @ApiModelProperty(value = "任务类别", example = "0")
    private Integer taskType = 0;

    @ApiModelProperty(value = "脚本类别", example = "0")
    private Integer scriptType = 0;

    @ApiModelProperty(value = "项目名称", example = "开发项目")
    private String projectName;

    @ApiModelProperty(value = "项目 ID", example = "3")
    private Long projectId;

    @ApiModelProperty(value = "是否有权限", example = "1")
    private Integer isPermissioned;

}
