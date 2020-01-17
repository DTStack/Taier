package com.dtstack.engine.dto;

import com.dtstack.engine.domain.BatchAlarm;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchAlarmDTO extends BatchAlarm {

    /**
     * 允许通知的开始时间，如5：00，早上5点
     */
    private String startTime;
    /**
     * 允许通知的结束时间，如22：00，不接受告警
     */
    private String endTime;

    private String webhook;

    private List<Long> taskIds;

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }
}
