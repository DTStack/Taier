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

package com.dtstack.taiga.dao.domain;

import lombok.Data;

/**
 * @author sishu.yss
 */
@Data
public class BatchResource extends TenantEntity {

    /**
     * 资源路径
     */
    private String url;

    /**
     * 资源类型 1,jar 2 sql
     */
    private Integer resourceType;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 源文件名
     */
    private String originFileName;

    private Long createUserId;

    private Long modifyUserId;

    private Long nodePid;

    private String resourceDesc;

    public BatchResource(String url) {
        this.url = url;
    }

    public BatchResource() {

    }

}
