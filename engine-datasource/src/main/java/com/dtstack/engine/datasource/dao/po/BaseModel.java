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

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.dtstack.engine.datasource.dao.BaseMapperField;

import java.io.Serializable;
import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian，quanyue
 * create: 2021/5/10
 */
public class BaseModel<T extends Model<?>> extends Model<T> implements Serializable {

    @TableId(value = BaseMapperField.COLUMN_ID, type = IdType.AUTO)
    private Long id;
    /**
     * 创建人ID
     */
    @TableField(value = BaseMapperField.COLUMN_CREATE_BY, fill = FieldFill.INSERT)
    private Long createUserId;
    /**
     * 更新人ID
     */
    @TableField(value = BaseMapperField.COLUMN_UPDATE_BY, fill = FieldFill.UPDATE)
    private Long modifyUserId;
    /**
     * 逻辑删除标志位
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(BaseMapperField.COLUMN_DELETED)
    private boolean deleted;
    /**
     * 创建时间
     */
    @TableField(value = BaseMapperField.COLUMN_CREATE_AT, fill = FieldFill.INSERT)
    private Date gmtCreate;
    /**
     * 更新时间
     */
    @TableField(value = BaseMapperField.COLUMN_UPDATE_AT, fill = FieldFill.UPDATE)
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }


}
