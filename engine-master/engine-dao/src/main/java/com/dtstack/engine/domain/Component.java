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

package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Component extends BaseEntity {

    private Long engineId;

    private String componentName;

    private Integer componentTypeCode;

    private Long clusterId;

    private String hadoopVersion;

    /**
     * 上传配置文件名称
     */
    @ApiModelProperty(notes = "上传配置文件名称")
    private String uploadFileName;

    /**
     * kerberos文件名称
     */
    @ApiModelProperty(notes = "kerberos文件名称")
    private String kerberosFileName;

    /**
     * 存储组件名称
     */
    @ApiModelProperty(notes = "存储组件名称")
    private Integer storeType;

    /**
     * 当前组件是否为默认版本
     */
    private Boolean isDefault;



    @ApiModelProperty(notes = "是否为元数据")
    private Integer isMetadata;

    private Integer deployType;

    public Integer getDeployType() {
        return deployType;
    }

    public void setDeployType(Integer deployType) {
        this.deployType = deployType;
    }

    public Integer getIsMetadata() {
        return isMetadata;
    }

    public void setIsMetadata(Integer isMetadata) {
        this.isMetadata = isMetadata;
    }

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    public String getKerberosFileName() {
        return kerberosFileName;
    }

    public void setKerberosFileName(String kerberosFileName) {
        this.kerberosFileName = kerberosFileName;
    }

    public String getHadoopVersion() {
        return hadoopVersion;
    }

    public void setHadoopVersion(String hadoopVersion) {
        this.hadoopVersion = hadoopVersion;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Integer getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(Integer componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
