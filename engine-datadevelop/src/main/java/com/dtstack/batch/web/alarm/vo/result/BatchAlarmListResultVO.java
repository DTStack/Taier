package com.dtstack.batch.web.alarm.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("告警列表结果信息")
public class BatchAlarmListResultVO {
    @ApiModelProperty(value = "告警id", example = "1")
    private Long alarmId;

    @ApiModelProperty(value = "告警名称", example = "alarm_name")
    private String alarmName;

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "任务名称", example = "task_name")
    private String taskName;

    @ApiModelProperty(value = "告警发送方式")
    private List<String> senderTypes;

    @ApiModelProperty(value = "告警发送方式名称")
    private List<String> senderTypeNames;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType = 0;

    @ApiModelProperty(value = "接受用户列表")
    private List<BatchAlarmCountReceiver> receiveUsers;

    @ApiModelProperty(value = "告警接受用户", example = "admin")
    private String receiveUserStr;

    @ApiModelProperty(value = "触发条件", example = "1")
    private Integer myTrigger = 0;

    @ApiModelProperty(value = "告警状态", example = "1")
    private Integer alarmStatus;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp createTime;

    @ApiModelProperty(value = "创建用户", example = "admin")
    private String createUser;

    @ApiModelProperty(value = "创建用户id", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "未完成时间", example = "300")
    private String uncompleteTime;

    @ApiModelProperty(value = "钉钉webhook", example = "test")
    private String webhook;

    @ApiModelProperty(value = "是否任务责任人", example = "0")
    private Integer isTaskHolder = 0;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "发送时间", example = "5:00")
    private String sendTime;

    @ApiModelProperty(value = "接收方式")
    private List<Integer> receiveTypes;

    @ApiModelProperty(value = "接收人id列表")
    private List<Long> receivers;
}
