package com.dtstack.batch.web.testproduct.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("发布包信息")
public class BatchExportPackageAsZipVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtUictenantId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "发布包 ID", example = "1", required = true)
    private Long packageId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "下载文件名称", example = "test")
    private String downFileName;

}
