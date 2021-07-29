package com.dtstack.batch.web.project.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目引擎信息")
public class BatchProjectEngineBaseVO {

    @ApiModelProperty(value = "引擎类型", example = "Hadoop", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "数据库名称", example = "dataBase", required = true)
    private String database;

    @ApiModelProperty(value = "创建项目的方式", example = "1", required = true)
    private Integer createModel;

    @ApiModelProperty(value = "生命周期", example = "9999", required = true)
    private Integer lifecycle;

    @ApiModelProperty(value = "目录 ID", example = "1", required = true)
    private Long catalogueId;
}
