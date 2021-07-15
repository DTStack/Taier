package com.dtstack.batch.web.table.vo.result;

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
public class BatchTableOwnerUserResultVO {

    @ApiModelProperty(value = "表责任人id")
    private Long userId;

    @ApiModelProperty(value = "表责任人名称")
    private String userName;

}
