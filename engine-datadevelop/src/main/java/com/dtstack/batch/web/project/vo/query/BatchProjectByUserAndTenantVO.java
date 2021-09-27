package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取所有项目信息")
public class BatchProjectByUserAndTenantVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "total 为false,查出该userId有参加的项目", example = "false", required = true)
    private Boolean total;

    @ApiModelProperty(hidden = true, value = "是否是uic的root用户")
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

}
