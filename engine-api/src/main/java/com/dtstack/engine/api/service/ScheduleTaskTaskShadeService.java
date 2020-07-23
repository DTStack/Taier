package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;


import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskTaskShadeService extends DtInsightServer {

    @RequestLine("POST /node/scheduleTaskTaskShade/clearDataByTaskId")
    void clearDataByTaskId(Long taskId, Integer appType);

    @RequestLine("POST /node/scheduleTaskTaskShade/saveTaskTaskList")
    void saveTaskTaskList(String taskLists);

    @RequestLine("POST /node/scheduleTaskTaskShade/getAllParentTask")
    List<ScheduleTaskTaskShade> getAllParentTask(Long taskId);


    @RequestLine("POST /node/scheduleTaskTaskShade/displayOffSpring")
    ScheduleTaskVO displayOffSpring(Long taskId,
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
    @RequestLine("POST /node/scheduleTaskTaskShade/getAllFlowSubTasks")
    ScheduleTaskVO getAllFlowSubTasks(Long taskId, Integer appType);
}
