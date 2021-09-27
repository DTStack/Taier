package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("停止补数据实例")
public class BatchJobStopFillDataJobsVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "补数据实例名称",example = "P_test_adsad_2020_12_29_35_13", required = true)
    private String fillDataJobName;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;
}
