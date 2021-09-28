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

import com.dtstack.engine.domain.TenantProjectEntity;
import lombok.Data;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/6/15 7:36 下午
 */
@Data
public class BatchDataSourceCenter extends TenantProjectEntity {

    /**
     * 数据源中心的id
     */
    private Long dtCenterSourceId;

    /**
     * 新建用户id
     */
    private Long createUserId;


    /**
     * 修改用户id
     */
    private Long modifyUserId;

    /**
     * 是否meta数据源，0=否，1=是
     */
    private Integer isDefault;

}
