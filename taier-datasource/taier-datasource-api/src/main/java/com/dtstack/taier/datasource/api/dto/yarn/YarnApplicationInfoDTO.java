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

package com.dtstack.taier.datasource.api.dto.yarn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * yarn app info DTO
 *
 * @author ：wangchuan
 * date：Created in 上午9:37 2022/3/17
 * company: www.dtstack.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YarnApplicationInfoDTO {

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务applicationId
     */
    private String applicationId;

    /**
     * 任务状态
     */
    private YarnApplicationStatus status;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date finishTime;
}
