package com.dtstack.engine.datadevelop.dao.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.dtstack.engine.datadevelop.dao.BaseMapperField;

/**
 * company: www.dtstack.com
 * author: toutian，quanyue
 * create: 2021/5/10
 */
public class TenantModel<T extends Model<?>> extends BaseModel<T> {

    /**
     * 租户id
     */
    @TableField(value = BaseMapperField.COLUMN_TENANT_ID)
    private Long tenantId;

    /**
     * dtuic 租户id
     */
    @TableField(value = BaseMapperField.COLUMN_DTUIC_TENANT_ID)
    private Long dtuicTenantId;


    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }
}
