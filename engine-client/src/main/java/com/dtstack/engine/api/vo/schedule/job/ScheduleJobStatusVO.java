package com.dtstack.engine.api.vo.schedule.job;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 10:37 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobStatusVO {

    private Integer all;

    private List<ScheduleJobStatusCountVO> scheduleJobStatusCountVO;

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

    public List<ScheduleJobStatusCountVO> getScheduleJobStatusCountVO() {
        return scheduleJobStatusCountVO;
    }

    public void setScheduleJobStatusCountVO(List<ScheduleJobStatusCountVO> scheduleJobStatusCountVO) {
        this.scheduleJobStatusCountVO = scheduleJobStatusCountVO;
    }
}
