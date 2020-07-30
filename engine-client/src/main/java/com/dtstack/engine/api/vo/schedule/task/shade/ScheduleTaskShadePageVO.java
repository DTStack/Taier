package com.dtstack.engine.api.vo.schedule.task.shade;

import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;


/**
 * @Auther: dazhi
 * @Date: 2020/7/30 11:54 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleTaskShadePageVO{

    private PageResult pageResult;
    private Integer publishedTasks;


    public PageResult getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResult pageResult) {
        this.pageResult = pageResult;
    }

    public Integer getPublishedTasks() {
        return publishedTasks;
    }

    public void setPublishedTasks(Integer publishedTasks) {
        this.publishedTasks = publishedTasks;
    }
}
