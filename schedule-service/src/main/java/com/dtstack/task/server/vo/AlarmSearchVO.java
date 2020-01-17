package com.dtstack.task.server.vo;

import com.dtstack.engine.domain.AppTenantEntity;

import java.util.List;

/**
 * Created by jiangbo on 2017/5/5 0005.
 */
public class AlarmSearchVO extends AppTenantEntity {

    private int pageSize = 10;
    private String taskName;
    private long ownerId = 0;
    private int alarmStatus = -1;
    private boolean isTimeSortDesc = true;
    private int pageIndex = 1;
    private long userId;
    private List<Long> taskIds;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public boolean isTimeSortDesc() {
        return isTimeSortDesc;
    }

    public void setTimeSortDesc(boolean timeSortDesc) {
        isTimeSortDesc = timeSortDesc;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
