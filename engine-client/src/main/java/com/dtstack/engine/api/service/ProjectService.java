package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

public interface ProjectService extends DtInsightServer {

    @RequestLine("POST /node/project/updateSchedule")
    ApiResponse updateSchedule(@Param("projectId") Long projectId, @Param("appType") Integer appType, @Param("scheduleStatus") Integer scheduleStatus);
}