package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表权限信息")
public class BatchTableInfoGetTablePermissionResultVO {

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "value")
    private Integer value;

}
