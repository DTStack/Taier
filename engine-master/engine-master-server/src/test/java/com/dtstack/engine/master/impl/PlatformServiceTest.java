package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.PlatformEventType;
import com.dtstack.engine.master.vo.PlatformEventVO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuebai
 * @date 2020-08-28
 */
public class PlatformServiceTest extends AbstractTest {

    @Autowired
    private PlatformService platformService;

    @Autowired
    private TenantDao tenantDao;

    @Test
    public void testUic(){
        //添加测试租户
        Tenant tenant = DataCollection.getData().getTenant();
        PlatformEventVO platformEventVO = new PlatformEventVO();
        platformEventVO.setTenantName("uic更新");
        platformEventVO.setTenantDesc("tes");
        platformEventVO.setTenantId(tenant.getDtUicTenantId());
        platformEventVO.setEventCode(PlatformEventType.EDIT_TENANT.name());
        platformService.callback(platformEventVO);
        Tenant dbTenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertEquals(dbTenant.getTenantName(),"uic更新");
        platformEventVO.setEventCode(PlatformEventType.DELETE_TENANT.name());
        platformService.callback(platformEventVO);
        Tenant deleteTenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNull(deleteTenant);
    }
}
