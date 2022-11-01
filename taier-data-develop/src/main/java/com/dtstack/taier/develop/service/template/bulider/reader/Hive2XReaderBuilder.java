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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Reader;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.SyncWriteMode;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.hive.Hive2XReader;
import com.dtstack.taier.develop.service.template.hive.Hive2XReaderParam;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.taier.common.util.DataSourceUtils.PASSWORD;
import static com.dtstack.taier.common.util.DataSourceUtils.USERNAME;
import static com.dtstack.taier.develop.service.datasource.impl.DatasourceService.HDFS_DEFAULTFS;
import static com.dtstack.taier.develop.service.develop.impl.DevelopTaskService.HADOOP_CONFIG;

@Component
public class Hive2XReaderBuilder implements DaReaderBuilder {

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private SourceLoaderService sourceLoaderService;

    @Autowired
    private DatasourceService datasourceService;

    @Override
    public void setReaderJson(TaskResourceParam param) {
        Map<String, Object> map = param.getSourceMap();
        if (!map.containsKey("sourceId")) {
            throw new TaierDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
        }

        Long sourceId = Long.parseLong(map.get("sourceId").toString());
        DsInfo source = dsInfoService.getOneById(sourceId);
        map.put("source", source);
        map.put("type", source.getDataTypeCode());
        map.put("dataName", source.getDataName());

        JSONObject json = JSONObject.parseObject(source.getDataJson());
        map.put("defaultFS", json.getString(HDFS_DEFAULTFS));
        String hadoopConfig = json.getString(HADOOP_CONFIG);
        if (StringUtils.isNotBlank(hadoopConfig)) {
            map.put("hadoopConfig", JSONObject.parse(hadoopConfig));
        } else {
            map.put("hadoopConfig", new JSONObject());
        }
        map.put(JDBC_URL, json.getString(JDBC_URL));
        map.put(USERNAME, json.getString(USERNAME));
        map.put(PASSWORD, json.getString(PASSWORD));
        //用于下载kerberos配置
        map.put("sourceId", sourceId);
        map.put("sourceIds", Collections.singletonList(sourceId));
    }

    @Override
    public Reader daReaderBuild(TaskResourceParam param) {
        setReaderJson(param);
        Map<String, Object> sourceMap = param.getSourceMap();
        DsInfo targetSource = (DsInfo) sourceMap.get("source");
       if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) {
           Hive2XReader hive2XReader = new Hive2XReader();
            datasourceService.setSftpConfig(JSONObject.parseObject(targetSource.getDataJson()),param.getTenantId() ,sourceMap,  HADOOP_CONFIG);
           Hive2XReaderParam hive2XReaderParam = JSON.parseObject(JSON.toJSONString(sourceMap), Hive2XReaderParam.class);
            if (StringUtils.isNotBlank(hive2XReaderParam.getTable())) {
                try {
                    //获取hive客户端
                    IClient client = ClientCache.getClient(targetSource.getDataTypeCode());
                    ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(targetSource.getId());
                    Table tableInfo = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(hive2XReaderParam.getTable()).build());
                    hive2XReaderParam.setPath(tableInfo.getPath());
                    hive2XReaderParam.setFileType(tableInfo.getStoreType());
                    List<ColumnMetaDTO> columnMetaData = tableInfo.getColumns();
                    List<String> fullColumnNames = new ArrayList<>();
                    List<String> fullColumnTypes = new ArrayList<>();

                    for (ColumnMetaDTO dto : columnMetaData) {
                        if (!dto.getPart()) {
                            fullColumnNames.add(dto.getKey());
                            fullColumnTypes.add(dto.getType());
                        }
                    }
                    for (int i = 0; i < fullColumnNames.size(); i++) {
                        for (Object col : hive2XReaderParam.getColumn()) {
                            if (fullColumnNames.get(i).equals(((Map<String, Object>) col).get("key"))) {
                                ((Map<String, Object>) col).put("index", i);
                                break;
                            }
                        }
                    }
                    hive2XReader.setFullColumnName(fullColumnNames);
                    hive2XReader.setFullColumnType(fullColumnTypes);
                    if (tableInfo.getDelim() != null) {
                        hive2XReaderParam.setFieldDelimiter(tableInfo.getDelim());
                    }
                } catch (Exception e) {
                    throw new TaierDefineException(String.format("inferHdfsParams error,Caused by: %s", e.getMessage()), e);
                }
            }

            if (hive2XReaderParam.getWriteMode() != null && hive2XReaderParam.getWriteMode().trim().length() != 0) {
                hive2XReaderParam.setWriteMode(SyncWriteMode.tranferHiveMode(hive2XReaderParam.getWriteMode()));
            } else {
                hive2XReaderParam.setWriteMode(SyncWriteMode.HIVE_OVERWRITE.getMode());
            }
           hive2XReader.setColumn(ColumnUtil.getColumns(hive2XReaderParam.getColumn(), PluginName.Hive_R));
           hive2XReader.setWriteMode(hive2XReaderParam.getWriteMode());
           hive2XReader.setDefaultFS(hive2XReaderParam.getDefaultFS());
           hive2XReader.setEncoding(hive2XReaderParam.getEncoding());
           hive2XReader.setFieldDelimiter(hive2XReaderParam.getFieldDelimiter());
           hive2XReader.setFileType(hive2XReaderParam.getFileType());
           hive2XReader.setPath(hive2XReaderParam.getPath().trim());
            if (StringUtils.isNotEmpty(hive2XReaderParam.getPartition())) {
                hive2XReader.setFileName(hive2XReaderParam.getPartition());
                hive2XReader.setPartition(hive2XReaderParam.getPartition());
            } else {
                hive2XReader.setFileName("");
            }
            if (StringUtils.isNotEmpty(hive2XReaderParam.getTable())) {
                hive2XReader.setTable(hive2XReaderParam.getTable());
            }
           hive2XReader.setHadoopConfig(hive2XReaderParam.getHadoopConfig());

           hive2XReader.setUsername(hive2XReaderParam.getUsername());
           hive2XReader.setPassword(hive2XReaderParam.getPassword());
           hive2XReader.setJdbcUrl(hive2XReaderParam.getJdbcUrl());
            if (StringUtils.isNotEmpty(hive2XReaderParam.getJdbcUrl())) {
                JSONObject connection = new JSONObject(2);
                connection.put("jdbcUrl", hive2XReaderParam.getJdbcUrl());
                connection.put("table", StringUtils.isNotBlank(hive2XReaderParam.getTable()) ? Lists.newArrayList(hive2XReaderParam.getTable()) : Lists.newArrayList());
                hive2XReader.setConnection(Lists.newArrayList(connection));
            }
           hive2XReader.setExtralConfig(hive2XReaderParam.getExtralConfig());
           hive2XReader.setSftpConf(hive2XReaderParam.getSftpConf());
           hive2XReader.setRemoteDir(hive2XReaderParam.getRemoteDir());
            return hive2XReader;
        }
        return new Hive2XReader();
    }

    @Override
    public Map<String, Object> getParserSourceMap(Map<String, Object> sourceMap) {
        Hive2XReaderParam hive2XReaderParam = JSON.parseObject(JSON.toJSONString(sourceMap), Hive2XReaderParam.class);
        return JSON.parseObject(JSON.toJSONString(hive2XReaderParam));
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.HIVE;
    }

}
