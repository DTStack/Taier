package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;


@ApiModel
public class TenantProjectEntity extends BaseEntity {


    private Long tenantId;

    private Long projectId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

}
