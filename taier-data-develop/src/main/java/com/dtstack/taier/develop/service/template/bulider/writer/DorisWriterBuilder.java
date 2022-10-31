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
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.DorisRestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.doris.DorisWriteParam;
import com.dtstack.taier.develop.service.template.doris.DorisWriter;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.JDBC_PASSWORD;
import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.JDBC_USERNAME;
import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.URL;

/**
 * @author leon
 * @date 2022-10-11 17:13
 **/
@Component
public class DorisWriterBuilder implements DaWriterBuilder {

    private final DsInfoService dataSourceCenterService;

    private final SourceLoaderService sourceLoaderService;

    private final static String SOURCE_ID_KEY = "sourceId";


    public DorisWriterBuilder(DsInfoService dataSourceCenterService, SourceLoaderService sourceLoaderService) {
        this.dataSourceCenterService = dataSourceCenterService;
        this.sourceLoaderService = sourceLoaderService;
    }

    @Override
    public Writer daWriterBuild(TaskResourceParam param) {
        DorisWriter writer = new DorisWriter();

        Map<String, Object> sourceMap = param.getTargetMap();

        Long sourceId = Long.parseLong(sourceMap.get(SOURCE_ID_KEY).toString());

        DorisWriteParam writerParam = PublicUtil.objectToObject(sourceMap, DorisWriteParam.class);
        if (Objects.isNull(writerParam)) {
            return writer;
        }

        DsInfo dsInfo = getDsInfo(sourceId);
        JSONObject dataJson = JSON.parseObject(dsInfo.getDataJson());
        IClient client = ClientCache.getClient(dsInfo.getDataTypeCode());
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(dsInfo.getId());
        Optional.ofNullable(writerParam.getSchema()).ifPresent(s -> {
            ((DorisRestfulSourceDTO) sourceDTO).setSchema(s);
        });
        Table tableInfo = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(writerParam.getTable()).build());

        writer.setUsername(JsonUtils.getStringDefaultEmpty(dataJson, JDBC_USERNAME));
        writer.setPassword(JsonUtils.getStringDefaultEmpty(dataJson, JDBC_PASSWORD));
        writer.setFeNodes(Lists.newArrayList(JsonUtils.getStringDefaultEmpty(dataJson, URL)));
        writerParam.getColumn().forEach(c -> {
            c.setName(c.getKey());
            c.setKey(null);
        });
        Optional.of(writerParam).map(DorisWriteParam::getColumn).ifPresent(writer::setColumn);
        writer.setSourceIds(Lists.newArrayList(sourceId));
        writer.setDatabase(writerParam.getSchema());
        writer.setFieldDelimiter(tableInfo.getDelim());
        writer.setTable(writerParam.getTable());
        return writer;
    }

    @Override
    public Map<String, Object> getParserTargetMap(Map<String, Object> sourceMap) {
        DorisWriteParam param = JsonUtils.objectToObject(sourceMap, DorisWriteParam.class);
        return JsonUtils.objectToMap(param);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.DorisRestful;
    }

    @Override
    public void setWriterJson(TaskResourceParam param) {}

    private DsInfo getDsInfo(Long sourceId) {
        return dataSourceCenterService.getOneById(sourceId);
    }

}
