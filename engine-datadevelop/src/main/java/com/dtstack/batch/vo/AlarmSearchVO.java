package com.dtstack.batch.vo;

import lombok.Data;

/**
 * Created by jiangbo on 2017/5/5 0005.
 */
@Data
public class AlarmSearchVO {

    private Integer pageSize = 10;
    private String taskName;
    private Long ownerId = 0L;
    private Integer alarmStatus = -1;
    private Boolean isTimeSortDesc = true;
    private Integer pageIndex = 1;
    private Long userId = 0L;
    private Long projectId = 0L;
    private Long tenantId = 0L;

    public boolean isTimeSortDesc() {
        return isTimeSortDesc;
    }

    public void setTimeSortDesc(boolean timeSortDesc) {
        isTimeSortDesc = timeSortDesc;
    }
}
