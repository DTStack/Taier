package com.dtstack.taier.scheduler.server.builder.cron;

import com.dtstack.taier.scheduler.server.builder.ScheduleConf;

/**
 * @Auther: dazhi
 * @Date: 2021/12/30 6:50 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface IScheduleConfParser {

    /**
     * 解析前端封装的任务运行
     *
     * @param scheduleConf 任务运行周期
     * @return corn 表达式
     */
    String parse(ScheduleConf scheduleConf);
}
