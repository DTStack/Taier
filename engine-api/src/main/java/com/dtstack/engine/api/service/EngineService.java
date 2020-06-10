package com.dtstack.engine.api.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.vo.EngineVO;
import com.dtstack.engine.api.vo.QueueVO;

import java.util.List;

public interface EngineService {

    List<QueueVO> getQueue(@Param("engineId") Long engineId);

    /**
     * [
     *     {
     *         "engineType":1,
     *         "supportComponent":[1,3,4]
     *     }
     * ]
     */
    public String listSupportEngine(@Param("tenantId") Long dtUicTenantId);

    @Forbidden
    public void addEnginesByComponentConfig(JSONObject componentConfig, Long clusterId);

    @Forbidden
    public Engine getOne(Long engineId);

    @Forbidden
    public List<EngineVO> listClusterEngines(Long clusterId, boolean queryQueue);
}
