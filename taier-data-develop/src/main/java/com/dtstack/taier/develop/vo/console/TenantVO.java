package com.dtstack.taier.develop.vo.console;

import io.swagger.annotations.ApiModelProperty;

public class TenantVO {

    @ApiModelProperty(notes = "租户名称")
    private String tenantName;

    @ApiModelProperty(notes = "租户id")
    private Long tenantId;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
