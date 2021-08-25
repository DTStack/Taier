package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.tenant.TenantResourceVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/tenant")
@Api(value = "/node/tenant", tags = {"租户接口"})
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ComponentService componentService;

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    public PageResult<List<EngineTenantVO>> pageQuery(@RequestParam("clusterId") Long clusterId,
                                                      @RequestParam("engineType") Integer engineType,
                                                      @RequestParam("tenantName") String tenantName,
                                                      @RequestParam("pageSize") int pageSize,
                                                      @RequestParam("currentPage") int currentPage) {
        return tenantService.pageQuery(clusterId, engineType, tenantName, pageSize, currentPage);
    }

    @RequestMapping(value="/listEngineTenant", method = {RequestMethod.POST})
    @ApiOperation(value = "获取处于统一集群的全部tenant")
    public List<EngineTenantVO> listEngineTenant(@RequestParam("dtuicTenantId") Long dtuicTenantId,
                                                 @RequestParam("engineType") Integer engineType) {
        return tenantService.listEngineTenant(dtuicTenantId, engineType);
    }

    @RequestMapping(value="/dtToken", method = {RequestMethod.POST})
    public List<Tenant> listTenant() {
        return tenantService.listTenant();
    }

    @RequestMapping(value="/bindingTenant", method = {RequestMethod.POST})
    public void bindingTenant(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("clusterId") Long clusterId,
                              @RequestParam("queueId") Long queueId, @RequestParam("dt_token") String dtToken, @RequestParam("namespace") String namespace) throws Exception {
        tenantService.bindingTenant(dtUicTenantId, clusterId, queueId, dtToken,namespace);
    }

    @RequestMapping(value="/bindingQueue", method = {RequestMethod.POST})
    public void bindingQueue(@RequestParam("queueId") Long queueId,
                             @RequestParam("tenantId") Long dtUicTenantId,
                             @RequestParam("taskTypeResourceJson") String taskTypeResourceJson
                             ) {
        tenantService.bindingQueue(queueId, dtUicTenantId,taskTypeResourceJson);
    }

    @ApiOperation(value = "获取租户的设置了任务资源限制的信息列表")
    @RequestMapping(value="/queryTaskResourceLimits", method = {RequestMethod.POST})
    public List<TenantResourceVO> queryTaskResourceLimits(@RequestParam("dtUicTenantId") Long dtUicTenantId
    ) {
        return tenantService.queryTaskResourceLimits(dtUicTenantId);
    }

    @ApiOperation(value = "根据租户id和taskType获取资源限制信息")
    @RequestMapping(value="/queryResourceLimitByTenantIdAndTaskType", method = {RequestMethod.POST})
    public String queryResourceLimitByTenantIdAndTaskType(@RequestParam("dtUicTenantId") Long dtUicTenantId,
                                                                         @RequestParam("taskType") Integer taskType) {
        return tenantService.queryResourceLimitByTenantIdAndTaskType(dtUicTenantId,taskType);
    }


    @RequestMapping(value="/bindNamespace", method = {RequestMethod.POST})
    @ApiOperation(value = "更新namespace")
    public void bindNamespace(@RequestParam("clusterId") Long clusterId, @RequestParam("namespace") String namespace, @RequestParam("queueId") Long queueId, @RequestParam("tenantId") Long dtUicTenantId) {
        componentService.addOrUpdateNamespaces(clusterId,namespace,queueId,dtUicTenantId);
    }
}
