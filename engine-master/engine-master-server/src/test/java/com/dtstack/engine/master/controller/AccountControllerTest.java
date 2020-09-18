package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.enums.AccountType;
import com.dtstack.engine.master.enums.MultiEngineType;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * @author yuebai
 * @date 2020-09-10
 */
public class AccountControllerTest extends AbstractTest {

    @Autowired
    private AccountController accountController;

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

        AccountVo secondVo = new AccountVo();
        secondVo.setAccountType(AccountType.LDAP.getVal());
        secondVo.setName("testLdap2");
        secondVo.setUsername("test2@dtstack.com");
        secondVo.setUserId(user.getDtuicUserId());
        secondVo.setBindUserId(user.getDtuicUserId());
        secondVo.setBindTenantId(tenant.getDtUicTenantId());
        secondVo.setPassword("");
        secondVo.setEngineType(MultiEngineType.HADOOP.getType());

        ArrayList<AccountVo> accountVos = Lists.newArrayList(firstVo, secondVo);
//        accountController.bindAccountList(accountVos,1L);
    }
}
