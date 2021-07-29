package com.dtstack.batch.web.alarm.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("告警结果信息")
public class BatchAlarmResultVO {
    @ApiModelProperty(value = "未完成时间", example = "1")
    private String uncompleteTime;

    @ApiModelProperty(value = "接收人", example = "mike, tom")
    private String receivers;

    @ApiModelProperty(value = "告警名称", example = "alarmName")
    private String name;

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "触发条件 0 失败 1完成 2 未完成", example = "0")
    private Integer myTrigger;

    @ApiModelProperty(value = "告警状态 0 正常 1关闭 2删除", example = "0")
    private Integer status;

    @ApiModelProperty(value = "发送类型", example = "1")
    private Integer senderType;

    @ApiModelProperty(value = "接收方式", example = "邮件")
    private String receiveTypes;

    @ApiModelProperty(value = "创建用户id")
    private Long createUserId;

    @ApiModelProperty(value = "任务负责人 1有任务负责人 0无任务负责人", example = "1")
    private Integer isTaskHolder;

    @ApiModelProperty(value = "告警类型  1项目报告 2任务告警", example = "2")
    private Integer alarmType;

    @ApiModelProperty(value = "项目报告发送时间", example = "0")
    private String sendTime;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted = 0;
}
