package com.dtstack.engine.api.vo.schedule.task.shade;

import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskVO;

import java.util.List;


/**
 * @Auther: dazhi
 * @Date: 2020/7/30 11:54 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskShadePageVO{

    private PageResult<List<ScheduleTaskVO>> pageResult;
    private Integer publishedTasks;


    public PageResult<List<ScheduleTaskVO>> getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResult<List<ScheduleTaskVO>> pageResult) {
        this.pageResult = pageResult;
    }

    public Integer getPublishedTasks() {
        return publishedTasks;
    }

    public void setPublishedTasks(Integer publishedTasks) {
        this.publishedTasks = publishedTasks;
    }
}
