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

package com.dtstack.taiga.develop.dto.devlop;

import lombok.Data;

/**
 * 资源新增DTO
 */
@Data
public class BatchResourceAddDTO {

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资源ID
     */
    private Long id;

    /**
     * 资源描述
     */
    private String resourceDesc;

    /**
     * 资源存放的目录ID
     */
    private Long nodePid;

    /**
     * 资源类型
     */
    private Integer resourceType;

    /**
     * 资源原始名称
     */
    private String originalFilename;

    /**
     * 资源临时存放地址
     */
    private String tmpPath;

    /**
     * 资源路径
     */
    private String url;

    /**
     * 新建资源的用户ID
     */
    private Long createUserId;
}
