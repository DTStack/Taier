package com.dtstack.taier.scheduler.dto.schedule;

import com.dtstack.taier.dao.domain.ScheduleTaskShade;

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
    private ScheduleTaskShade scheduleTaskShade;

    /**
     * 父节点taskId
     */
    private List<Long>  parentTaskIdList;

    public ScheduleTaskShade getScheduleTaskShade() {
        return scheduleTaskShade;
    }

    public void setScheduleTaskShade(ScheduleTaskShade scheduleTaskShade) {
        this.scheduleTaskShade = scheduleTaskShade;
    }

    public List<Long> getParentTaskIdList() {
        return parentTaskIdList;
    }

    public void setParentTaskIdList(List<Long> parentTaskIdList) {
        this.parentTaskIdList = parentTaskIdList;
    }
}
