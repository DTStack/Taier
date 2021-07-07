package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by jiangbo on 2017/5/19 0019.
 */
@Data
public class AlarmRecordVO {

    private String time;
    private String taskName;
    private String taskCreateUser;
    private String taskOwnerUser;
    private String receiveUserStr;
    private List<AlarmVO.Receiver> receiveUsers;
    // 告警发送方式
    private List<String> senderTypes;
    // 告警发送方式名称
    private List<String> senderTypeNames;
    private Integer taskType = 0;
    private String alarmContent;
    private Integer alarmType = 0;
    private Integer myTrigger = 0;
    private Long alarmId;
    private Long createUserId;
    private Long taskId;
}
