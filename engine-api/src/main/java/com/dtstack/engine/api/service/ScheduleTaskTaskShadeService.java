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

    public void clearDataByTaskId( Long taskId,Integer appType);

    public void saveTaskTaskList( String taskLists);

    public List<ScheduleTaskTaskShade> getAllParentTask( Long taskId);


    public ScheduleTaskVO displayOffSpring( Long taskId,
                                            Long projectId,
                                            Long userId,
                                            Integer level,
                                            Integer directType, Integer appType);
    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskVO getAllFlowSubTasks( Long taskId,  Integer appType);
}
