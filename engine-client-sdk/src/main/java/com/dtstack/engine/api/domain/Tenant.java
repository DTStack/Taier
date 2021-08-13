package com.dtstack.engine.api.domain;


import io.swagger.annotations.ApiModel;

/**
 * @author sishu.yss
 */
@ApiModel
public class Tenant extends BaseEntity {

    private String tenantName;

    private Long dtuicTenantId;

    private Long createUserId;

    private String tenantDesc;

    private Integer status;


    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getDtUicTenantId() {
        return dtuicTenantId;
    }

    public void setDtUicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getTenantDesc() {
        return tenantDesc;
    }

    public void setTenantDesc(String tenantDesc) {
        this.tenantDesc = tenantDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }
}
