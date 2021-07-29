package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;


/**
 * Created by jiangbo on 2017/5/5 0005.
 */
@Data
public class AlarmSearchRecordVO {

    private Long projectId;
    private Long userId;
    private String taskName;
    private Long receive;
    private List<Long> alarmIds;
    private List<Long> taskIds;
    private Boolean isTimeSortDesc = true;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Long startTime;
    private Long endTime;

    public boolean isTimeSortDesc() {
        return isTimeSortDesc;
    }

    public void setTimeSortDesc(boolean timeSortDesc) {
        isTimeSortDesc = timeSortDesc;
    }

}
