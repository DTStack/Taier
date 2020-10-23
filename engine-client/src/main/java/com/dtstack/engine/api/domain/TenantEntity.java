package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname TenantEntity
 * @Description
 * @Date 2020/10/22 20:10
 * @Created chener@dtstack.com
 */
@ApiModel
public class TenantEntity extends BaseEntity {
    @ApiModelProperty(notes = "租户id")
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
