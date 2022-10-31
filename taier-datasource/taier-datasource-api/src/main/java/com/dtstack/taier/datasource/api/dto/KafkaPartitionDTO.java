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
 * kafka分区信息传输类
 *
 * @author ：wangchuan
 * date：Created in 3:36 下午 2021/1/10
 * company: www.dtstack.com
 */
@Data
@Builder
public class KafkaPartitionDTO {

    // topic名称
    private final String topic;

    // 分区编号
    private final Integer partition;

    // 副本中的leader
    private final Node leader;

    // 所有的副本
    private final Node[] replicas;

    // isr队列中的副本
    private final Node[] inSyncReplicas;

    @Data
    @Builder
    public static class Node {

        // 副本编号
        private final int id;

        // 副本 string 类型编号
        private final String idString;

        // host
        private final String host;

        // 端口号
        private final int port;

        // 机架名称
        private final String rack;
    }

}