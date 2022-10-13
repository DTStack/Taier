package com.dtstack.taier.datasource.plugin.phoenix5;

import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Phoenix5SourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:30 2020/7/8
 * @Description：Phoenix 客户端
 */
public class PhoenixClient extends AbsRdbmsClient {

    /**
     * 展示指定 SCHEMA 的表
     */
    private static final String SHOW_TABLES_SCHEMA = "SELECT TABLE_NAME FROM SYSTEM.CATALOG WHERE TABLE_TYPE = 'u'AND TABLE_SCHEM = '%s'";

    /**
     * 展示指定 没有schema（默认schema）下的表：default下的表schema为null
     */
    private static final String SHOW_TABLES_IN_DEFAULT = "SELECT DISTINCT TABLE_NAME FROM SYSTEM.CATALOG WHERE TABLE_TYPE = 'u' AND TABLE_SCHEM IS NULL";

    /**
     * 获取所有的schema
     */
    private static final String SHOW_SCHEMA = "SELECT DISTINCT TABLE_SCHEM FROM SYSTEM.CATALOG WHERE TABLE_TYPE = 'u' AND TABLE_SCHEM IS NOT NULL";

    /**
     * 默认schema
     */
    private static final String DEFAULT_SCHEMA = "default";

    @Override
    protected ConnFactory getConnFactory() {
        return new PhoenixConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.Phoenix;
    }

    /**
     * 处理schema和表名
     *
     * @param schema
     * @param tableName
     * @return
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        // schema为空直接返回
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        if (!tableName.startsWith("\"") || !tableName.endsWith("\"")) {
            tableName = String.format("\"%s\"", tableName);
        }
        if (!schema.startsWith("\"") || !schema.endsWith("\"")) {
            schema = String.format("\"%s\"", schema);
        }
        return String.format("%s.%s", schema, tableName);
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        String tableName = queryDTO.getTableName();
        if (tableName.contains(".")) {
            tableName = tableName.split("\\.")[1];
        }
        tableName = tableName.replace("\"", "");

        ResultSet resultSet = null;

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getTables(null, null, tableName, null);
            while (resultSet.next()) {
                String comment = resultSet.getString(DtClassConsistent.PublicConsistent.REMARKS);
                return comment;
            }
        } catch (Exception e) {
            throw new SourceException(String.format("get table: %s's information error. Please contact the DBA to check the database、table information.",
                    queryDTO.getTableName()), e);
        } finally {
            DBUtil.closeDBResources(resultSet, null, connection);
        }
        return "";
    }


    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Phoenix5SourceDTO rdbmsSourceDTO = (Phoenix5SourceDTO) sourceDTO;
        ResultSet rs = null;
        List<String> tableList = new ArrayList<>();
        try {
            DatabaseMetaData meta = connection.getMetaData();
            if (null == queryDTO) {
                rs = meta.getTables(null, rdbmsSourceDTO.getSchema(), null, null);
            } else {
                rs = meta.getTables(null, rdbmsSourceDTO.getSchema(), null, DBUtil.getTableTypes(queryDTO));
            }
            while (rs.next()) {
                if (StringUtils.isBlank(rdbmsSourceDTO.getSchema()) && StringUtils.isNotBlank(rs.getString(2))) {
                    // 返回 schema.tableName形式 TODO 待FlinkX支持 "schema"."tableName"时修改
                    tableList.add(String.format("%s.%s", rs.getString(2), rs.getString(3)));
                } else {
                    tableList.add(rs.getString(3));
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("Get database table exception,%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(rs, null, connection);
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getSchema())) {
            return getTableList(source, queryDTO);
        }
        return super.getTableListBySchema(source, queryDTO);
    }

    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        // 对默认schema特殊处理
        if (DEFAULT_SCHEMA.equalsIgnoreCase(queryDTO.getSchema())) {
            return SHOW_TABLES_IN_DEFAULT;
        }
        return String.format(SHOW_TABLES_SCHEMA, queryDTO.getSchema());
    }

    @Override
    protected String getShowDbSql() {
        return SHOW_SCHEMA;
    }
}
