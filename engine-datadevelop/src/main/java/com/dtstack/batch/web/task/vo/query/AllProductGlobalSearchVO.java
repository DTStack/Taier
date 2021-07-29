package com.dtstack.batch.web.task.vo.query;


import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class AllProductGlobalSearchVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "产品类型")
    private Integer appType;

    @ApiModelProperty(value = "租户ID")
    private Long uicTenantId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

}
