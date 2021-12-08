package com.dtstack.engine.domain;

import java.sql.Timestamp;

/**
 * Reason:
 * Date: 2017/5/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public class JobGraphTrigger extends BaseEntity {

    /**
     * 触发创建jobGraph的时间
     */
    private Timestamp triggerTime;

    private Integer triggerType;
    /**
     * 当前生成的schedule_job的最小id
     */
    private Long minJobId;

    public Timestamp getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Timestamp triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Integer getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public Long getMinJobId() {
        return minJobId;
    }

    public void setMinJobId(Long minJobId) {
        this.minJobId = minJobId;
    }
}
