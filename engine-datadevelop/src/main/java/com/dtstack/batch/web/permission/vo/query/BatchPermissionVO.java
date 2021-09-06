package com.dtstack.batch.web.permission.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("权限基础信息")
public class BatchPermissionVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "角色ID", example = "1", required = true)
    private Long roleId;
}