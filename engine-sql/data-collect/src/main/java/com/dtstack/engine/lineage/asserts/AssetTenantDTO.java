package com.dtstack.engine.lineage.asserts;

/**
 * @author chener
 * @Classname AssetTenantDTO
 * @Description TODO
 * @Date 2020/11/30 10:48
 * @Created chener@dtstack.com
 */
public class AssetTenantDTO {
    private Long id;

    private Long dtuicTenantId;

    private String tenantName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
