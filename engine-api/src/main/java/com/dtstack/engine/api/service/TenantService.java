package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;

import java.util.List;

public interface TenantService {

    public PageResult<List<EngineTenantVO>> pageQuery(@Param("clusterId") Long clusterId,
                                                      @Param("engineType") Integer engineType,
                                                      @Param("tenantName") String tenantName,
                                                      @Param("pageSize") int pageSize,
                                                      @Param("currentPage") int currentPage);

    /**
     * 获取处于统一集群的全部tenant
     *
     * @param dtuicTenantId
     * @param engineType
     * @return
     */
    public List<EngineTenantVO> listEngineTenant(@Param("dtuicTenantId") Long dtuicTenantId,
                                                 @Param("engineType") Integer engineType);

    public List listTenant(@Param("dtToken") String dtToken);

    public void bindingTenant(@Param("tenantId") Long dtUicTenantId, @Param("clusterId") Long clusterId,
                              @Param("queueId") Long queueId, @Param("dtToken") String dtToken) throws Exception;

    @Forbidden
    public Tenant addTenant(Long dtUicTenantId, String dtToken);

    public void bindingQueue(@Param("queueId") Long queueId,
                             @Param("tenantId") Long dtUicTenantId);
}
