package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据类目信息")
public class BatchDataCatalogueInfoVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "是否获取file", example = "true", required = true)
    private Boolean isGetFile;

    @ApiModelProperty(value = "ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "节点名称", example = "dev", required = true)
    private String nodeName;

}
