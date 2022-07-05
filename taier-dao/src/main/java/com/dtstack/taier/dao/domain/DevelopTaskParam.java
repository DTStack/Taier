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

package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * Reason:
 * Date: 2017/6/7
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
@TableName("develop_task_param")
public class DevelopTaskParam extends BaseEntity {

    @TableField("task_id")
    private Long taskId;

    @TableField("type")
    private Integer type;

    @TableField("param_name")
    private String paramName;

    @TableField("param_command")
    private String paramCommand;

    @TableId(value="id", type= IdType.AUTO)
    private Long id = 0L;

    @TableField("is_deleted")
    private Integer isDeleted = 0;

    /**
     * 实体创建时间
     */
    @TableField(
            value = "gmt_create",
            fill = FieldFill.INSERT
    )
    private Timestamp gmtCreate;
    /**
     * 实体修改时间
     */
    @TableField(
            value = "gmt_modified",
            fill = FieldFill.INSERT_UPDATE
    )
    private Timestamp gmtModified;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamCommand() {
        return paramCommand;
    }

    public void setParamCommand(String paramCommand) {
        this.paramCommand = paramCommand;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getIsDeleted() {
        return isDeleted;
    }

    @Override
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    @Override
    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public Timestamp getGmtModified() {
        return gmtModified;
    }

    @Override
    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }
}
