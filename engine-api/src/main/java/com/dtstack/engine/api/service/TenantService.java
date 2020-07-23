package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

public interface TenantService extends DtInsightServer {

    @RequestLine("POST /node/tenant/pageQuery")
    PageResult<List<EngineTenantVO>> pageQuery( Long clusterId,
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
    @RequestLine("POST /node/tenant/listEngineTenant")
    List<EngineTenantVO> listEngineTenant( Long dtuicTenantId,
                                           Integer engineType);

    @RequestLine("POST /node/tenant/listTenant")
    List listTenant( String dtToken);

    @RequestLine("POST /node/tenant/bindingTenant")
    void bindingTenant( Long dtUicTenantId,  Long clusterId,
                        Long queueId,  String dtToken) throws Exception;


    @RequestLine("POST /node/tenant/bindingQueue")
    void bindingQueue( Long queueId,
                       Long dtUicTenantId);
}