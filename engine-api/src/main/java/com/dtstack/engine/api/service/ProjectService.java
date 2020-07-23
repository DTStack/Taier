package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

public interface ProjectService extends DtInsightServer {

    @RequestLine("POST /node/project/updateSchedule")
    void updateSchedule(Long projectId, Integer appType, Integer scheduleStatus);
}