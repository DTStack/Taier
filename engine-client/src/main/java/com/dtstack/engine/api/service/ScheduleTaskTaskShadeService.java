package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.*;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskTaskShadeService extends DtInsightServer {

    @RequestLine("POST /node/scheduleTaskTaskShade/clearDataByTaskId")
    ApiResponse clearDataByTaskId(@Param("taskId") Long taskId, @Param("appType") Integer appType);

    @RequestLine("POST /node/scheduleTaskTaskShade/saveTaskTaskList")
    ApiResponse saveTaskTaskList(@Param("taskLists") String taskLists);

    @RequestLine("POST /node/scheduleTaskTaskShade/getAllParentTask")
    ApiResponse<List<ScheduleTaskTaskShade>> getAllParentTask(@Param("taskId") Long taskId);


    @RequestLine("POST /node/scheduleTaskTaskShade/displayOffSpring")
    ApiResponse<ScheduleTaskVO> displayOffSpring(@Param("taskId") Long taskId,
                                                 @Param("projectId") Long projectId,
                                                 @Param("userId") Long userId,
                                                 @Param("level") Integer level,
                                                 @Param("directType") Integer directType, @Param("appType") Integer appType);

    /**
     * 查询工作流全部节点信息 -- 依赖树
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/scheduleTaskTaskShade/getAllFlowSubTasks")
    ApiResponse<ScheduleTaskVO> getAllFlowSubTasks(@Param("taskId") Long taskId, @Param("appType") Integer appType);
}
