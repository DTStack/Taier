package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取不在项目的UIC用户")
public class BatchProjectGetUicNotInProjectVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

}
