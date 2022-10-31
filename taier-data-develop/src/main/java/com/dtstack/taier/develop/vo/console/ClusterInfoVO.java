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

package com.dtstack.taier.develop.vo.console;


import com.dtstack.taier.dao.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

public class ClusterInfoVO extends BaseEntity {

    @ApiModelProperty(notes = "集群名称")
    private String clusterName;

    @ApiModelProperty(notes = "集群id")
    private Long clusterId;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Integer isDeleted = 0;

    @ApiModelProperty(notes = "是否能修改切换metadata")
    private boolean canModifyMetadata = true;

    public boolean isCanModifyMetadata() {
        return canModifyMetadata;
    }

    public void setCanModifyMetadata(boolean canModifyMetadata) {
        this.canModifyMetadata = canModifyMetadata;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
