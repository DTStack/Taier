package com.dtstack.batch.web.testproduct.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发布包信息")
public class BatchPackageGetRelationByTaskVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 ID", example = "33", required = true)
    private Long taskId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

}
