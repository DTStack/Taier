package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/4/14 2:17 下午
 */
@Data
@ApiModel("数据地图-查找数据：表责任人信息")
public class BatchTableOwnerUserVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "表责任人名称-模糊查询条件")
    private String ownerUserName;

}
