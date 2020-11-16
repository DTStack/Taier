package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author yuebai
 * @date 2020-07-08
 */
@PrepareForTest({AkkaConfig.class, ClientOperator.class,DtUicUserConnect.class})
public class AccountServiceTest extends AbstractTest {

    private String accountClusterName = "accountCluster";

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private TenantService tenantService;

    @Mock
    private ClientOperator clientOperator;

    @Autowired
    private AccountService accountService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(AkkaConfig.class);
        when(AkkaConfig.isLocalMode()).thenReturn(true);
        PowerMockito.mockStatic(ClientOperator.class);
        PowerMockito.mockStatic(DtUicUserConnect.class);

        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        when(ClientOperator.getInstance()).thenReturn(clientOperator);

        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> rootMap = new HashMap<>(5);
        rootMap.put("userName", "clusterAccount");
        rootMap.put("userId", "-1100");
        rootMap.put("active", true);
        rootMap.put("createTime", new DateTime().toString());
        users.add(rootMap);

        when(DtUicUserConnect.getAllUicUsers(any(),any(),any(),any())).thenReturn(users);

        when(clientOperator.testConnect(any(), any())).thenReturn(componentTestResult);
        when(clientOperator.executeQuery(any(), any(), any(), any())).thenReturn(new ArrayList());
    }


    /**
     * @see AccountService#bindAccount(com.dtstack.engine.api.vo.AccountVo)
     * @see AccountService#pageQuery(java.lang.Long, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)
     * @see TenantService#bindingTenant(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.String)
     * @see AccountService#updateBindAccount(com.dtstack.engine.api.vo.AccountTenantVo, java.lang.Long)
     * @see AccountService#unbindAccount(com.dtstack.engine.api.vo.AccountTenantVo, java.lang.Long)
     * @see AccountService#getTenantUnBandList(java.lang.Long, java.lang.String, java.lang.Long, java.lang.Integer)
     * @throws Exception
     */
    @Test
    public void testAccountCluster() throws Exception {
        //创建集群
        componentService.addOrCheckClusterWithName(accountClusterName);
        ClusterVO dbCluster = clusterService.getClusterByName(accountClusterName);
        Assert.assertNotNull(dbCluster);
        componentService.addOrUpdateComponent(dbCluster.getClusterId(), "{\"jdbcUrl\":\"jdbc:mysql://172.16.100.73:4000/\",\"maxJobPoolSize\":\"\",\"minJobPoolSize\":\"\",\"password\":\"\",\"username\":\"\"}",
                null, "", "", "[]", EComponentType.TIDB_SQL.getTypeCode(),null);
        //添加测试租户
        Tenant tenant = DataCollection.getData().getTenant();
        tenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(tenant.getId());
        //绑定租户
        tenantService.bindingTenant(tenant.getDtUicTenantId(), dbCluster.getClusterId(), null, "");

        User user = DataCollection.getData().getUser();
        AccountVo accountVo = new AccountVo();
        accountVo.setUserId(user.getId());
        accountVo.setName("root");
        accountVo.setPassword("password");
        accountVo.setUsername(user.getUserName());
        accountVo.setEngineType(MultiEngineType.TIDB.getType());
        accountVo.setBindTenantId(tenant.getDtUicTenantId());
        accountVo.setBindUserId(user.getDtuicUserId());
        //账号绑定
        accountService.bindAccount(accountVo);
        PageResult<List<AccountVo>> listPageResult = accountService.pageQuery(tenant.getDtUicTenantId(), user.getUserName(), 1, 10, MultiEngineType.TIDB.getType(),null);
        Assert.assertNotNull(listPageResult);
        Assert.assertNotNull(listPageResult.getData());

        AccountTenantVo accountTenantVo = new AccountTenantVo();
        accountTenantVo.setId(listPageResult.getData().get(0).getId());
        accountTenantVo.setName("testModify");
        accountTenantVo.setPassword("modify");
        accountTenantVo.setEngineType(MultiEngineType.TIDB.getType());
        accountTenantVo.setModifyDtUicUserId(-1L);
        //修改账号信息
        accountService.updateBindAccount(accountTenantVo);
        //解绑账号信
        accountTenantVo.setModifyDtUicUserId(-1L);
        accountService.unbindAccount(accountTenantVo);
        List<Map<String, Object>> tenantUnBandList = accountService.getTenantUnBandList(tenant.getDtUicTenantId(), "", user.getDtuicUserId(), MultiEngineType.TIDB.getType());
        Assert.assertNotNull(tenantUnBandList);
    }
}
