package com.dtstack.batch.web.table.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel("表信息")
public class BatchTableDataHistoryResultVO{

    @ApiModelProperty(value = "项目大小")
    private Map<String, Object> projectSize;

    @ApiModelProperty(value = "表数量")
    private Map<String, Object> tableNum;

}
