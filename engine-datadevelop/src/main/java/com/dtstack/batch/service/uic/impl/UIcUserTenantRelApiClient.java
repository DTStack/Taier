package com.dtstack.batch.service.uic.impl;

import com.dtstack.engine.master.vo.tenant.TenantUsersVO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/7/15
 */
@Component
public class UIcUserTenantRelApiClient {


    public List<TenantUsersVO> findUsersByTenantId(Long dtuicTenantId) {
        return Collections.emptyList();
    }
}
