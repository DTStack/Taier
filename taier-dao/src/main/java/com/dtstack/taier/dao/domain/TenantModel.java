package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.dtstack.taier.common.constant.MP;

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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

}
