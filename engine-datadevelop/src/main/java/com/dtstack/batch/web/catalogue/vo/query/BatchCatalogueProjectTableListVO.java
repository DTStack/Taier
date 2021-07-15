package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("目录项目表信息")
public class BatchCatalogueProjectTableListVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "表名称", example = "test", required = true)
    private String tableName;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "script类型", example = "1", required = true)
    private Integer scriptType;

    @ApiModelProperty(value = "表唯一标识", example = "dev", required = true)
    private String projectIdentifier;

    @ApiModelProperty(value = "是否为root", hidden = true)
    private Boolean isRoot;
}
