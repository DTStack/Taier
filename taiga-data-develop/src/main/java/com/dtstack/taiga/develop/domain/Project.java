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

package com.dtstack.taiga.develop.domain;

import com.dtstack.taiga.dao.domain.BaseEntity;
import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class Project extends BaseEntity {

    private String projectIdentifier;

    private String projectName;

    private String projectAlias;

    private String projectDesc;

    private Integer status;

    private Long createUserId;

    /**
     * 修改人id
     */
    private Long modifyUserId;

    private Long tenantId;

    private Integer projectType;

    private Long produceProjectId;

    private Integer scheduleStatus;

    /**
     * 是否允许下载查询结果 1-正常 0-禁用
     */
    private Integer isAllowDownload;

    /**
     * 项目创建人
     */
    private String createUserName;

    private Long catalogueId;

    private Integer alarmStatus;

    public Integer getIsAllowDownload() {
        return isAllowDownload;
    }

    public void setIsAllowDownload(Integer isAllowDownload) {
        this.isAllowDownload = isAllowDownload;
    }
}
