package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("任务详情信息")
public class BatchJobFindTaskRuleJobResultVO {

    @ApiModelProperty(value = "任务名", example = "spark_task")
    private String name;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "租户名称", example = "dtstack")
    private String tenantName;

    @ApiModelProperty(value = "产品类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "规则类型", example = "0：无规则 1：弱规则 2：强规则")
    private Integer taskRule;

    @ApiModelProperty(value = "项目别名", example = "dev")
    private String projectAlias;

    @ApiModelProperty(value = "绑定的规则任务")
    private List<BatchJobFindTaskRuleJobResultVO> scheduleDetailsVOList;
}
