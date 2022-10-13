package com.dtstack.taier.develop.service.template.hive;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.HiveSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.service.datasource.impl.DsInfoService;
import com.dtstack.taier.develop.service.template.hdfs.HdfsReaderBase;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:16 2019-07-04
 */
@Component
public class HiveReaderBase extends HdfsReaderBase {

    @Autowired
    DsInfoService dataSourceAPIClient;
    private static final String TEXT_FORMAT = "TextOutputFormat";
    private static final String ORC_FORMAT = "OrcOutputFormat";
    private static final String PARQUET_FORMAT = "MapredParquetOutputFormat";
    private static final String PROPERTIES_STR = "Properties: \\[(.*)\\]";
    private static final Pattern PROPERTIES_PATTERN = Pattern.compile(PROPERTIES_STR);
    private static final String COMMA = ",";
    private static final String OUTPUT_FORMAT = "OUTPUTFORMAT";
    private static final String LOCATION = "LOCATION";

    protected String password;
    protected String username;
    protected String jdbcUrl;
    protected String writeTableType;
    protected String table;
    protected String tablesColumn;
    protected String analyticalRules;
    protected String partition;
    protected String writeMode;
    protected String writeStrategy;
    protected Integer maxFileSize;
    protected String fileName;
    protected JSONObject distributeTable;
    protected int partitionType;
    protected String schema;

    protected boolean isPartitioned;
    protected List<String> partitionList = new ArrayList<>();
    protected List<String> partitionedBy = new ArrayList<>();

    protected AtomicBoolean inferred = new AtomicBoolean();
    protected List<String> fullColumnNames = new ArrayList<>();
    protected List<String> fullColumnTypes = new ArrayList<>();

    protected Long sourceId;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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

    public String getTablesColumn() {
        return tablesColumn;
    }

    public void setTablesColumn(String tablesColumn) {
        this.tablesColumn = tablesColumn;
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

    public JSONObject getDistributeTable() {
        return distributeTable;
    }

    public void setDistributeTable(JSONObject distributeTable) {
        this.distributeTable = distributeTable;
    }

    public int getPartitionType() {
        return partitionType;
    }

    public void setPartitionType(int partitionType) {
        this.partitionType = partitionType;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getWriteTableType() {
        return writeTableType;
    }

    public void setWriteTableType(String writeTableType) {
        this.writeTableType = writeTableType;
    }

    public String getAnalyticalRules() {
        return analyticalRules;
    }

    public void setAnalyticalRules(String analyticalRules) {
        this.analyticalRules = analyticalRules;
    }

    public String getWriteStrategy() {
        return writeStrategy;
    }

    public void setWriteStrategy(String writeStrategy) {
        this.writeStrategy = writeStrategy;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    protected void inferHdfsParams() {
        if (inferred.compareAndSet(false, true) && StringUtils.isNotBlank(table)) {
            try {
                //获取hive客户端
                IClient client = ClientCache.getClient(DataSourceType.HIVE.getVal());
                HiveSourceDTO sourceDTO = (HiveSourceDTO) dataSourceAPIClient.getSourceDTO(sourceId);
                Table tableInfo = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(table).build());
                this.path = tableInfo.getPath();
                this.fileType = tableInfo.getStoreType();
                if (tableInfo.getDelim() != null) {
                    this.fieldDelimiter = tableInfo.getDelim();
                }
            } catch (Exception e) {
                throw new RdosDefineException(String.format("inferHdfsParams error,Caused by: %s", e.getMessage()), e);
            }
        }
    }

    @Override
    public String pluginName() {
        return null;
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
