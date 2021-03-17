package com.dtstack.engine.master.dao;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.dao.AccountDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/2/19 2:10 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */

public class DaoTest extends AbstractTest {

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Test
    public void testTenantDao(){
        Tenant byDtUicTenantId = tenantDao.getByDtUicTenantId(1L);
        List<Tenant> tenantList = tenantDao.listAllTenantByDtUicTenantIds(Lists.newArrayList(1L, 2L));
        System.out.println(byDtUicTenantId);
        System.out.println(tenantList);
    }

    @Test
    public void testEngineJobCacheDao() {
        EngineJobCache testJobId = engineJobCacheDao.getOne("jobId");

        System.out.println(testJobId);

    }




}
