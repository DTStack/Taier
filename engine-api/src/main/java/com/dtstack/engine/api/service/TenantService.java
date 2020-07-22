package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;

import java.util.List;

public interface TenantService {

    public PageResult<List<EngineTenantVO>> pageQuery( Long clusterId,
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
    public List<EngineTenantVO> listEngineTenant( Long dtuicTenantId,
                                                  Integer engineType);

    public List listTenant( String dtToken);

    public void bindingTenant( Long dtUicTenantId,  Long clusterId,
                               Long queueId,  String dtToken) throws Exception;


    public void bindingQueue( Long queueId,
                              Long dtUicTenantId);
}