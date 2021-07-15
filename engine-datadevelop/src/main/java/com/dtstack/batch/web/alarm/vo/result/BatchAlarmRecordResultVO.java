package com.dtstack.batch.web.alarm.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("告警记录结果信息")
public class BatchAlarmRecordResultVO {
    @ApiModelProperty(value = "告警时间", example = "5:00")
    private String time;

    @ApiModelProperty(value = "任务名称", example = "task_name")
    private String taskName;

    @ApiModelProperty(value = "任务创建人", example = "admin")
    private String taskCreateUser;

    @ApiModelProperty(value = "任务责任人", example = "admin")
    private String taskOwnerUser;

    @ApiModelProperty(value = "告警接受用户", example = "admin")
    private String receiveUserStr;

    @ApiModelProperty(value = "接受用户列表")
    private List<BatchAlarmCountReceiver> receiveUsers;

    @ApiModelProperty(value = "告警发送方式")
    private List<String> senderTypes;

    @ApiModelProperty(value = "告警发送方式名称")
    private List<String> senderTypeNames;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType = 0;

    @ApiModelProperty(value = "告警内容", example = "test")
    private String alarmContent;

    @ApiModelProperty(value = "告警类型", example = "1")
    private Integer alarmType = 0;

    @ApiModelProperty(value = "触发条件", example = "1")
    private Integer myTrigger = 0;

    @ApiModelProperty(value = "告警id", example = "1")
    private Long alarmId;

    @ApiModelProperty(value = "创建用户id", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;
}
