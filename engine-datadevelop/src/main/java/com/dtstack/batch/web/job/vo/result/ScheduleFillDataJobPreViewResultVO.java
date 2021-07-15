package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("返回所有的补数据名称信息")
public class ScheduleFillDataJobPreViewResultVO {

    @ApiModelProperty(value = "ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "起始时间", example = "2020-12-28 10:03:19")
    private String fromDay;

    @ApiModelProperty(value = "截至时间", example = "2020-12-28 10:03:19")
    private String toDay;

    @ApiModelProperty(value = "责任用户名", example = "2020-12-28 10:03:19")
    private String createTime;

    @ApiModelProperty(value = "责任用户名", example = "ruomu")
    private String dutyUserName;

    @ApiModelProperty(value = "成功job数量", example = "1")
    private Long finishedJobSum;

    @ApiModelProperty(value = "所有job数量", example = "1")
    private Long allJobSum;

    @ApiModelProperty(value = "完成的job数量", example = "1")
    private Long doneJobSum;

    @ApiModelProperty(value = "责任人ID", example = "1")
    private Long dutyUserId;

    @ApiModelProperty(value = "补数据实例名称",example = "P_test_adsad_2020_12_29_35_13")
    private String fillDataJobName;

}
