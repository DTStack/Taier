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

package com.dtstack.taiga.develop.utils.develop.sync.template;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.ITable;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.Table;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.develop.enums.develop.SourceDTOType;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author jingzhen
 */
@Component
public abstract class HiveBase extends HDFSBase {

    private static final String PATTERN_STR = "Storage\\(Location: (.*), InputFormat: (.*), OutputFormat: (.*), Serde: (.*)";
    private static final String PROPERTIES_STR = "Properties: \\[(.*)\\]";

    protected String password;
    protected String username;
    protected String jdbcUrl;
    protected String table;
    protected String partition;
    protected String writeMode;
    protected String fileName;
    protected Integer dataSourceType;

    /**
     * 所属db
     */
    protected String dbName;

    /**
     * 是否是事务表
     */
    protected Boolean isTransaction;

    protected boolean isPartitioned;
    protected List<String> partitionList = new ArrayList<>();
    protected List<String> partitionedBy = new ArrayList<>();

    protected AtomicBoolean inferred = new AtomicBoolean();
    protected List<String> fullColumnNames = new ArrayList<>();
    protected List<String> fullColumnTypes = new ArrayList<>();

    protected Long sourceId;
    protected JSONObject kerberosConfig;

    public List<String> getPartitionList() {
        return partitionList;
    }

    public void setPartitionList(List<String> partitionList) {
        this.partitionList = partitionList;
    }

    /**
     * 是否默认数据源  只有 hive1.x或hive2.x 且为默认数据源的时候 这个地方才为true
     * 如果是true  忽略 传入的dataType 直接使用spark 数据源类型
     */
    protected Boolean isDefaultSource;

    public Boolean getIsDefaultSource() {
        return isDefaultSource;
    }

    public void setIsDefaultSource(Boolean defaultSource) {
        isDefaultSource = defaultSource;
    }

    public JSONObject getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(JSONObject kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Boolean getTransaction() {
        return isTransaction;
    }

    public void setTransaction(Boolean transaction) {
        isTransaction = transaction;
    }

    protected void inferHdfsParams() {
        if (inferred.compareAndSet(false, true) && StringUtils.isNotBlank(table)) {
            DataSourceType sourceType = DataSourceType.getSourceType(dataSourceType);
            JSONObject dataJson = new JSONObject();
            dataJson.put(SourceDTOType.JDBC_URL, jdbcUrl);
            dataJson.put(SourceDTOType.JDBC_USERNAME, username);
            dataJson.put(SourceDTOType.JDBC_PASSWORD, password);
            ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, sourceType.getVal(), kerberosConfig, Maps.newHashMap());
            IClient client = ClientCache.getClient(sourceType.getVal());
            Table tableInfo = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(this.table).build());
            List<ColumnMetaDTO> columnMetaData = tableInfo.getColumns();
            for (ColumnMetaDTO dto : columnMetaData) {
                if (!dto.getPart()) {
                    fullColumnNames.add(dto.getKey());
                    fullColumnTypes.add(dto.getType());
                } else {
                    isPartitioned = true;
                    partitionedBy.add(dto.getKey());
                }
            }
            if (isPartitioned) {
                ITable tableClient = ClientCache.getTable(sourceType.getVal());
                List<String> partitions = tableClient.showPartitions(sourceDTO, table);
                partitions.forEach(bean -> {
                    partitionList.add(bean);
                });
            }
            this.dbName = tableInfo.getDb();
            this.path = tableInfo.getPath();
            this.fileType = tableInfo.getStoreType();
            this.fieldDelimiter = tableInfo.getDelim();
            this.isTransaction = tableInfo.getIsTransTable();
        }

        for (int i = 0; i < fullColumnNames.size(); i++) {
            for (Object col : column) {
                if (fullColumnNames.get(i).equals(((Map<String, Object>) col).get("key"))) {
                    ((Map<String, Object>) col).put("index", i);
                    break;
                }
            }
        }
    }
}
