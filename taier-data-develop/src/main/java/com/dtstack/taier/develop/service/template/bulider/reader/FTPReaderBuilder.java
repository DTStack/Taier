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

package com.dtstack.taier.develop.service.template.bulider.reader;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.*;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.ftp.FTPFileReaderParam;
import com.dtstack.taier.develop.service.template.ftp.FTPFileReader;
import com.dtstack.taier.develop.utils.JsonUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author bnyte
 * @since 1.3.1
 */
@Component
public class FTPReaderBuilder implements DaReaderBuilder {

    private Map<Integer, DaReaderBuilder> builderMap = new HashMap<>();

    @Autowired
    private DsInfoService dsInfoService;

    @PostConstruct
    private void init() {
        if (dsInfoService == null) {
            throw new TaierDefineException("streamDataSourceService should not be null");
        }
        builderMap.put(HalfStructureDaType.FILE.getCode(), new FtpDaBuilder(dsInfoService));
    }

    @Override
    public void setReaderJson(TaskResourceParam param) {
        builderMap.get(MapUtils.getInteger(param.getSourceMap(), HALF_STRUCTURE_DA_TYPE, HalfStructureDaType.FILE.getCode()))
                .setReaderJson(param);
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) throws Exception {
        return builderMap.get(MapUtils.getInteger(param.getSourceMap(), HALF_STRUCTURE_DA_TYPE, HalfStructureDaType.FILE.getCode()))
                .daReaderBuild(param);
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        return builderMap.get(MapUtils.getInteger(sourceMap, HALF_STRUCTURE_DA_TYPE, HalfStructureDaType.FILE.getCode()))
                .getParserSourceMap(sourceMap);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.FTP;
    }

    public static class FtpDaBuilder implements DaReaderBuilder {
        private DsInfoService dsInfoService;

        public FtpDaBuilder(DsInfoService dsInfoService) {
            this.dsInfoService = dsInfoService;
        }

        @Override
        public void setReaderJson(TaskResourceParam param) {
            Map<String, Object> map = param.getSourceMap();
            Long sourceId = Long.parseLong(map.get("sourceId").toString());
            DsInfo source = dsInfoService.getOneById(sourceId);
            map.put("source", source);
            map.put("type", source.getDataTypeCode());
            map.put("dataName", source.getDataName());
        }

        @Override
        public Reader daReaderBuild(TaskResourceParam param) throws Exception {
            setReaderJson(param);
            Map<String, Object> map = param.getSourceMap();
            DsInfo dataSource = (DsInfo) map.get("source");
            FTPFileReaderParam readerParam = JsonUtils.objectToObject(map, FTPFileReaderParam.class);

            FTPFileReader ftpFileReader = new FTPFileReader();

            ftpFileReader.setEncoding(readerParam.getEncoding());
            ftpFileReader.setPath(readerParam.getPath());
            ftpFileReader.setFieldDelimiter(readerParam.getFieldDelimiter());
            ftpFileReader.setFirstLineHeader(ftpFileReader.getFirstLineHeader());

            //populate data source parameters
            JSONObject dataJson = DataSourceUtils.getDataSourceJson(dataSource.getDataJson());
            ftpFileReader.setProtocol(dataJson.getString("protocol"));
            ftpFileReader.setHost(dataJson.getString("host"));
            ftpFileReader.setPort(dataJson.getInteger("port"));
            ftpFileReader.setUsername(dataJson.getString("username"));
            ftpFileReader.setPassword(dataJson.getString("password"));

            // param insert on front-end
            ftpFileReader.setFileType(readerParam.getFileType());
            ftpFileReader.setEncoding(readerParam.getEncoding());
            ftpFileReader.setColumn(readerParam.getColumn());
            ftpFileReader.setFieldDelimiter(readerParam.getFieldDelimiter());
            ftpFileReader.setFirstLineHeader(readerParam.getFirstLineHeader());

            return ftpFileReader;
        }

        @Override
        public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
            try {
                FTPFileReaderParam param = JSONObject.parseObject(JSONObject.toJSONString(sourceMap), FTPFileReaderParam.class, Feature.OrderedField);
                return JSONObject.parseObject(JSONObject.toJSONString(param), Feature.OrderedField);
            } catch (Exception e) {
                throw new TaierDefineException(String.format("getParserSourceMap error,Caused by: %s", e.getMessage()), e);
            }
        }

        @Override
        public DataSourceType getDataSourceType() {
            return DataSourceType.FTP;
        }

    }


}
