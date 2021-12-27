package com.dtstack.engine.domain.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.dtstack.engine.common.constrant.MP;
import com.dtstack.engine.domain.BaseModel;

/**
 * 带租户属性的数据模型类
 * @description:
 * @author: liuxx
 * @date: 2021/3/15
 */
public class TenantModel<T extends Model<?>> extends BaseModel {

    /**
     * 租户id
     */
    @TableField(value = MP.COLUMN_TENANT_ID)
    private Long tenantId;

    /**
     * dtuic 租户id
     */
    @TableField(value = MP.COLUMN_DTUIC_TENANT_ID)
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
