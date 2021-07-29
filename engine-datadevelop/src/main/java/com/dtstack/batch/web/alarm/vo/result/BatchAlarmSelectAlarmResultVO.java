package com.dtstack.batch.web.alarm.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;


@Data
@ApiModel("告警信息")
public class BatchAlarmSelectAlarmResultVO {

    @ApiModelProperty(value = "告警 ID", example = "1")
    private Long alarmId;

    @ApiModelProperty(value = "告警 名称", example = "告警测试")
    private String alarmName;

    @ApiModelProperty(value = "任务 ID", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "任务名称", example = "task_tidb")
    private String taskName;

    @ApiModelProperty(value = "告警发送方式")
    private List<String> senderTypes;

    @ApiModelProperty(value = "告警发送方式名称")
    private List<String> senderTypeNames;

    @ApiModelProperty(value = "任务类别", example = "0")
    private Integer taskType = 0;

    @ApiModelProperty(value = "告警接收人")
    private List<Receiver> receiveUsers;

    @ApiModelProperty(value = "调度方式")
    private Integer myTrigger = 0;

    @ApiModelProperty(value = "告警状态", example = "0")
    private Integer alarmStatus = 0;

    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "创建人", example = "admin")
    private String createUser;

    @ApiModelProperty(value = "创建人 ID", example = "13")
    private Long createUserId;

    @ApiModelProperty(value = "未完成时间", example = "09:35")
    private String uncompleteTime;

    @ApiModelProperty(value = "钉钉webhook", example = "hook")
    private String webhook;

    @ApiModelProperty(value = "是否项目责任人", example = "0")
    private Integer isTaskHolder = 0;

    @ApiModelProperty(value = "租户 ID", example = "3")
    private Long tenantId = 0L;

    @ApiModelProperty(value = "项目 ID", example = "11")
    private Long projectId = 0L;

    @ApiModelProperty(value = "发送时间")
    private String sendTime;

    @Data
    public static class Receiver {

        @ApiModelProperty(value = "用户 ID")
        private Long userId;

        @ApiModelProperty(value = "用户名称")
        private String userName;

    }
}
