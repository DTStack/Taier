/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.master.vo.AccountTenantVo;
import com.dtstack.engine.master.vo.AccountVo;
import com.dtstack.engine.master.vo.ClusterVO;
import com.dtstack.engine.master.vo.user.UserVO;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.AccountType;
import com.dtstack.engine.master.utils.Template;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private AccountService accountService;

    @MockBean
    private ClientOperator clientOperator;

    @Before
    public void setup() throws Exception {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setResult(true);
        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> rootMap = new HashMap<>(5);
        rootMap.put("userName", "clusterAccount");
        rootMap.put("userId", "-1100");
        rootMap.put("active", true);
        rootMap.put("createTime", new DateTime().toString());
        users.add(rootMap);

        when(clientOperator.testConnect(any(), any())).thenReturn(componentTestResult);
        when(clientOperator.executeQuery(any(), any(), any(), any())).thenReturn(new ArrayList());
    }


    @Test
    public void addLdap() throws Exception{
        Tenant tenant = DataCollection.getData().getTenant();
        User user = DataCollection.getData().getUser();

        AccountVo firstVo = new AccountVo();
        firstVo.setAccountType(AccountType.LDAP.getVal());
        firstVo.setName("testLdap");
        firstVo.setUsername("test1@dtstack.com");
        firstVo.setBindUserId(user.getDtuicUserId());
        firstVo.setBindTenantId(tenant.getDtUicTenantId());
        firstVo.setUserId(user.getDtuicUserId());
        firstVo.setPassword("");
        firstVo.setEngineType(MultiEngineType.HADOOP.getType());
        firstVo.setModifyUserName("admin@dtstack.com");
        firstVo.setUserId(1L);
        accountService.bindAccountList(Lists.newArrayList(firstVo));
    }

    @Test
    @Transactional
    @Rollback
    public void testAccountCluster() throws Exception {
        //创建集群
        componentService.addOrCheckClusterWithName(accountClusterName);
        ClusterVO dbCluster = clusterService.getClusterByName(accountClusterName);
        Assert.assertNotNull(dbCluster);
        componentService.addOrUpdateComponent(dbCluster.getClusterId(), "{\"jdbcUrl\":\"jdbc:mysql://127.0.0.1:4000/\",\"maxJobPoolSize\":\"\",\"minJobPoolSize\":\"\",\"password\":\"\",\"username\":\"\"}",
                null, "", "", "[{\"key\":\"jdbcUrl\",\"required\":true,\"type\":\"INPUT\",\"value\":\"jdbc:hive2://127.0.0.1:4000/%s\"},{\"key\":\"maxJobPoolSize\",\"required\":false,\"type\":\"INPUT\",\"value\":\"\"},{\"key\":\"minJobPoolSize\",\"required\":false,\"type\":\"INPUT\",\"value\":\"\"},{\"key\":\"password\",\"required\":false,\"type\":\"PASSWORD\",\"value\":\"\"},{\"key\":\"queue\",\"required\":false,\"type\":\"INPUT\",\"value\":\"\"},{\"key\":\"username\",\"required\":false,\"type\":\"INPUT\",\"value\":\"\"}]", EComponentType.TIDB_SQL.getTypeCode(),
                null,"","",true,true,null);
        //添加测试租户
        Tenant tenant = Template.getTenantTemplate();
        tenant.setDtUicTenantId(-1112L);
        tenantDao.insert(tenant);
        tenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(tenant.getId());
        //绑定租户
        tenantService.bindingTenant(tenant.getDtUicTenantId(), dbCluster.getClusterId(), null, "","");

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
        List<UserVO> tenantUnBandList = accountService.getTenantUnBandList(tenant.getDtUicTenantId(), MultiEngineType.TIDB.getType());
        Assert.assertNotNull(tenantUnBandList);
    }
}
