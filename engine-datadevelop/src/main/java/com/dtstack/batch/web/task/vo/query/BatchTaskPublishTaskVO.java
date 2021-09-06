package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskPublishTaskVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "发布备注", example = "test", required = true)
    private String publishDesc;

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "忽略检查", example = "true", required = true)
    private Boolean ignoreCheck;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;
    
}
