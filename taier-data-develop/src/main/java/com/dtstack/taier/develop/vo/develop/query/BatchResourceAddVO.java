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

@ApiModel("添加资源信息")
public class BatchResourceAddVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "资源名称")
    private String resourceName;

    @ApiModelProperty(value = "租户ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "资源ID")
    private Long id;

    @ApiModelProperty(value = "资源描述")
    private String resourceDesc;

    @ApiModelProperty(value = "资源存放的目录ID")
    private Long nodePid;

    @ApiModelProperty(value = "资源类型", required = true)
    private Integer resourceType;

    @ApiModelProperty(value = "资源原始名称", hidden = true)
    private String originalFilename;

    @ApiModelProperty(value = "资源临时存放地址", hidden = true)
    private String tmpPath;

    @ApiModelProperty(hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "新建资源的用户ID", required = true)
    private Long createUserId;

    @ApiModelProperty(value = "修改资源的用户ID", required = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "项目代号")
    private String productCode;

    /**
     * 实时暂时上传到sftp , 离线上传到hdfs
     */
    @ApiModelProperty(value = "计算类型 0实时，1 离线")
    private Integer computeType = 0;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceDesc() {
        return resourceDesc;
    }

    public void setResourceDesc(String resourceDesc) {
        this.resourceDesc = resourceDesc;
    }

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public String getDtToken() {
        return dtToken;
    }

    public void setDtToken(String dtToken) {
        this.dtToken = dtToken;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }
}
