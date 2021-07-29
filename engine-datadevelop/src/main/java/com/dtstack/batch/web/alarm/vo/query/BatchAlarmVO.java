package com.dtstack.batch.web.alarm.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("告警信息")
public class BatchAlarmVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "告警id", example = "1", required = true)
    private Long alarmId;

    @ApiModelProperty(value = "发送类型 1邮件 2短信", required = true)
    private List<String> senderTypes;

    @ApiModelProperty(value = "接收人id列表", required = true)
    private List<Long> receiverIds;

    @ApiModelProperty(value = "接受人名称", example = "users", required = true)
    private String receiveUsers;

    @ApiModelProperty(value = "允许通知的开始时间", example = "5:00", required = true)
    private String startTime;

    @ApiModelProperty(value = "允许通知的结束时间", example = "22:00", required = true)
    private String endTime;

    @ApiModelProperty(value = "钉钉webhook", example = "test", required = true)
    private String webhook;

    @ApiModelProperty(value = "未完成时间", example = "1", required = true)
    private String uncompleteTime;

    @ApiModelProperty(value = "接收人", example = "mike, tom", required = true)
    private String receivers;

    @ApiModelProperty(value = "告警名称", example = "alarmName", required = true)
    private String name;

    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "触发条件 0 失败 1完成 2 未完成", example = "0", required = true)
    private Integer myTrigger;

    @ApiModelProperty(value = "告警状态 0 正常 1关闭 2删除", example = "0", required = true)
    private Integer status;

    @ApiModelProperty(value = "发送类型", example = "1", required = true)
    private Integer senderType;

    @ApiModelProperty(value = "接收方式", example = "邮件", required = true)
    private String receiveTypes;

    @ApiModelProperty(value = "创建用户id", hidden = true)
    private Long createUserId;

    @ApiModelProperty(value = "任务负责人 1有任务负责人 0无任务负责人", example = "1", required = true)
    private  Integer  isTaskHolder;

    @ApiModelProperty(value = "告警类型  1项目报告 2任务告警", example = "2", required = true)
    private Integer alarmType;

    @ApiModelProperty(value = "项目报告发送时间", example = "0", required = true)
    private String sendTime;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "app类型 RDOS(1) DQ(2) API(3) TAG(4) MAP(5) CONSOLE(6) STREAM(7) DATASCIENCE(8)", example = "RDOS", required = true)
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
