package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/6/22 8:00 下午
 */
@Data
@ApiModel("获取shell on agent 任务的 label和用户接口入参")
public class BatchGetShellOnAgentClusterAndUserVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

}
