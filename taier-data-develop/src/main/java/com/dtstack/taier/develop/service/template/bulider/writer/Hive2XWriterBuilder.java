package com.dtstack.taier.develop.service.template.bulider.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.common.template.Writer;
import com.dtstack.taier.develop.dto.devlop.TaskResourceParam;
import com.dtstack.taier.develop.enums.develop.PartitionType;
import com.dtstack.taier.develop.enums.develop.RdbmsDaType;
import com.dtstack.taier.develop.enums.develop.SyncCreateTableMode;
import com.dtstack.taier.develop.enums.develop.SyncWriteMode;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.PluginName;
import com.dtstack.taier.develop.service.template.hdfs.HdfsWriter;
import com.dtstack.taier.develop.service.template.hive.Hive2XWriter;
import com.dtstack.taier.develop.service.template.hive.Hive2XWriterParam;
import com.dtstack.taier.develop.utils.develop.sync.util.ColumnUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.taier.common.util.DataSourceUtils.PASSWORD;
import static com.dtstack.taier.common.util.DataSourceUtils.USERNAME;
import static com.dtstack.taier.develop.service.datasource.impl.DatasourceService.HDFS_DEFAULTFS;
import static com.dtstack.taier.develop.service.develop.impl.DevelopTaskService.HADOOP_CONFIG;
import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.JDBC_URL;
import static com.dtstack.taier.develop.service.template.bulider.reader.DaReaderBuilder.RDBMS_DA_TYPE;

@Component
public class Hive2XWriterBuilder implements DaWriterBuilder {

    @Autowired
    DsInfoService dataSourceAPIClient;

    @Autowired
    DatasourceService datasourceService;

    public static final String WRITE_TABLE_TYPE = "writeTableType";

    @Override
    public void setWriterJson(TaskResourceParam param) {
        Map<String, Object> map = param.getTargetMap();
        if (!map.containsKey("sourceId")) {
            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
        }

        Long sourceId = Long.parseLong(map.get("sourceId").toString());
        DsInfo source = dataSourceAPIClient.getOneById(sourceId);
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
        map.put("sourceIds", Arrays.asList(sourceId));
        Boolean sourceEs7 = param.getSourceMap().get("dataSourceType") == null ? false : Objects.equals(DataSourceType.ES7.getVal(), Integer.valueOf(param.getSourceMap().get("dataSourceType").toString()));
        String partition = param.getTargetMap().getOrDefault("partition","").toString();
        List<String> partList = null;
        if (StringUtils.isNotBlank(partition)) {
            String[] parts = partition.split("/");
            partList = new ArrayList<>();
            for (String part : parts) {
                String[] partDetail = part.split("=");
                String partCol = partDetail[0];
                partList.add(partCol);
            }
        }
    }

