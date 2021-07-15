package com.dtstack.batch.web.testproduct.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("导入导出包信息")
public class BatchPackageItemGetPackageItemListVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "发布包 ID", example = "3")
    private Long packageId;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageIndex;

    @ApiModelProperty(value = "展示个数", example = "10", required = true)
    private Integer pageSize;

}
