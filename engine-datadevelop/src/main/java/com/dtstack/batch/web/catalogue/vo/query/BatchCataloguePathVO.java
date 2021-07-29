package com.dtstack.batch.web.catalogue.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("目录路径信息")
public class BatchCataloguePathVO {
    @ApiModelProperty(value = "路径列表", required = true)
    private List<String> nameList;

    @ApiModelProperty(value = "父节点名称", example = "root_name", required = true)
    private String rootName;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "script类型", example = "1")
    private Integer scriptType;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;
}
