package com.dtstack.engine.dto;

import com.dtstack.engine.domain.BatchAlarmRecord;

import java.util.List;

/**
 * Reason:
 * Date: 2018/1/2
 * Company: www.dtstack.com
 * @author xuchao
 */

public class BatchAlarmRecordDTO extends BatchAlarmRecord {

    private List<Long> taskIdList;

    private Long startTime;

    private Long endTime;

    private List<Long> alarmIdList;

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<Long> getAlarmIdList() {
        return alarmIdList;
    }

    public void setAlarmIdList(List<Long> alarmIdList) {
        this.alarmIdList = alarmIdList;
    }
}
