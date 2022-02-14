package com.dtstack.taier.scheduler.dto.schedule;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/31 9:54 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SavaTaskDTO {

    /**
     * 任务
     */
    private ScheduleTaskShadeDTO scheduleTaskShadeDTO;

    /**
     * 父节点taskId
     */
    private List<Long>  parentTaskIdList;


    public ScheduleTaskShadeDTO getScheduleTaskShadeDTO() {
        return scheduleTaskShadeDTO;
    }

    public void setScheduleTaskShadeDTO(ScheduleTaskShadeDTO scheduleTaskShadeDTO) {
        this.scheduleTaskShadeDTO = scheduleTaskShadeDTO;
    }

    public List<Long> getParentTaskIdList() {
        return parentTaskIdList;
    }

    public void setParentTaskIdList(List<Long> parentTaskIdList) {
        this.parentTaskIdList = parentTaskIdList;
    }
}
