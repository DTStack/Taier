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

package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

@ApiModel("读写锁信息")
public class BatchReadWriteLockBaseVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "上一个持有锁的用户名", example = "1")
    private String lastKeepLockUserName;

    @ApiModelProperty(value = "检查结果", example = "1")
    private Integer result = 0;

    @ApiModelProperty(value = "是否持有锁", hidden = true)
    private Boolean isGetLock = false;

    @ApiModelProperty(value = "锁名称", hidden = true)
    private String lockName;

    @ApiModelProperty(value = "修改的用户", hidden = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "乐观锁", example = "1")
    private Integer version;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long relationId;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private String type;

    @ApiModelProperty(value = "ID", hidden = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;

    public String getLastKeepLockUserName() {
        return lastKeepLockUserName;
    }

    public void setLastKeepLockUserName(String lastKeepLockUserName) {
        this.lastKeepLockUserName = lastKeepLockUserName;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Boolean getGetLock() {
        return isGetLock;
    }

    public void setGetLock(Boolean getLock) {
        isGetLock = getLock;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
