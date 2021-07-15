package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("目录位置信息")
public class BatchCatalogueLocationVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "目录类型", example = "1", required = true)
    private String catalogueType;

    @ApiModelProperty(value = "位置id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "任务名称", example = "a", required = true)
    private String name;
}
