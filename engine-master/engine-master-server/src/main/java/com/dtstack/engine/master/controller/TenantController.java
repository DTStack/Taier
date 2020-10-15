package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.tenant.TenantResourceVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.engine.master.impl.TenantService;
import com.dtstack.engine.master.router.DtHeader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/tenant")
@Api(value = "/node/tenant", tags = {"租户接口"})
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    public PageResult<List<EngineTenantVO>> pageQuery(@DtRequestParam("clusterId") Long clusterId,
                                                      @DtRequestParam("engineType") Integer engineType,
                                                      @DtRequestParam("tenantName") String tenantName,
                                                      @DtRequestParam("pageSize") int pageSize,
                                                      @DtRequestParam("currentPage") int currentPage) {
        return tenantService.pageQuery(clusterId, engineType, tenantName, pageSize, currentPage);
    }

    @RequestMapping(value="/listEngineTenant", method = {RequestMethod.POST})
    @ApiOperation(value = "获取处于统一集群的全部tenant")
    public List<EngineTenantVO> listEngineTenant(@DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                                                 @DtRequestParam("engineType") Integer engineType) {
        return tenantService.listEngineTenant(dtuicTenantId, engineType);
    }

    @RequestMapping(value="/dtToken", method = {RequestMethod.POST})
    public List<UserTenantVO> listTenant(@DtHeader(value = "cookie",cookie = "dt_token") String dtToken) {
        return tenantService.listTenant(dtToken);
    }

    @RequestMapping(value="/bindingTenant", method = {RequestMethod.POST})
    public void bindingTenant(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("clusterId") Long clusterId,
                              @DtRequestParam("queueId") Long queueId, @DtHeader(value = "cookie",cookie = "dt_token") String dtToken) throws Exception {
        tenantService.bindingTenant(dtUicTenantId, clusterId, queueId, dtToken);
    }

    @RequestMapping(value="/bindingQueue", method = {RequestMethod.POST})
    public void bindingQueue(@DtRequestParam("queueId") Long queueId,
                             @DtRequestParam("tenantId") Long dtUicTenantId,
                             @DtRequestParam("taskTypeResourceJson") String taskTypeResourceJson
                             ) {
        tenantService.bindingQueue(queueId, dtUicTenantId,taskTypeResourceJson);
    }

    @ApiOperation(value = "获取租户的设置了任务资源限制的信息列表")
    @RequestMapping(value="/queryTaskResourceLimits", method = {RequestMethod.POST})
    public List<TenantResourceVO> queryTaskResourceLimits(@DtRequestParam("tenantId") Long dtUicTenantId
    ) {
        List<TenantResourceVO> tenantResourceVOS = tenantService.queryTaskResourceLimits(dtUicTenantId);
        return tenantResourceVOS;
    }
}
