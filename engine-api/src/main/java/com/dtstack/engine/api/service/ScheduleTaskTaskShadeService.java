package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;


import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskTaskShadeService {

    public void clearDataByTaskId(@Param("taskId") Long taskId,@Param("appType")Integer appType);

    public void saveTaskTaskList(@Param("taskTask") String taskLists);

    public List<ScheduleTaskTaskShade> getAllParentTask(@Param("taskId") Long taskId);


    public ScheduleTaskVO displayOffSpring(@Param("taskId") Long taskId,
                                           @Param("projectId") Long projectId,
                                           @Param("userId") Long userId,
                                           @Param("level") Integer level,
                                           @Param("type") Integer directType, @Param("appType")Integer appType);
    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskVO getAllFlowSubTasks(@Param("taskId") Long taskId, @Param("appType") Integer appType);
}
