package com.dtstack.batch.web.filemanager.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("小文件合并规则根据ruleId更新规则状态")
public class BatchFileMergeRuleUpdateStatusVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "治理规则id", example = "1", required = true)
    private Long ruleId;


    @ApiModelProperty(value = "治理规则状态 0启动 1停止", example = "1", required = true)
    private Integer ruleStatus;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "是否是超管", hidden = true)
    private Boolean isRoot;

}
