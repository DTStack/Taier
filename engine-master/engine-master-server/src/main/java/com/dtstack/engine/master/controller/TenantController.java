package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.EngineTenantVO;
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
    public List listTenant(@RequestParam("dtToken") String dtToken) {
        return tenantService.listTenant(dtToken);
    }

    @RequestMapping(value="/bindingTenant", method = {RequestMethod.POST})
    public void bindingTenant(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("clusterId") Long clusterId,
                              @RequestParam("queueId") Long queueId, @RequestParam("dtToken") String dtToken) throws Exception {
        tenantService.bindingTenant(dtUicTenantId, clusterId, queueId, dtToken);
    }

    @RequestMapping(value="/bindingQueue", method = {RequestMethod.POST})
    public void bindingQueue(@RequestParam("queueId") Long queueId,
                             @RequestParam("tenantId") Long dtUicTenantId) {
        tenantService.bindingQueue(queueId, dtUicTenantId);
    }
}
