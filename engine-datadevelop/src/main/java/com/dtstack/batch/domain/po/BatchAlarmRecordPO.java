package com.dtstack.batch.domain.po;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Reason:
 * Date: 2018/1/3
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class BatchAlarmRecordPO {

    private Long id;

    private Long alarmId;

    private String alarmContent;

    private Timestamp createTime;

    private Long createUserId;

    private Long taskId;

    private Integer senderType;

    private Integer myTrigger;

    private String receiveUser;

}
