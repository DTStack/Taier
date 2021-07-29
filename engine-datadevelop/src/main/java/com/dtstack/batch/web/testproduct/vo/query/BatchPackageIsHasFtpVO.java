package com.dtstack.batch.web.testproduct.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发布包信息")
public class BatchPackageIsHasFtpVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    Long dtuicTenantId;
}
