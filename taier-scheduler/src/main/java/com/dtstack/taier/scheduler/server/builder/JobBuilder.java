package com.dtstack.taier.scheduler.server.builder;

import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/30 2:57 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public interface JobBuilder {

    /**
     * 构建周期实例
     *
     * @param scheduleTaskShade 构建的任务
     * @param name 实例名称（只在yarn上面显示）
     * @param triggerDay 目标天(格式 yyyy-MM-dd)
     * @param beginTime 从什么时间开始生成(格式:HH:mm:ss) 非必填 默认00:00:00
     * @param endTime 从什么时间结束生成(格式:HH:mm:ss) 非必填 默认23:59:59
     * @param fillId 补数据id,周期实例直接传0
     * @return 生成周期实例
     */
    List<ScheduleJobDetails> buildJob(ScheduleTaskShade scheduleTaskShade,
                                      String name,
                                      String triggerDay,
                                      String beginTime,
                                      String endTime,
                                      Long fillId,
                                      JobSortWorker jobSortWorker) throws Exception;
}
