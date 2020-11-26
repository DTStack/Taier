package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.Engine;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.domain.TenantResource;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.tenant.TenantResourceVO;
import com.dtstack.engine.api.vo.tenant.UserTenantVO;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.dao.TestConsoleUserDao;
import com.dtstack.engine.dao.TestQueueDao;
import com.dtstack.engine.dao.TestTenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.engine.master.router.login.domain.UserTenant;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author basion
 * @Classname TenantServiceTest
 * @Description unit test for TenantService
 * @Date 2020-11-24 18:05:31
 * @Created basion
 */
@PrepareForTest({AkkaConfig.class, ClientOperator.class,DtUicUserConnect.class})
public class TenantServiceTest extends AbstractTest {

    @Autowired
    private TenantService tenantService;

    @Mock
    private ClientOperator clientOperator;

    @Autowired
    private TestConsoleUserDao consoleUserDao;

    @Autowired
    private TestTenantDao tenantDao;

    @Autowired
    private TestQueueDao queueDao;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        initMock();
    }

    private void initMock() {
        MockitoAnnotations.initMocks(this);
        initMockUserTenants();
        initMockAkka();
        initMockClientOperator();
    }

    private void initMockClientOperator() {
        PowerMockito.mockStatic(ClientOperator.class);
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        when(ClientOperator.getInstance()).thenReturn(clientOperator);
    }

    private void initMockAkka() {
        PowerMockito.mockStatic(AkkaConfig.class);
        when(AkkaConfig.isLocalMode()).thenReturn(true);
    }

    private void initMockUserTenants() {
        PowerMockito.mockStatic(DtUicUserConnect.class);
        List<UserTenant> tenants = Lists.newArrayList();
        UserTenant userTenant = new UserTenant();
        userTenant.setAdmin(true);
        userTenant.setTenantId(1L);
        userTenant.setTenantName("测试租户");
        tenants.add(userTenant);
        when(DtUicUserConnect.getUserTenants(any(),any(),any())).thenReturn(tenants);
        when(DtUicUserConnect.getTenantByTenantId(any(),any(),any())).thenReturn(userTenant);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testPageQuery() {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        Tenant defaultTenant = DataCollection.getData().getDefaultTenant();
        PageResult<List<EngineTenantVO>> pageQuery = tenantService.pageQuery(defaultCluster.getId(), MultiEngineType.HADOOP.getType(), defaultTenant.getTenantName(), 10, 1);
        Assert.assertNotNull(pageQuery);
        Assert.assertTrue(pageQuery.getTotalCount()>0);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testListEngineTenant() {
        Tenant defaultTenant = DataCollection.getData().getDefaultTenant();
        List<EngineTenantVO> listEngineTenant = tenantService.listEngineTenant(defaultTenant.getDtUicTenantId(), MultiEngineType.HADOOP.getType());
        Assert.assertEquals(listEngineTenant.size(),1);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testListTenant() {
        List<UserTenantVO> listTenant = tenantService.listTenant("token");
    }

    @Test()
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testBindingTenant() throws Exception {
        Tenant tenant = initDefaultTenantForBind();
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        tenantService.bindingTenant(tenant.getDtUicTenantId(), defaultCluster.getId(), 0L, "", "");
    }

    private Tenant initDefaultTenantForBind() {
        Tenant tenant = new Tenant();
        tenant.setId(2L);
        tenant.setDtUicTenantId(2L);
        tenant.setTenantName("测试租户2");
        tenant.setTenantDesc("测试租户2");
        tenantDao.insert(tenant);
        return tenant;
    }

    @Test()
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckClusterCanUse() throws Exception {
        Cluster defaultCluster = DataCollection.getData().getDefaultCluster();
        tenantService.checkClusterCanUse(defaultCluster.getId());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAddTenant() {
        Tenant addTenant = tenantService.addTenant(3L, "");
        Assert.assertNotNull(addTenant);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateTenantQueue() {
        tenantService.updateTenantQueue(1L, 1L, 1L, 1L);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testBindingQueue() {
        Queue one = queueDao.getOne();
        tenantService.bindingQueue(one.getId(), 1L, "");
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateTenantTaskResource() {
        tenantService.updateTenantTaskResource(1L, 1L, "");
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTaskResourceLimits() {
        Tenant defaultTenant = DataCollection.getData().getDefaultTenant();
        TenantResource defaultTenantResource = DataCollection.getData().getDefaultTenantResource();
        List<TenantResourceVO> queryTaskResourceLimits = tenantService.queryTaskResourceLimits(defaultTenantResource.getDtUicTenantId().longValue());
        Assert.assertEquals(CollectionUtils.isNotEmpty(queryTaskResourceLimits),true);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryResourceLimitByTenantIdAndTaskType() {
        String queryResourceLimitByTenantIdAndTaskType = tenantService.queryResourceLimitByTenantIdAndTaskType(1L, EComponentType.SPARK.getTypeCode());
        Assert.assertNotNull(queryResourceLimitByTenantIdAndTaskType);
    }
}
