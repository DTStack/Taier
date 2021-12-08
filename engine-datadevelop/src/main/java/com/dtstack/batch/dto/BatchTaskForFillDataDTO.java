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

package com.dtstack.batch.dto;

import com.dtstack.engine.domain.TenantEntity;
import com.dtstack.engine.domain.User;
import lombok.Data;

/**
 * 补数据任务所需的任务参数
 * -- ps 简化了batchTask
 *
 * @author sanyue
 * @date 2019/1/18
 */
@Data
public class BatchTaskForFillDataDTO extends TenantEntity {

    private String name;

    private Integer taskType;

    private Long createUserId;

    private Long ownerUserId;

    private User createUser;

    private User ownerUser;

    private String scheduleConf;

}
