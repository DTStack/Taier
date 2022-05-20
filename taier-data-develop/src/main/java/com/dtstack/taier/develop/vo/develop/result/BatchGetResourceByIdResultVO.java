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

package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

@ApiModel("获取资源返回信息")
public class BatchGetResourceByIdResultVO {

    @ApiModelProperty(value = "创建用户")
    private BatchUserBaseResultVO createUser;

    @ApiModelProperty(value = "修改用户")
    private BatchUserBaseResultVO modifyUser;

    @ApiModelProperty(value = "资源路径", example = "hdfs://ns1/rdos/batch/***")
    private String url;

    @ApiModelProperty(value = "资源描述", example = "我是描述")
    private String resourceDesc;

    @ApiModelProperty(value = "资源类型", example = "1")
    private Integer resourceType;

    @ApiModelProperty(value = "资源名称", example = "我是资源")
    private String resourceName;

    @ApiModelProperty(value = "源文件名称", example = "我是源文件名")
    private String originFileName;

    @ApiModelProperty(value = "创建用户 ID", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "修改用户的ID", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "父文件夹id", example = "1")
    private Long nodePid;

    @ApiModelProperty(value = "租户id", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "ID", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "计算类型 0实时，1 离线")
    private Integer computeType = 0;

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public BatchUserBaseResultVO getCreateUser() {
        return createUser;
    }

    public void setCreateUser(BatchUserBaseResultVO createUser) {
        this.createUser = createUser;
    }

    public BatchUserBaseResultVO getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(BatchUserBaseResultVO modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResourceDesc() {
        return resourceDesc;
    }

    public void setResourceDesc(String resourceDesc) {
        this.resourceDesc = resourceDesc;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
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

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
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
