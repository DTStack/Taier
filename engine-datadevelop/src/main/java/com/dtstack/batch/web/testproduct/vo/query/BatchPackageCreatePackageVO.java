package com.dtstack.batch.web.testproduct.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("发布包信息")
public class BatchPackageCreatePackageVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "发布包名称", example = "test发布包", required = true)
    private String packageName;

    @ApiModelProperty(value = "发布包备注", example = "for test", required = true)
    private String packageDesc;

    @ApiModelProperty(value = "打包项目 list")
    private List<BatchPackageItemInfoVO> items;

}
