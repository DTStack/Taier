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

import java.sql.Timestamp;

@Data
public class BatchPackage extends TenantEntity {

    private String name;

    private String comment;

    private Long createUserId;

    private Long publishUserId;
    /**
     * 发布状态：0-待发布，1-成功，2-失败
     */
    private Integer status;

    private String log;
    /**
     * 导出0 导入1
     */
    private Integer packageType;

    /**
     * 如果式导入 则为导入的压缩包的path
     */
    private String path;

    /**
     * 用于后期判断zip的导入时间是否过期
     */
    private Timestamp pathTime;

}
