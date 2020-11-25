package com.dtstack.engine.lineage;

/**
 * @author chener
 * @Classname BatchTenant
 * @Description
 * @Date 2020/11/23 14:46
 * @Created chener@dtstack.com
 */
public class BatchTenant {

    private Long id;

    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
