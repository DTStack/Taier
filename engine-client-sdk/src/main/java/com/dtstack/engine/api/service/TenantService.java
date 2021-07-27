package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;

import java.util.List;

public interface TenantService {

    PageResult<List<EngineTenantVO>> pageQuery(Long clusterId,
                                               Integer engineType,
                                               String tenantName,
                                               int pageSize,
                                               int currentPage);

    /**
     * 获取处于统一集群的全部tenant
     *
     * @param dtuicTenantId
     * @param engineType
     * @return
     */
    List<EngineTenantVO> listEngineTenant(Long dtuicTenantId, Integer engineType);

    List<UserTenantVO> listTenant(String dtToken);

    Void bindingTenant(Long dtUicTenantId, Long clusterId,
                       Long queueId, String dtToken);


    Void bindingQueue(Long queueId, Long dtUicTenantId, String taskTypeResourceJson);


    String queryResourceLimitByTenantIdAndTaskType(Long dtUicTenantId, Integer taskType);
}