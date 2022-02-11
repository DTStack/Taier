package com.dtstack.taiga.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 8:15 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleTaskShadeMapper extends BaseMapper<ScheduleTaskShade> {

    /**
     * 查询所有可以生成周期实例的任务
     *
     * @param startId 开始id
     * @param scheduleStatusList 任务状态
     * @param taskSize 获取的任务数
     * @return 任务列表
     */
    List<ScheduleTaskShade> listRunnableTask(@Param("startId") Long startId, @Param("scheduleStatusList") List<Integer> scheduleStatusList, @Param("taskSize") Integer taskSize);
}
