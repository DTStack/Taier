package com.dtstack.batch.web.model.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取权限模型返回信息")
public class BatchModelGetModelRuleResultVO {

    @ApiModelProperty(value = "值", example = "1")
    private Integer value;

    @ApiModelProperty(value = "名称", example = "ruomu")
    private String name;
}
