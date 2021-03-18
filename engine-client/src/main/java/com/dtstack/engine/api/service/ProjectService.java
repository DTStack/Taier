package com.dtstack.engine.api.service;

import com.dtstack.engine.api.param.ScheduleEngineProjectParam;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface ProjectService extends DtInsightServer {

    @RequestLine("POST /node/project/updateSchedule")
    ApiResponse<Void> updateSchedule(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("scheduleStatus") Integer scheduleStatus);

    @RequestLine("POST /node/project/addProject")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> addProject(ScheduleEngineProjectParam scheduleEngineProjectParam);

    @RequestLine("POST /node/project/updateProject")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> updateProject(ScheduleEngineProjectParam scheduleEngineProjectParam);

    @RequestLine("POST /node/project/deleteProject")
    ApiResponse<Void> deleteProject( @Param("projectId") Long projectId, @Param("appType") Integer appType);

    @RequestLine("POST /node/project/findFuzzyProjectByProjectAlias")
    ApiResponse<List<ScheduleEngineProjectVO>> findFuzzyProjectByProjectAlias(@Param("name") String name, @Param("appType") Integer appType, @Param("uicTenantId") Long uicTenantId);

    @RequestLine("POST /node/project/findProject")
    ApiResponse<ScheduleEngineProjectVO> findProject(@Param("projectId") Long projectId,@Param("appType") Integer appType);
}