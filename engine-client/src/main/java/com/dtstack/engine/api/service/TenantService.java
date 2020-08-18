package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface TenantService extends DtInsightServer {

    @RequestLine("POST /node/tenant/pageQuery")
    ApiResponse<PageResult<List<EngineTenantVO>>> pageQuery(@Param("clusterId") Long clusterId,
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
    @RequestLine("POST /node/tenant/listEngineTenant")
    ApiResponse<List<EngineTenantVO>> listEngineTenant(@Param("dtuicTenantId") Long dtuicTenantId,
                                                       @Param("engineType") Integer engineType);

    @RequestLine("POST /node/tenant/dtToken")
    ApiResponse<List<UserTenantVO>> listTenant(@Param("dtToken") String dtToken);

    @RequestLine("POST /node/tenant/bindingTenant")
    ApiResponse<Void> bindingTenant(@Param("tenantId") Long dtUicTenantId, @Param("clusterId") Long clusterId,
                              @Param("queueId") Long queueId, @Param("dtToken") String dtToken) ;


    @RequestLine("POST /node/tenant/bindingQueue")
    ApiResponse<Void> bindingQueue(@Param("queueId") Long queueId,
                             @Param("tenantId") Long dtUicTenantId);
}