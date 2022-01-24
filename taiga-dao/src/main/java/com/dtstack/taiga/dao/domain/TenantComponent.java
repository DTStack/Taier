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
 * 项目引擎类型关联表
 * Date: 2019/6/1
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class TenantComponent extends BaseEntity {

    /**
     * 租户Id
     */
    private Long tenantId;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 组件标识
     */
    private String componentIdentity;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 修改人id
     */
    private Long modifyUserId;

}
