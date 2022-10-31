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

package com.dtstack.taier.datasource.plugin.influxdb;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * influxDB 客户端
 *
 * @author ：wangchuan
 * date：Created in 上午10:33 2021/6/7
 * company: www.dtstack.com
 */
public class InfluxDBClient extends AbsNoSqlClient {

    // 获取所有的数据库
    private static final String SHOW_DATABASE = "SHOW databases";

    // 获取当前数据库下面的所有表
    private static final String SHOW_TABLE = "SHOW measurements";

    // 数据预览并限制条数 SQL
    private static final String PREVIEW_LIMIT = "SELECT %s FROM \"%s\" LIMIT %s";

    // 获取表字段信息 SQL
    private static final String SHOW_FIELD = "SHOW field keys from \"%s\"";

    // 获取表 tag 信息 SQL
    private static final String SHOW_TAG = "SHOW tag keys from \"%s\"";

    // time 字段
    private static final String TIME_KEY = "time";

    // time 字段类型
    private static final String TIME_TYPE = "LONG";

    // time 字段类型
    private static final String TAG_TYPE = "STRING";

    @Override
    public Boolean testCon(ISourceDTO source) {
        return InfluxDBConnFactory.testCon(source);
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        InfluxDB influxDB = InfluxDBConnFactory.getClient(source);
        List<String> dbList = InfluxDBUtil.queryWithOneWay(influxDB, SHOW_DATABASE, true);
        return SearchUtil.handleSearchAndLimit(dbList, queryDTO);
    }

    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getTableListBySchema(source, queryDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        InfluxDB influxDB = InfluxDBConnFactory.getClient(InfluxDBUtil.dealDb(source, queryDTO));
        List<String> tableList = InfluxDBUtil.queryWithOneWay(influxDB, SHOW_TABLE, true);
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        InfluxDB influxDB = InfluxDBConnFactory.getClient(InfluxDBUtil.dealDb(source, queryDTO));
        if (StringUtils.isBlank(queryDTO.getTableName())) {
            throw new SourceException("table name cannot be empty.");
        }
        List<ColumnMetaDTO> columnMetaData = getColumnMetaData(source, queryDTO);
        if (CollectionUtils.isEmpty(columnMetaData)) {
            return Collections.emptyList();
        }
        List<String> fieldList = columnMetaData.stream().map(col -> String.format("\"%s\"", col.getKey())).collect(Collectors.toList());
        return InfluxDBUtil.queryWithList(influxDB, String.format(PREVIEW_LIMIT, String.join(",", fieldList), queryDTO.getTableName(), queryDTO.getPreviewNum()), true);
    }

    @Override
    public List<String> getRootDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getAllDatabases(source, queryDTO);
    }

    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO source, SqlQueryDTO queryDTO) {
        InfluxDB influxDB = InfluxDBConnFactory.getClient(InfluxDBUtil.dealDb(source, queryDTO));
        return InfluxDBUtil.queryWithMap(influxDB, queryDTO.getSql(), true);
    }

    @Override
    public Boolean createDatabase(ISourceDTO source, String dbName, String comment) {
        InfluxDB influxDB = InfluxDBConnFactory.getClient(source);
        influxDB.createDatabase(dbName);
        return true;
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTableName())) {
            throw new SourceException("table name cannot be empty.");
        }
        InfluxDB influxDB = InfluxDBConnFactory.getClient(InfluxDBUtil.dealDb(source, queryDTO));
        List<List<Object>> fieldResult = InfluxDBUtil.queryWithList(influxDB, String.format(SHOW_FIELD, queryDTO.getTableName()), true);
        List<List<Object>> tagResult = InfluxDBUtil.queryWithList(influxDB, String.format(SHOW_TAG, queryDTO.getTableName()), true);
        List<ColumnMetaDTO> columnMetas = Lists.newArrayList();
        // 添加 time 字段
        ColumnMetaDTO timeColumn = new ColumnMetaDTO();
        timeColumn.setKey(TIME_KEY);
        timeColumn.setType(TIME_TYPE);
        columnMetas.add(timeColumn);
        addColumnFromResult(columnMetas, fieldResult);
        addColumnFromResult(columnMetas, tagResult);
        return columnMetas;
    }

    /**
     * 从结果集添加 column 字段，包括 tag 和 field
     *
     * @param columnMetas column 集合
     * @param result      查询结果集
     */
    private void addColumnFromResult(List<ColumnMetaDTO> columnMetas, List<List<Object>> result) {
        // result 结果集 第一行为字段名，后面为字段值，长度小于 2 直接返回
        if (CollectionUtils.isEmpty(result) || result.size() < 2) {
            return;
        }
        for (int i = 1; i < result.size(); i++) {
            // 查询 field 返回两列数据分别是 fieldKey 和 fieldType，查询 tag 返回一列数据是 tagKey
            List<Object> row = result.get(i);
            if (CollectionUtils.isNotEmpty(row) && row.size() >= 1 && row.size() <= 2) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                if (row.size() == 2) {
                    columnMetaDTO.setKey(Objects.nonNull(row.get(0)) ? row.get(0).toString() : "");
                    columnMetaDTO.setType(Objects.nonNull(row.get(1)) ? row.get(1).toString() : "");
                } else {
                    columnMetaDTO.setKey(Objects.nonNull(row.get(0)) ? row.get(0).toString() : "");
                    columnMetaDTO.setType(TAG_TYPE);
                }
                columnMetas.add(columnMetaDTO);
            }
        }
    }
}
