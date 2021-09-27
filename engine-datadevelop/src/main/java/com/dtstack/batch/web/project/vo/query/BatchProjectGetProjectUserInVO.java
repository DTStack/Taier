package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获得用户下的项目信息")
public class BatchProjectGetProjectUserInVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "默认项目 ID", example = "1L", required = true)
    private Long defaultProjectId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;
}
