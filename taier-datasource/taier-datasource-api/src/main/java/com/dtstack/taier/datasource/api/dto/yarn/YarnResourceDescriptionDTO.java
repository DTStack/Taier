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

import java.util.List;

/**
 * yarn 资源
 *
 * @author ：wangchuan
 * date：Created in 下午5:11 2022/3/17
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YarnResourceDescriptionDTO {

    private int totalNode;
    private int totalMemory;
    private int totalCores;
    private List<QueueDescription> queueDescriptions;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueDescription {
        private String queueName;
        private String queuePath;
        private String capacity;
        private String maximumCapacity;
        private String queueState;
        private List<QueueDescription> childQueues;
    }
}
