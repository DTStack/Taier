package com.dtstack.batch.web.project.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目添加引擎信息")
public class BatchProjectAddNewEngineVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "UIC 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "引擎类型", example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "引擎 DB 信息", example = "dev", required = true)
    private String database;

    @ApiModelProperty(value = "添加 DB 模式， 0 导入已有 DB，1 创建", example = "0", required = true)
    private Integer createModel;

    @ApiModelProperty(value = "表的生命周期", example = "9999", required = true)
    private Integer lifecycle;

    @ApiModelProperty(value = "所属类目", example = "18", required = true)
    private Long catalogueId;
}
