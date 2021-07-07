package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("自定义调度周期接下来的调度日期")
public class BatchTaskRecentlyRunTimeResultVO {

    @ApiModelProperty(value = "调度日期")
    private List<String> recentlyRunTimes;

    @ApiModelProperty(value = "cron表达式是否合法", example = "true")
    private Boolean isCronValid;
}