    @Override
    public Writer daWriterBuild(TaskResourceParam param) {
        setWriterJson(param);
        Map<String, Object> targetMap = param.getTargetMap();
        DsInfo targetSource = (DsInfo) targetMap.get("source");
        if (Objects.equals(param.getTaskType(), EScheduleJobType.DATA_ACQUISITION.getVal())) {
            HdfsWriter hdfsWriter = new HdfsWriter();
            Map<String, Object> sourceMap = param.getSourceMap();
            int sourceType = Integer.parseInt(String.valueOf(sourceMap.get("type")));
            DataSourceType dataSourceType = DataSourceType.getSourceType(sourceType);
            DsInfo source = null;
            SyncCreateTableMode writeTableType = SyncCreateTableMode.MANUAL_SELECTION;
            if (targetMap.containsKey(WRITE_TABLE_TYPE)) {
                writeTableType = SyncCreateTableMode.getByMode(MapUtils.getIntValue(targetMap, "writeTableType"));
                // 根据写入表是否为自动建表区分读取表结构数据源来源
                source = SyncCreateTableMode.AUTO_CREATE.equals(writeTableType) ? (DsInfo) sourceMap.get("source") : (DsInfo) targetMap.get("source");
            }
            if (null != source && null != source.getDataJson()) {
                boolean isPartition = StringUtils.isNotEmpty(MapUtils.getString(sourceMap, "distributeTable"));
                Object tablesName = SyncCreateTableMode.AUTO_CREATE.equals(writeTableType) ? MapUtils.getObject(sourceMap, "table") : MapUtils.getObject(targetMap, "table");
                if (isPartition) {
                    tablesName = sourceMap.get("distributeTable");
                    targetMap.put("distributeTable", sourceMap.get("distributeTable"));
                }
                JSONObject tablesColumn = dataSourceAPIClient.getTablesColumn(source, tablesName, isPartition, MapUtils.getString(sourceMap, "schema"));
                boolean pavingData = MapUtils.getBooleanValue(sourceMap, "pavingData");
                if (pavingData) {
                    tablesColumn = dataSourceAPIClient.dealTablesColumnPavingData(tablesColumn);
                }
                if (SyncCreateTableMode.AUTO_CREATE.equals(writeTableType)) {
                    tablesColumn = dataSourceAPIClient.dealTablesColumnAutoCreate(tablesColumn);
                }
                targetMap.put("tablesColumn", tablesColumn.toJSONString());
                //mysql binlog && oracle logminer需要加schema
                if (dataSourceType.equals(DataSourceType.MySQL) && Objects.equals(RdbmsDaType.Binlog.getCode(), MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.Binlog.getCode()))) {
                    String dataBase = dataSourceAPIClient.getDBFromJdbc(MapUtils.getLong(param.getSourceMap(), "sourceId"));
                    targetMap.put("schema", dataBase);
                }
                if (DataSourceType.Oracle.equals(dataSourceType) && Objects.equals(RdbmsDaType.LOGMINER.getCode(), MapUtils.getInteger(param.getSourceMap(), RDBMS_DA_TYPE, RdbmsDaType.LOGMINER.getCode()))) {
                    String schema = MapUtils.getString(param.getSourceMap(), "schema");
                    targetMap.put("schema", schema);
                }
                dataSourceAPIClient.setFtpConf(targetMap, targetSource, param.getTenantId(), "hadoopConfig");
            }
            Hive2XWriterParam writerParam = JSON.parseObject(JSON.toJSONString(targetMap), Hive2XWriterParam.class);

            if (StringUtils.isNotBlank(writerParam.getTable())) {
                try {
                    //获取hive客户端
                    IClient client = ClientCache.getClient(targetSource.getDataTypeCode());
                    Table tableInfo = client.getTable(dataSourceAPIClient.getSourceDTO(targetSource.getId()), SqlQueryDTO.builder().tableName(writerParam.getTable()).build());
                    writerParam.setPath(tableInfo.getPath());
                    writerParam.setFileType(tableInfo.getStoreType());
                    if (tableInfo.getDelim() != null) {
                        writerParam.setFieldDelimiter(tableInfo.getDelim());
                    }
                } catch (Exception e) {
                    throw new RdosDefineException(String.format("inferHdfsParams error,Caused by: %s", e.getMessage()), e);
                }
            }

            if (writerParam.getWriteMode() != null && writerParam.getWriteMode().trim().length() != 0) {
                writerParam.setWriteMode(SyncWriteMode.tranferHiveMode(writerParam.getWriteMode()));
            } else {
                writerParam.setWriteMode(SyncWriteMode.HIVE_OVERWRITE.getMode());
            }

            hdfsWriter.setWriteMode(writerParam.getWriteMode());
            hdfsWriter.setFileName(writerParam.getFileName());
            hdfsWriter.setDefaultFS(writerParam.getDefaultFS());
            hdfsWriter.setEncoding(writerParam.getEncoding());
            hdfsWriter.setFieldDelimiter(writerParam.getFieldDelimiter());
            hdfsWriter.setFileType(writerParam.getFileType());
//            hdfsWriter.setHadoopConfig(writerParam.getHadoopConfig());
            hdfsWriter.setInterval(writerParam.getInterval());
            hdfsWriter.setPath(writerParam.getPath().trim());
            if (StringUtils.isNotEmpty(writerParam.getPartition())) {
                hdfsWriter.setFileName(writerParam.getPartition());
                hdfsWriter.setPartition(writerParam.getPartition());
            } else {
                hdfsWriter.setFileName("");
            }
            if (StringUtils.isNotEmpty(writerParam.getTable())) {
                hdfsWriter.setTable(writerParam.getTable());
            }

            hdfsWriter.setCharsetName(writerParam.getEncoding());
            writerParam.getHadoopConfig().put("fs.defaultFS", writerParam.getDefaultFS());
            writerParam.getHadoopConfig().put("fs.hdfs.impl.disable.cache", "true");
            writerParam.getHadoopConfig().put("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
            hdfsWriter.setHadoopConfig(writerParam.getHadoopConfig());

            hdfsWriter.setUsername(writerParam.getUsername());
            hdfsWriter.setPassword(writerParam.getPassword());
            hdfsWriter.setJdbcUrl(writerParam.getJdbcUrl());
            hdfsWriter.setSchema(writerParam.getSchema());
            hdfsWriter.setTablesColumn(writerParam.getTablesColumn().replaceAll("\"type\":\"VARCHAR\"|\"type\":\"varchar\"","\"type\":\"STRING\""));
            // 补充Hive 数据同步特有字段;
            hdfsWriter.setAnalyticalRules(StringUtils.isEmpty(writerParam.getAnalyticalRules()) ? null : "DATASYNC".toLowerCase() + "_" + writerParam.getAnalyticalRules());
            hdfsWriter.setMaxFileSize(writerParam.getMaxFileSize());
            hdfsWriter.setDistributeTable(writerParam.getDistributeTable() == null ? null : writerParam.getDistributeTable().toJSONString());
            hdfsWriter.setPartitionType(PartitionType.fromTypeValue(writerParam.getPartitionType()).getName());
            hdfsWriter.setSourceIds(writerParam.getSourceIds());
            return hdfsWriter;
        } else if (Objects.equals(param.getTaskType(), EScheduleJobType.SYNC.getVal())) {
            Hive2XWriter hdfsWriter = new Hive2XWriter();
            datasourceService.setSftpConfig(JSONObject.parseObject(targetSource.getDataJson()),param.getTenantId() ,targetMap,  HADOOP_CONFIG);
            Hive2XWriterParam writerParam = JSON.parseObject(JSON.toJSONString(targetMap), Hive2XWriterParam.class);
            if (StringUtils.isNotBlank(writerParam.getTable())) {
                try {
                    //获取hive客户端
                    IClient client = ClientCache.getClient(targetSource.getDataTypeCode());
                    Table tableInfo = client.getTable(dataSourceAPIClient.getSourceDTO(targetSource.getId()), SqlQueryDTO.builder().tableName(writerParam.getTable()).build());
                    writerParam.setPath(tableInfo.getPath());
                    writerParam.setFileType(tableInfo.getStoreType());
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
                        for (Object col : writerParam.getColumn()) {
                            if (fullColumnNames.get(i).equals(((Map<String, Object>) col).get("key"))) {
                                ((Map<String, Object>) col).put("index", i);
                                break;
                            }
                        }
                    }
                    hdfsWriter.setFullColumnName(fullColumnNames);
                    hdfsWriter.setFullColumnType(fullColumnTypes);
                    if (tableInfo.getDelim() != null) {
                        writerParam.setFieldDelimiter(tableInfo.getDelim());
                    }
                } catch (Exception e) {
                    throw new RdosDefineException(String.format("inferHdfsParams error,Caused by: %s", e.getMessage()), e);
                }
            }

            if (writerParam.getWriteMode() != null && writerParam.getWriteMode().trim().length() != 0) {
                writerParam.setWriteMode(SyncWriteMode.tranferHiveMode(writerParam.getWriteMode()));
            } else {
                writerParam.setWriteMode(SyncWriteMode.HIVE_OVERWRITE.getMode());
            }
            hdfsWriter.setColumn(ColumnUtil.getColumns(writerParam.getColumn(), PluginName.HIVE_W));
            hdfsWriter.setWriteMode(writerParam.getWriteMode());
            hdfsWriter.setDefaultFS(writerParam.getDefaultFS());
            hdfsWriter.setEncoding(writerParam.getEncoding());
            hdfsWriter.setFieldDelimiter(writerParam.getFieldDelimiter());
            hdfsWriter.setFileType(writerParam.getFileType());
            hdfsWriter.setPath(writerParam.getPath().trim());
            if (StringUtils.isNotEmpty(writerParam.getPartition())) {
                hdfsWriter.setFileName(writerParam.getPartition());
                hdfsWriter.setPartition(writerParam.getPartition());
            } else {
                hdfsWriter.setFileName("");
            }
            if (StringUtils.isNotEmpty(writerParam.getTable())) {
                hdfsWriter.setTable(writerParam.getTable());
            }
            hdfsWriter.setHadoopConfig(writerParam.getHadoopConfig());

            hdfsWriter.setUsername(writerParam.getUsername());
            hdfsWriter.setPassword(writerParam.getPassword());
            hdfsWriter.setJdbcUrl(writerParam.getJdbcUrl());
            if (StringUtils.isNotEmpty(writerParam.getJdbcUrl())) {
                JSONObject connection = new JSONObject(2);
                connection.put("jdbcUrl", writerParam.getJdbcUrl());
                connection.put("table", StringUtils.isNotBlank(writerParam.getTable()) ? Lists.newArrayList(writerParam.getTable()) : Lists.newArrayList());
                hdfsWriter.setConnection(Lists.newArrayList(connection));
            }
            hdfsWriter.setExtralConfig(writerParam.getExtralConfig());
            hdfsWriter.setSftpConf(writerParam.getSftpConf());
            hdfsWriter.setRemoteDir(writerParam.getRemoteDir());
            hdfsWriter.setSourceIds(writerParam.getSourceIds());
            return hdfsWriter;
        }
        return new HdfsWriter();
    }

    @Override
    public Map<String, Object> getParserTargetMap(Map<String, Object> targetMap) {
        Hive2XWriterParam writerParam = JSON.parseObject(JSON.toJSONString(targetMap), Hive2XWriterParam.class);
        return JSON.parseObject(JSON.toJSONString(writerParam));
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.HIVE;
    }

}
