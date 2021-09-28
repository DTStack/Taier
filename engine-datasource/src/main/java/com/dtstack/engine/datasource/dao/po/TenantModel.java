/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.datasource.dao.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.dtstack.engine.datasource.dao.BaseMapperField;

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
