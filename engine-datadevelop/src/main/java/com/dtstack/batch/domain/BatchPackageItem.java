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

package com.dtstack.batch.domain;

import com.dtstack.engine.domain.TenantEntity;
import lombok.Data;

@Data
public class BatchPackageItem extends TenantEntity {

    private Long id;

    private Long tenantId;

    private Long projectId;

    private Long packageId;

    /**
     * 存放的是task_id
     */
    private Long itemId;

    /**
     * '资源类型：0-任务，1-表，2-资源，3-函数'
     */
    private Integer itemType;

    private Integer itemInnerType;

    private String publishParam;

    /**
     * 0 未发布 1发布失败 2发布完成
     */
    private Integer status;

    private String log;

    /**
     * 0 一键发布  1导入导出
     */
    private Integer type;

    /**
     * 冗余字段
     */
    private String itemName;

}
