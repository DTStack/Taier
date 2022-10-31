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
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.es.Es7Writer;
import com.dtstack.taier.develop.service.template.es.EsWriterParam;
import com.dtstack.taier.develop.service.template.es.SslConfig;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author daojin
 */
@Component
public class EsWriterBuilder implements DaWriterBuilder {

    @Autowired
    DsInfoService dataSourceAPIClient;

    @Autowired
    DatasourceService datasourceService;
    @Override
    public void setWriterJson(TaskResourceParam param) {
        Long sourceId = Long.parseLong(param.getTargetMap().get("sourceId").toString());
        DsInfo data = dataSourceAPIClient.getOneById(sourceId);
        param.getTargetMap().put("source", data);
        param.getTargetMap().put("dataSourceType", DataSourceType.ES7.getVal());
        param.getTargetMap().putAll(JSON.parseObject(data.getDataJson()));
        List<Long> sourceIds = new ArrayList<>();
        sourceIds.add(sourceId);
        param.getTargetMap().put("sourceIds", sourceIds);

        // ssl配置
        if (StringUtils.isNotEmpty((String) param.getTargetMap().get("keyPath"))) {
            Map<String, String> sftpMap = datasourceService.getSftpMap(param.getTenantId());
            SslConfig sslConfig = SslConfig.setSslConfig(param.getTargetMap(),sftpMap);
            param.getTargetMap().put("sslConfig", sslConfig);
        }
    }

    @Override
    public Writer daWriterBuild(TaskResourceParam param) {
        setWriterJson(param);
        EsWriterParam esWriterParam = JsonUtils.objectToObject(param.getTargetMap(), EsWriterParam.class);
        Es7Writer esWriter = new Es7Writer();
        esWriter.setHosts(Arrays.asList(param.getTargetMap().get("address").toString()));
        esWriter.setIndex(esWriterParam.getIndex());
        esWriter.setBulkAction(esWriterParam.getBulkAction());
        esWriter.setColumn(ColumnUtil.getColumns(esWriterParam.getColumn(), PluginName.ES7_W));
        esWriter.setSourceIds(esWriterParam.getSourceIds());
        esWriter.setUsername(esWriterParam.getUsername());
        esWriter.setPassword(esWriterParam.getPassword());
        esWriter.setSslConfig(esWriterParam.getSslConfig());
        esWriter.setExtralConfig(esWriterParam.getExtralConfig());
        return esWriter;
    }

    @Override
    public Map<String, Object> getParserTargetMap(Map<String, Object> targetMap) {
        EsWriterParam writerParam = JSON.parseObject(JSON.toJSONString(targetMap), EsWriterParam.class);
        return JSON.parseObject(JSON.toJSONString(writerParam));
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.ES7;
    }

}
