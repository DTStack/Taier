package com.dtstack.batch.domain;

import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/26
 */
@SuppressWarnings("serial")
@Data
public class AlarmRecord extends TenantProjectEntity {

    private Long alarmId;

    /**
     * 告警内容
     */
    private String alarmContent;

    /**
     * 触发方式
     */
    private Integer triggerType;

    private String receiveUser;

}
