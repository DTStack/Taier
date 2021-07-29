package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("表数据预览信息")
public class BatchTableInfoGetDataResultVO {

    @ApiModelProperty(value = "状态 true：有权限 false： 无权限", example = "true")
    public Boolean status;

    @ApiModelProperty(value = "数据", example = "")
    public List<Object> data;

    @ApiModelProperty(value = "提示信息")
    public String msg;

}
