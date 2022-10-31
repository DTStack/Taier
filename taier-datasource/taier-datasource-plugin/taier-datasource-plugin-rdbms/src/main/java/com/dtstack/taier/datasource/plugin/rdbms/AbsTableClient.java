package com.dtstack.taier.datasource.plugin.rdbms;

import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.MathUtil;
import com.dtstack.taier.datasource.api.client.ITable;
import com.dtstack.taier.datasource.api.dto.UpsertColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * <p>表操作相关抽象客户端</>
 *
 * @author ：wangchuan
 * date：Created in 2:08 下午 2020/12/12
 * company: www.dtstack.com
 */
@Slf4j
public abstract class AbsTableClient implements ITable {

    // client
    private final IClient client = ClientCache.getClient(getSourceType().getVal());

    // 获取所有分区
    protected static final String SHOW_PARTITIONS_SQL = "show partitions %s";

    /**
     * 获取数据源类型
     *
     * @return 数据源类型
     */
    protected abstract DataSourceType getSourceType();

    /**
     * 执行sql查询
     *
     * @param sourceDTO 数据源信息
     * @param sql       查询sql
     * @return 查询结果
     */
    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO sourceDTO, String sql) {
        Connection connection = client.getCon(sourceDTO);
        try {
            return DBUtil.executeSql(connection, sql);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    /**
     * 执行sql，不需要结果
     *
     * @param sourceDTO 数据源信息
     * @param sql       需要执行的 sql
     * @return 执行成功与否
     */
    @Override
    public Boolean executeSqlWithoutResultSet(ISourceDTO sourceDTO, String sql) {
        executeQuery(sourceDTO, sql);
        return true;
    }

    @Override
    public List<String> showPartitions(ISourceDTO source, String tableName) {
        log.info("get table ：{} partition", tableName);
        if (StringUtils.isBlank(tableName)) {
            throw new SourceException("Table name cannot be empty！");
        }
        List<Map<String, Object>> result = executeQuery(source, String.format(SHOW_PARTITIONS_SQL, tableName));
        List<String> partitions = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(result)) {
            result.forEach(rs -> partitions.add(MapUtils.getString(rs, "partition")));
        }
        return partitions;
    }

    @Override
    public Boolean dropTable(ISourceDTO source, String tableName) {
        log.info("drop table：{}", tableName);
        if (StringUtils.isBlank(tableName)) {
            throw new SourceException("Table name cannot be empty！");
        }
        String dropTableSql = getDropTableSql(tableName);
        return executeSqlWithoutResultSet(source, dropTableSql);
    }

    /**
     * 获取删除表的sql
     * @param tableName 表名
     * @return sql
     */
    protected String getDropTableSql(String tableName){
        return String.format("drop table if exists `%s`", tableName);
    };

    @Override
    public Boolean renameTable(ISourceDTO source, String oldTableName, String newTableName) {
        log.info("rename table：{} to {}", oldTableName, newTableName);
        if (StringUtils.isBlank(oldTableName) || StringUtils.isBlank(newTableName)) {
            throw new SourceException("Table name cannot be empty！");
        }
        String renameTableSql = String.format("alter table %s rename to %s", oldTableName, newTableName);
        return executeSqlWithoutResultSet(source, renameTableSql);
    }

    @Override
    public Boolean alterTableParams(ISourceDTO source, String tableName, Map<String, String> params) {
        log.info("alter table：{} params：{}", tableName, params);
        if (StringUtils.isBlank(tableName)) {
            throw new SourceException("Table name cannot be empty！");
        }
        if (params == null || params.isEmpty()) {
            throw new SourceException("Table parameter cannot be empty！");
        }
        List<String> tableProperties = Lists.newArrayList();
        params.forEach((key, val) -> tableProperties.add(String.format("'%s'='%s'", key, val)));
        String alterTableParamsSql = String.format("alter table %s set tblproperties (%s)", tableName, StringUtils.join(tableProperties, "."));
        return executeSqlWithoutResultSet(source, alterTableParamsSql);
    }

    @Override
    public Long getTableSize(ISourceDTO source, String schema, String tableName) {
        log.info("get ，schema：{}，table size：{}", schema, tableName);
        if (StringUtils.isBlank(tableName)) {
            throw new SourceException("Table name cannot be empty！");
        }
        String tableSizeSql = getTableSizeSql(schema, tableName);
        log.info("get table size sql：{}", tableSizeSql);
        List<Map<String, Object>> result = executeQuery(source, tableSizeSql);
        if (CollectionUtils.isEmpty(result) || MapUtils.isEmpty(result.get(0))) {
            throw new SourceException("Obtaining Table Occupied Storage Information exception");
        }
        Object tableSize = result.get(0).values().stream().findFirst().orElseThrow(() -> new SourceException("Get the table occupation storage information exception"));
        return MathUtil.getLongVal(tableSize);
    }

    @Override
    public Boolean isView(ISourceDTO source, String schema, String tableName) {
        throw new SourceException("The method is not supported");
    }

    /**
     * 添加表字段
     *
     * @param source
     * @param columnMetaDTO
     * @return
     */
    protected Boolean addTableColumn(ISourceDTO source, UpsertColumnMetaDTO columnMetaDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    /**
     * 生成修改表列名的sql
     *
     * @param source
     * @param columnMetaDTO
     * @return
     */
    protected Boolean updateTableColumn(ISourceDTO source, UpsertColumnMetaDTO columnMetaDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    /**
     * 生成删除表列名的sql
     *
     * @param source
     * @param columnMetaDTO
     * @return
     */
    protected Boolean deleteTableColumn(ISourceDTO source, UpsertColumnMetaDTO columnMetaDTO) {
      throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    /**
     * 处理schema和表名
     *
     * @param schema
     * @param tableName
     * @return
     */
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        return String.format("%s.%s", schema, tableName);
    }

    protected String transformTableColumn(String tableName, String column) {
        return tableName + "." + column;
    }


    /**
     * 检查参数并设置schema
     * @param source 数据源信息
     * @param schema  schema名称
     * @param tableName 表名
     */
    protected void checkParamAndSetSchema (ISourceDTO source, String schema, String tableName) {
        if (StringUtils.isBlank(tableName)) {
            throw new SourceException("Table name cannot be empty");
        }
        if (StringUtils.isNotBlank(schema)) {
            RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
            rdbmsSourceDTO.setSchema(schema);
        }
    }

    /**
     * 获取表占用存储的sql
     * @param schema schema信息
     * @param tableName 表名
     * @return 占用存储sql
     */
    protected String getTableSizeSql(String schema, String tableName) {
       throw new SourceException("This data source does not support obtaining tables occupying storage");
    }
}
