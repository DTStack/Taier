package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskCheckNameVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 名称", example = "spark_test", required = true)
    private String name;

    @ApiModelProperty(value = "类别", example = "1", required = true)
    private String type;

    @ApiModelProperty(value = "父id", example = "3", required = true)
    private Integer pid;

    @ApiModelProperty(value = "是否是文件", example = "1", required = true)
    private Integer isFile;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;
}
