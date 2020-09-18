package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.AccountType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.impl.AccountService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuebai
 * @date 2020-09-10
 */
public class AccountControllerTest extends AbstractTest {

    @Autowired
    private AccountService accountService;

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
}
