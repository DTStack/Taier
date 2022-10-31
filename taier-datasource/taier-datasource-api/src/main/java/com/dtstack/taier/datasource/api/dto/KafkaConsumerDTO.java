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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * kafka 消费者组相关参数
 *
 * @author ：wangchuan
 * date：Created in 下午4:26 2021/4/13
 * company: www.dtstack.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumerDTO {

    // 消费者组
    private String groupId;

    // 当前分区
    private Integer partition;

    // 当前消费 offset
    private Long currentOffset;

    // 属于 topic
    private String topic;

    // broker host
    private String brokerHost;

    // 未消费数据
    private Long lag;

    // 当前分区 leader 最后一次提交的offset 也就是当前分区的最大偏移量
    private Long logEndOffset;
}
