package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.Tenant;
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
import com.dtstack.engine.master.enums.EComponentType;
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
        initUserForTest();
    }

    private User initUserForTest() {
        User user = new User();
        user.setDtuicUserId(1L);
        user.setUserName("test@dtstack.com");
        user.setEmail("test@dtstack.com");
        user.setStatus(0);
        user.setPhoneNumber("");
        consoleUserDao.insert(user);
        return user;
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
    public void testPageQuery() {
        PageResult<List<EngineTenantVO>> pageQuery = tenantService.pageQuery(1L, 9, "测试租户", 10, 0);
        Assert.assertNotNull(pageQuery);
        Assert.assertEquals(pageQuery.getTotalCount(),1);
    }

    @Test
    public void testListEngineTenant() {
        List<EngineTenantVO> listEngineTenant = tenantService.listEngineTenant(1L, 9);
        Assert.assertEquals(listEngineTenant.size(),1);
    }

    @Test
    public void testListTenant() {
        List<UserTenantVO> listTenant = tenantService.listTenant("token");
    }

    @Test()
    public void testBindingTenant() throws Exception {
        initDefaultTenantForBind();
        tenantService.bindingTenant(2L, 1L, 0L, "", "");
    }

    private void initDefaultTenantForBind() {
        Tenant tenant = new Tenant();
        tenant.setId(2L);
        tenant.setDtUicTenantId(2L);
        tenant.setTenantName("测试租户2");
        tenant.setTenantDesc("测试租户2");
        tenantDao.insert(tenant);
    }

    @Test()
    public void testCheckClusterCanUse() throws Exception {
        tenantService.checkClusterCanUse(1L);
    }

    @Test
    public void testAddTenant() {
        Tenant addTenant = tenantService.addTenant(3L, "");
        Assert.assertNotNull(addTenant);
    }

    @Test
    public void testUpdateTenantQueue() {
        tenantService.updateTenantQueue(1L, 1L, 1L, 1L);
    }

    @Test
    public void testBindingQueue() {
        Queue one = queueDao.getOne();
        tenantService.bindingQueue(one.getId(), 1L, "");
    }

    @Test
    public void testUpdateTenantTaskResource() {
        tenantService.updateTenantTaskResource(1L, 1L, "");
    }

    @Test
    public void testQueryTaskResourceLimits() {
        List<TenantResourceVO> queryTaskResourceLimits = tenantService.queryTaskResourceLimits(1L);
        Assert.assertEquals(CollectionUtils.isNotEmpty(queryTaskResourceLimits),true);
    }

    @Test
    public void testQueryResourceLimitByTenantIdAndTaskType() {
        String queryResourceLimitByTenantIdAndTaskType = tenantService.queryResourceLimitByTenantIdAndTaskType(1L, EComponentType.SPARK.getTypeCode());
        Assert.assertNotNull(queryResourceLimitByTenantIdAndTaskType);
    }
}
