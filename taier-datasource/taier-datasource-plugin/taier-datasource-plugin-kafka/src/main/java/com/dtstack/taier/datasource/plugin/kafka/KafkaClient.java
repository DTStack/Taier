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

package com.dtstack.taier.datasource.plugin.kafka;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.kafka.util.KafkaUtil;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KafkaSourceDTO;

/**t
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午4:35 2020/6/2
 * @Description：Kafka 客户端 支持 Kafka 0.9、0.10、0.11、1.x版本
 */
public class KafkaClient extends AbsNoSqlClient {
    @Override
    public Boolean testCon(ISourceDTO sourceDTO) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        return KafkaUtil.checkConnection(kafkaSourceDTO);
    }
}
