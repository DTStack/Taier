package com.dtstack.engine.domain;

import com.dtstack.dtcenter.base.domain.TenantProjectEntity;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class AppTenantEntity extends TenantProjectEntity {

    private Long dtuicTenantId = 0L;


    /**
     * RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8)
     */
    private Integer appType;

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }
}
