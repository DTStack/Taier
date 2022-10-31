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

package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * kafka topic
 *
 * @author ：nanqi
 * date：Created in 下午5:05 2022/9/23
 * company: www.dtstack.com
 */
@Data
@Builder
public class KafkaTopicDTO {
    /**
     * 名称
     */
    private String topicName;

    /**
     * 分区数
     */
    @Builder.Default
    private Integer partitions = 1;

    /**
     * 复制因子
     */
    @Builder.Default
    private Short replicationFactor = 1;
}
