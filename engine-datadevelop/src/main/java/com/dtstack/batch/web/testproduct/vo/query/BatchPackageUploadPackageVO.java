package com.dtstack.batch.web.testproduct.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发布包信息")
public class BatchPackageUploadPackageVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "文件临时存储路径", hidden = true)
    private String tmpPath;

    @ApiModelProperty(value = "file原文件名", hidden = true)
    private String originalFilename;

}
