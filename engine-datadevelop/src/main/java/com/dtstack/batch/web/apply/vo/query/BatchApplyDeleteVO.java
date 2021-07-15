package com.dtstack.batch.web.apply.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("申请删除信息")
public class BatchApplyDeleteVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "申请id列表", required = true)
    private List<Long> ids;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;
}
