package com.dtstack.taier.datasource.plugin.clickhouse;

import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ClickHouseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:57 2020/1/7
 * @Description：clickhouse 客户端
 */
public class ClickhouseClient extends AbsRdbmsClient {

    // 获取正在使用数据库
    private static final String CURRENT_DB = "select currentDatabase()";

    @Override
    protected ConnFactory getConnFactory() {
        return new ClickhouseConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.Clickhouse;
    }

    private static String PARTITION_COLUMN_SQL = "select name,type,comment from system.columns where database = '%s' and table = '%s' and is_in_partition_key = 1";

    private static final String DONT_EXIST = "doesn't exist";

    private static final String SHOW_TABLE_SQL = "show tables %s";

    // 根据schema选表表名模糊查询
    private static final String SEARCH_SQL = " LIKE '%s' ";

    // 获取当前版本号
    private static final String SHOW_VERSION = "select version()";

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
        StringBuilder constr = new StringBuilder();
        if (Objects.nonNull(queryDTO) && StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            constr.append(String.format(SEARCH_SQL, addFuzzySign(queryDTO)));
        }
        // 获取表信息需要通过show tables 语句
        String sql = String.format(SHOW_TABLE_SQL, constr.toString());
        Statement statement = null;
        ResultSet rs = null;
        List<String> tableList = new ArrayList<>();
        try {
            statement = connection.createStatement();
            if (Objects.nonNull(queryDTO) && Objects.nonNull(queryDTO.getLimit())) {
                // 设置最大条数
                statement.setMaxRows(queryDTO.getLimit());
            }
            DBUtil.setFetchSize(statement, queryDTO);
            rs = statement.executeQuery(sql);
            int columnSize = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                tableList.add(rs.getString(columnSize == 1 ? 1 : 2));
            }
        } catch (Exception e) {
            throw new SourceException("get table exception" + e.getMessage(), e);
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getTableList(source, queryDTO);
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        Connection connection = getCon(source);
        ClickHouseSourceDTO clickHouseSourceDTO = (ClickHouseSourceDTO) source;
        String sql = String.format(PARTITION_COLUMN_SQL,clickHouseSourceDTO.getSchema(),queryDTO.getTableName());
        Statement statement = null;
        ResultSet rs = null;
        List<ColumnMetaDTO> columnList = new ArrayList<>();
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                columnMetaDTO.setKey(rs.getString(1));
                columnMetaDTO.setType(rs.getString(2));
                columnMetaDTO.setComment(rs.getString(3));
                columnMetaDTO.setPart(true);
                columnList.add(columnMetaDTO);
            }
        } catch (Exception e) {
            throw new SourceException("get table exception" + e.getMessage(), e);
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return columnList;
    }

    @Override
    public List<ColumnMetaDTO> getFlinkColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Statement statement = null;
        ResultSet rs = null;
        List<ColumnMetaDTO> columns = new ArrayList<>();
        try {
            statement = connection.createStatement();
            String queryColumnSql = "select * from " + queryDTO.getTableName()
                    + " where 1=2";
            rs = statement.executeQuery(queryColumnSql);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                columnMetaDTO.setKey(rsMetaData.getColumnName(i + 1));
                String type = rsMetaData.getColumnTypeName(i + 1);
                int columnType = rsMetaData.getColumnType(i + 1);
                int precision = rsMetaData.getPrecision(i + 1);
                int scale = rsMetaData.getScale(i + 1);
                //clickhouse类型转换
                String flinkSqlType = ClickhouseAdapter.mapColumnTypeJdbc2Java(columnType, precision, scale);
                if (StringUtils.isNotEmpty(flinkSqlType)) {
                    type = flinkSqlType;
                }
                columnMetaDTO.setType(type);
                // 获取字段精度
                if (columnMetaDTO.getType().equalsIgnoreCase("decimal")
                        || columnMetaDTO.getType().equalsIgnoreCase("float")
                        || columnMetaDTO.getType().equalsIgnoreCase("double")
                        || columnMetaDTO.getType().equalsIgnoreCase("numeric")) {
                    columnMetaDTO.setScale(rsMetaData.getScale(i + 1));
                    columnMetaDTO.setPrecision(rsMetaData.getPrecision(i + 1));
                }
                columns.add(columnMetaDTO);
            }
            return columns;

        } catch (SQLException e) {
            if (e.getMessage().contains(DONT_EXIST)) {
                throw new SourceException(queryDTO.getTableName() + "table not exist" + e.getMessage(), e);
            } else {
                throw new SourceException(String.format("Failed to get meta information for the fields of table :%s. Please contact the DBA to check the database table information.%s", queryDTO.getTableName(), e.getMessage()), e);
            }
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        ClickHouseSourceDTO clickHouseSourceDTO = (ClickHouseSourceDTO) source;
        ClickHouseDownloader clickHouseDownloader = new ClickHouseDownloader(getCon(clickHouseSourceDTO), queryDTO.getSql(), clickHouseSourceDTO.getSchema());
        clickHouseDownloader.configure();
        return clickHouseDownloader;
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }

    @Override
    protected String getVersionSql() {
        return SHOW_VERSION;
    }
}
