package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/26
 */
@SuppressWarnings("serial")
@Data
public class Alarm extends TenantProjectEntity {

    /**
     * 告警名称
     */
    private String name;

    private Long taskId;

    /**
     * 触发条件   0 失败
     */
    private Integer myTrigger;

    /**
     * 告警状态   0 正常 1关闭 2删除
     */
    private Integer status;

    private Integer senderType;

    /** 接收方式 */
    private String receiveTypes;


    private Long createUserId;



    //任务负责人 1 有任务负责人  0无任务负责人
    private  Integer  isTaskHolder;

    //告警类型  1：项目报告，2：任务告警
    private Integer alarmType;

    // 项目报告发送时间
    private String sendTime;

    public Integer getIsTaskHolder() {
        return isTaskHolder;
    }

    public void setIsTaskHolder(Integer isTaskHolder) {
        this.isTaskHolder = isTaskHolder;
    }
}
