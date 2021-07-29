package com.dtstack.batch.web.controller;

import com.dtstack.batch.domain.Tenant;
import com.dtstack.batch.mapstruct.vo.TenantMapstructTransfer;
import com.dtstack.batch.service.impl.TenantService;
import com.dtstack.batch.vo.TenantVO;
import com.dtstack.batch.web.task.vo.query.BatchTaskGetAppVO;
import com.dtstack.batch.web.tenant.vo.query.BatchTenantAddOrUpdateVO;
import com.dtstack.batch.web.tenant.vo.query.BatchTenantGetUserTenantsVO;
import com.dtstack.batch.web.tenant.vo.result.TenantResultVO;
import com.dtstack.batch.web.tenant.vo.result.TenantUpdateResultVO;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "租户管理", tags = {"租户管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;


    @ApiOperation(value = "新增和编辑租户信息", response = Tenant.class)
    @PostMapping(value = "addOrUpdate")
    public R<TenantUpdateResultVO> addOrUpdate(@RequestBody BatchTenantAddOrUpdateVO tenantVO) {

        return new APITemplate<TenantUpdateResultVO>() {
            @Override
            protected TenantUpdateResultVO process() throws BizException {
                Tenant tenant = tenantService.addOrUpdate(TenantMapstructTransfer.INSTANCE.tenantAddOrUpdateVOToTenant(tenantVO));
                return TenantMapstructTransfer.INSTANCE.tenantToTenantUpdateResultVO(tenant);
            }
        }.execute();
    }


    @ApiOperation("根据名称获取租户")
    @PostMapping(value = "getUserTenants")
    public R<List<TenantResultVO>> getUserTenants(@RequestBody(required = false) BatchTenantGetUserTenantsVO tenantVO) {

        return new APITemplate<List<TenantResultVO>>() {
            @Override
            protected List<TenantResultVO> process() throws BizException {
                List<TenantVO> userTenants = tenantService.getUserTenants(tenantVO.getTenantName(), tenantVO.getDtToken());
                return TenantMapstructTransfer.INSTANCE.tenantVOToTenantResultVO(userTenants);
            }
        }.execute();
    }

    @ApiOperation("获取任务可依赖的产品列表")
    @PostMapping(value = "getAppList")
    public R<List<Integer>> getAppList(@RequestBody(required = false) BatchTaskGetAppVO vo){
        return new APITemplate<List<Integer>>() {
            @Override
            protected List<Integer> process() throws BizException {
                return tenantService.getAppList(vo.getTenantId(), vo.getDtuicUserId(), vo.getDtToken(), vo.getProductCode());
            }
        }.execute();
    }

    @ApiOperation("返回当前租户是否支持 standeAlone模式")
    @PostMapping(value = "isStandeAlone")
    public R<Boolean> isStandeAlone(@RequestBody(required = false) BatchTaskGetAppVO vo){
        return new APITemplate<Boolean>() {
            @Override
            protected Boolean process() throws BizException {
                return tenantService.isStandAlone(vo.getTenantId());
            }
        }.execute();
    }
}

