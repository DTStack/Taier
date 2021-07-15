package com.dtstack.batch.web.resource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BatchResourceRenameResourceVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "资源名称", example = "资源1", required = true)
    private String name;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "资源 ID", required = true)
    private Long resourceId;
}
