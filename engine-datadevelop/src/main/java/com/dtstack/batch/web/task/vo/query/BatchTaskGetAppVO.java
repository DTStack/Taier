package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * date: 2021/4/12 4:18 下午
 * author: zhaiyue
 */
@Data
@ApiModel("获取产品信息")
public class BatchTaskGetAppVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", required = true, example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "UIC 用户 ID", hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(value = "dt token", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "产品 code", hidden = true)
    private String productCode;

}
