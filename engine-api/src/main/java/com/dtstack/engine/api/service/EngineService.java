package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.vo.QueueVO;

import java.util.List;

public interface EngineService {

    public List<QueueVO> getQueue( Long engineId);

    /**
     * [
     *     {
     *         "engineType":1,
     *         "supportComponent":[1,3,4]
     *     }
     * ]
     */
    public String listSupportEngine( Long dtUicTenantId);
}