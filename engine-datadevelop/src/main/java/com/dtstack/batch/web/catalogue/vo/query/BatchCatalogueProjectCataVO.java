package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("目录项目信息")
public class BatchCatalogueProjectCataVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "是否获取文件", example = "false")
    private Boolean isGetFile = false;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "是否为root", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "是否admin用户", hidden = true)
    private Boolean isAdmin;
}
