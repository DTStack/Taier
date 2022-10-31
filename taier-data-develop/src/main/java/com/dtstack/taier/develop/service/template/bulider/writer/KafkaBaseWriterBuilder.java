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

package com.dtstack.taier.develop.service.template.bulider.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.kafka.KafkaBaseWriter;
import com.dtstack.taier.develop.service.template.kafka.KafkaWriterParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 2020/3/5
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
@Component
public abstract class KafkaBaseWriterBuilder implements DaWriterBuilder {

    @Autowired
    private DsInfoService dataSourceAPIClient;

    @Autowired
    private SourceLoaderService sourceLoaderService;

    @Override
    public void setWriterJson(TaskResourceParam param) {
        Map<String, Object> map = param.getTargetMap();
        if (!map.containsKey("sourceId")) {
            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
        }

        Long sourceId = Long.parseLong(map.get("sourceId").toString());
        DsInfo source = dataSourceAPIClient.getOneById(sourceId);
        map.put("source", source);
        Integer sourceType = source.getDataTypeCode();
        map.put("type", sourceType);
        map.put("dataName", source.getDataName());

        JSONObject json = JSONObject.parseObject(source.getDataJson());
        String zkUrls = json.getString("address") == null ? "" : json.getString("address");
        map.put("zookeeperConnect", zkUrls);
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId, null);
        String allBrokersAddress = ClientCache.getKafka(sourceType).getAllBrokersAddress(sourceDTO);
        map.put("brokerList", json.containsKey("brokerList") ? json.getString("brokerList") : allBrokersAddress);
        map.put("topic", map.get("topic") == null ? "" : map.get("topic"));
        map.put("bootstrapServers", allBrokersAddress);
        //如果开启Kerberos 需要设置
        Map<String, Object> kerberosCnf = DataSourceUtils.getOriginKerberosConfig(json, false);
        if (MapUtils.isNotEmpty(kerberosCnf)) {
            map.put("kafkaSettings", DataSourceUtils.initKafkaKerberos(MapUtils.getString(kerberosCnf, DataSourceUtils.SASL_KERBEROS_SERVICE_NAME)));
        }
        // 如果开启 SASL_PLAINTEXT(PLAIN) 认证需要设置
        Map<String, String> plainParam = DataSourceUtils.initKafkaPlainIfOpen(json.getString("username"), json.getString("password"), null);
        if (MapUtils.isNotEmpty(plainParam)) {
            map.put("kafkaSettings", plainParam);
        }
    }

    @Override
    public Writer daWriterBuild(TaskResourceParam param) throws Exception {
        setWriterJson(param);
        Map<String, Object> targetMap = param.getTargetMap();
        targetMap.put("tableFields", param.getSourceMap().get("tableFields"));
        KafkaBaseWriter kafkaWriter = createKafkaWriter(targetMap);
        KafkaWriterParam writerParam = JSON.parseObject(JSON.toJSONString(targetMap), KafkaWriterParam.class);
        Boolean isKafka9 = DataSourceType.KAFKA_09.getVal().equals(getDataSourceType());
        Map<String, String> producerSettings = new HashMap<>();
        if (MapUtils.isNotEmpty(writerParam.getKafkaSettings())) {
            producerSettings.putAll(writerParam.getKafkaSettings());
        }
        producerSettings.put("zookeeper.connect", writerParam.getZookeeperConnect());
        if (isKafka9) {
            kafkaWriter.setBrokerList(writerParam.getBootstrapServers());
        } else {
            producerSettings.put("bootstrap.servers", writerParam.getBootstrapServers());
        }
        kafkaWriter.setProducerSettings(producerSettings);
        kafkaWriter.setDataCompelOrder(writerParam.isDataSequence());
        if (CollectionUtils.isNotEmpty(writerParam.getPartitionKey())) {
            kafkaWriter.setPartitionAssignColumns(writerParam.getPartitionKey());
        }
        kafkaWriter.setTopic(writerParam.getTopic());
        kafkaWriter.setEncoding(writerParam.getExtralConfig());
        kafkaWriter.setTableFields(writerParam.getTableFields());
        return kafkaWriter;
    }

    @Override
    public Map<String, Object> getParserTargetMap(Map<String, Object> sourceMap) {
        KafkaWriterParam readerParam = JSON.parseObject(JSON.toJSONString(sourceMap), KafkaWriterParam.class);
        return JSON.parseObject(JSON.toJSONString(readerParam));
    }

    public abstract KafkaBaseWriter createKafkaWriter(Map<String, Object> sourceMap);
}
