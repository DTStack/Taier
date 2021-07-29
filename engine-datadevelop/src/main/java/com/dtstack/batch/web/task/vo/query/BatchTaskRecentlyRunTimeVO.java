package com.dtstack.batch.web.task.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("自定义调度周期接下来的调度日期")
public class BatchTaskRecentlyRunTimeVO {

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "cron表达式")
    private String cron;

    @ApiModelProperty(value = "预览个数")
    private Integer num;
}
