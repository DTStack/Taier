package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.QueueVO;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface EngineService extends DtInsightServer {

    @RequestLine("POST /node/engine/getQueue")
    List<QueueVO> getQueue( Long engineId);

    /**
     * [
     *     {
     *         "engineType":1,
     *         "supportComponent":[1,3,4]
     *     }
     * ]
     */
    @RequestLine("POST /node/engine/listSupportEngine")
    String listSupportEngine( Long dtUicTenantId);
}