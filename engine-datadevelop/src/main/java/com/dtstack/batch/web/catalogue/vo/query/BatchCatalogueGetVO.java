package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("目录获取信息")
public class BatchCatalogueGetVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long appointProjectId;

    @ApiModelProperty(value = "是否获取文件", example = "false")
    private Boolean isGetFile = false;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "父id", example = "1")
    private Long parentId;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "节点父id", example = "3", required = true)
    private Long nodePid;

    @ApiModelProperty(value = "目录类型", example = "1", required = true)
    private String catalogueType;
}
