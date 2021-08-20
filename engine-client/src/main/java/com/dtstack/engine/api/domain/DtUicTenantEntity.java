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
public class DtUicTenantEntity extends BaseEntity {

    @ApiModelProperty(notes = "dtUic租户id")
    private Long dtUicTenantId;

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }
}
