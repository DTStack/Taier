package com.dtstack.batch.service.uic.impl;

import com.dtstack.batch.service.uic.impl.domain.UICUserVO;
import com.dtstack.engine.api.pager.PageResult;
import org.springframework.stereotype.Component;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/7/15
 */
@Component
public class UicUserApiClient {

    public Boolean userHasSubProduct(Long uicUserId, Long dtuicTenantId, String subProductCode, String productCode, String token) {
        return true;
    }

    public UICUserVO getByTenantId(Long uicUserId, Long uicTenantId) {
        return new UICUserVO();
    }
}
