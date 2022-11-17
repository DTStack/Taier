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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.ftp.FTPWriteParam;
import com.dtstack.taier.develop.service.template.ftp.FTPWriter;
import com.dtstack.taier.develop.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author bnyte
 * @since 1.3.1
 **/
@Component
public class FTPWriterBuilder implements DaWriterBuilder {

    private final DsInfoService dataSourceCenterService;

    private final SourceLoaderService sourceLoaderService;

    private final static String SOURCE_ID_KEY = "sourceId";


    public FTPWriterBuilder(DsInfoService dataSourceCenterService, SourceLoaderService sourceLoaderService) {
        this.dataSourceCenterService = dataSourceCenterService;
        this.sourceLoaderService = sourceLoaderService;
    }

    @Override
    public Writer daWriterBuild(TaskResourceParam param) {
        FTPWriter writer = new FTPWriter();

        Map<String, Object> sourceMap = param.getTargetMap();

        Long sourceId = Long.parseLong(sourceMap.get(SOURCE_ID_KEY).toString());

        DsInfo dsInfo = getDsInfo(sourceId);
        FTPWriteParam writeParam = JsonUtils.objectToObject(sourceMap, FTPWriteParam.class);
        if (Objects.isNull(writeParam)) {
            return writer;
        }

        BeanUtils.copyProperties(writeParam, writer);

        JSONObject dataJson = DataSourceUtils.getDataSourceJson(dsInfo.getDataJson());
        writer.setProtocol(dataJson.getString("protocol"));
        writer.setHost(dataJson.getString("host"));
        writer.setPort(dataJson.getInteger("port"));
        writer.setUsername(dataJson.getString("username"));
        writer.setPassword(dataJson.getString("password"));

        writer.setWriteMode("append");

        return writer;
    }

    @Override
    public Map<String, Object> getParserTargetMap(Map<String, Object> sourceMap) {
        FTPWriteParam param = JsonUtils.objectToObject(sourceMap, FTPWriteParam.class);
        return JsonUtils.objectToMap(param);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.FTP;
    }

    @Override
    public void setWriterJson(TaskResourceParam param) {}

    private DsInfo getDsInfo(Long sourceId) {
        return dataSourceCenterService.getOneById(sourceId);
    }

}
