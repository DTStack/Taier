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

package com.dtstack.taier.datasource.plugin.oceanbase;

import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 14:18 2021/4/21
 */
@Slf4j
public class OceanBaseClient extends AbsRdbmsClient {
    // 获取正在使用数据库
    private static final String CURRENT_DB = "select database()";

    // 模糊查询数据库
    private static final String SHOW_DB_LIKE = "show databases like '%s'";

    private static final String DONT_EXIST = "doesn't exist";

    // 获取指定数据库下的表
    private static final String SHOW_TABLE_BY_SCHEMA_SQL = "select table_name from information_schema.tables where table_schema='%s' and table_type='BASE TABLE' %s";

    // 表名正则匹配模糊查询
    private static final String SEARCH_SQL = " AND table_name REGEXP '%s' ";

    // 限制条数语句
    private static final String LIMIT_SQL = " limit %s ";

    // 创建数据库
    private static final String CREATE_SCHEMA_SQL_TMPL = "create schema %s ";

    // 判断table是否在schema中
    private static final String TABLE_IS_IN_SCHEMA = "select table_name from information_schema.tables where table_schema='%s' and table_name = '%s'";

    // 获取当前版本号
    private static final String SHOW_VERSION = "select version()";

    @Override
    protected ConnFactory getConnFactory() {
        return new OceanBaseConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.OceanBase;
    }

    @Override
    protected String transferTableName(String tableName) {
        return tableName.contains("`") ? tableName : String.format("`%s`", tableName);
    }

    @Override
    protected String doDealType(ResultSetMetaData rsMetaData, Integer los) throws SQLException {
        int columnType = rsMetaData.getColumnType(los + 1);
        // text,mediumtext,longtext的jdbc类型名都是varchar，需要区分。不同的编码下，最大存储长度也不同。考虑1，2，3，4字节的编码

        if (columnType != Types.LONGVARCHAR) {
            return super.doDealType(rsMetaData, los);
        }

        int precision = rsMetaData.getPrecision(los + 1);
        if (precision >= 16383 && precision <= 65535) {
            return "TEXT";
        }

        if (precision >= 4194303 && precision <= 16777215) {
            return "MEDIUMTEXT";
        }

        if (precision >= 536870911 && precision <= 2147483647) {
            return "LONGTEXT";
        }

        return super.doDealType(rsMetaData, los);
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        OceanBaseDownloader oceanBaseDownloader = new OceanBaseDownloader(getCon(source), queryDTO.getSql());
        oceanBaseDownloader.configure();
        return oceanBaseDownloader;
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("show table status");
            while (resultSet.next()) {
                String dbTableName = resultSet.getString(1);

                if (dbTableName.equalsIgnoreCase(queryDTO.getTableName())) {
                    return resultSet.getString(DtClassConsistent.PublicConsistent.COMMENT);
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("get table: %s's information error. Please contact the DBA to check the database、table information.",
                    queryDTO.getTableName()), e);
        } finally {
            DBUtil.closeDBResources(resultSet, statement, connection);
        }
        return "";
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException("Not Support");
    }

    @Override
    protected Map<String, String> getColumnComments(RdbmsSourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Statement statement = null;
        ResultSet rs = null;
        Map<String, String> columnComments = new HashMap<>();
        try {
            statement = connection.createStatement();
            String queryColumnCommentSql =
                    "show full columns from " + transferSchemaAndTableName(sourceDTO, queryDTO);
            rs = statement.executeQuery(queryColumnCommentSql);
            while (rs.next()) {
                String columnName = rs.getString("Field");
                String columnComment = rs.getString("Comment");
                columnComments.put(columnName, columnComment);
            }

        } catch (Exception e) {
            if (e.getMessage().contains(DONT_EXIST)) {
                throw new SourceException(String.format(queryDTO.getTableName() + "table not exist,%s", e.getMessage()), e);
            } else {
                throw new SourceException(String.format("Failed to get the comment information of the field of the table: %s. Please contact the DBA to check the database and table information.",
                        queryDTO.getTableName()), e);
            }
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return columnComments;
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }

    @Override
    protected String getCreateDatabaseSql(String dbName, String comment) {
        return String.format(CREATE_SCHEMA_SQL_TMPL, dbName);
    }

    @Override
    public Boolean isDatabaseExists(ISourceDTO source, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name is not empty");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(SHOW_DB_LIKE, dbName)).build()));
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name is not empty");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(TABLE_IS_IN_SCHEMA, dbName, tableName)).build()));
    }

    /**
     * 获取指定schema下的表，如果没有填schema，默认使用当前schema。支持正则匹配查询、条数限制
     *
     * @param sourceDTO 数据源信息
     * @param queryDTO  查询条件
     * @return
     */
    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        String schema = queryDTO.getSchema();
        // 如果不传scheme，默认使用当前连接使用的schema
        if (StringUtils.isBlank(schema)) {
            log.info("schema is empty，get current used schema!");
            // 获取当前数据库
            try {
                schema = getCurrentDatabase(sourceDTO);
            } catch (Exception e) {
                throw new SourceException(String.format("get current used database error！,%s", e.getMessage()), e);
            }

        }
        log.info("current used schema：{}", schema);
        StringBuilder constr = new StringBuilder();
        if (StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            constr.append(String.format(SEARCH_SQL, queryDTO.getTableNamePattern()));
        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            constr.append(String.format(LIMIT_SQL, queryDTO.getLimit()));
        }
        return String.format(SHOW_TABLE_BY_SCHEMA_SQL, schema, constr.toString());
    }

    /**
     * 处理 schema和tableName，适配schema和tableName中有.的情况
     *
     * @param schema
     * @param tableName
     * @return
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (!tableName.startsWith("`") || !tableName.endsWith("`")) {
            tableName = String.format("`%s`", tableName);
        }
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        if (!schema.startsWith("`") || !schema.endsWith("`")) {
            schema = String.format("`%s`", schema);
        }
        return String.format("%s.%s", schema, tableName);
    }

    @Override
    protected String getVersionSql() {
        return SHOW_VERSION;
    }
}
