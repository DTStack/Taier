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

package com.dtstack.taier.datasource.plugin.saphana;

import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SchemaUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SapHana1SourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * sap hana 客户端，获取表需要有 SYS schema 的权限
 *
 * @author ：wangchuan
 * date：Created in 上午10:13 2021/12/30
 * company: www.dtstack.com
 */
@Slf4j
public class SapHanaClient extends AbsRdbmsClient {

    // 获取正在使用 schema
    private static final String CURRENT_DB = " SELECT CURRENT_SCHEMA FROM DUMMY ";

    // schema 是否存在
    private static final String DB_EXISTS = " SELECT * FROM SYS.SCHEMAS WHERE SCHEMA_NAME = '%s' ";

    // 查询表注释
    private static final String TABLE_COMMENT = " SELECT COMMENTS FROM SYS.TABLES WHERE SCHEMA_NAME = '%s' AND TABLE_NAME = '%s' LIMIT 1 ";

    // 查询表基本 sql
    private static final String SHOW_TABLE_BASE = " SELECT TABLE_NAME, TABLE_NAME_UPPER FROM (%s) WHERE 1 = 1 ";

    // 查询视图基本 sql
    private static final String SHOW_VIEW_BASE = " SELECT VIEW_NAME, VIEW_NAME_UPPER FROM (%s)  WHERE 1 = 1 ";

    // 获取指定数据库下的表
    private static final String SHOW_TABLE_BY_SCHEMA_SQL = " SELECT UPPER(TABLE_NAME) AS TABLE_NAME_UPPER, TABLE_NAME, SCHEMA_NAME FROM SYS.TABLES WHERE SCHEMA_NAME = '%s' ";

    // 获取全部表
    private static final String SHOW_SCHEMA_TABLE_SQL = " SELECT UPPER('\"'||SCHEMA_NAME||'\".\"'||TABLE_NAME||'\"') AS TABLE_NAME_UPPER, '\"'||SCHEMA_NAME||'\".\"'||TABLE_NAME||'\"' AS TABLE_NAME FROM SYS.TABLES ";

    // 获取全部视图
    private static final String SHOW_SCHEMA_VIEW_SQL = " SELECT UPPER('\"'||SCHEMA_NAME||'\".\"'||VIEW_NAME||'\"') AS VIEW_NAME_UPPER, '\"'||SCHEMA_NAME||'\".\"'||VIEW_NAME||'\"' AS VIEW_NAME FROM SYS.VIEWS ";

    // 获取指定数据库下的视图
    private static final String SHOW_VIEW_BY_SCHEMA_SQL = " SELECT UPPER(VIEW_NAME) AS VIEW_NAME_UPPER, VIEW_NAME, SCHEMA_NAME FROM SYS.VIEWS WHERE SCHEMA_NAME = '%s' ";

    // 表名模糊查询
    private static final String TABLE_SEARCH_SQL = " AND TABLE_NAME_UPPER LIKE UPPER('%s') ";

    // 视图模糊查询
    private static final String VIEW_SEARCH_SQL = " AND VIEW_NAME_UPPER LIKE UPPER('%s') ";

    // 限制条数语句
    private static final String LIMIT_SQL = " LIMIT %s ";

    // 创建数据库
    private static final String CREATE_SCHEMA_SQL_TMPL = " CREATE SCHEMA %s ";

    // 判断表是否在指定 schema 中
    private static final String TABLE_IS_IN_SCHEMA = " SELECT TABLE_NAME from SYS.TABLES WHERE SCHEMA_NAME = '%s' AND TABLE_NAME = '%s' ";

    // 判断视图是否在指定 schema 中
    private static final String VIEW_IS_IN_SCHEMA = " SELECT VIEW_NAME from SYS.VIEWS WHERE SCHEMA_NAME = '%s' AND VIEW_NAME = '%s' ";

    // 获取所有的 schema
    private static final String SHOW_SCHEMA = " SELECT SCHEMA_NAME FROM SYS.SCHEMAS ";

    private static final String CREATE_TABLE_SQL = "call get_object_definition('%s','%s');";

    @Override
    protected ConnFactory getConnFactory() {
        return new SapHanaConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.SAP_HANA1;
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        return getTableListBySchema(sourceDTO, queryDTO);
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        String schema = getSchema(sourceDTO, queryDTO);
        // 构建查询条件
        SqlQueryDTO customQueryDTO = SqlQueryDTO.builder()
                .sql(String.format(TABLE_COMMENT, schema, queryDTO.getTableName()))
                .build();

        // 查询表注释信息
        List<Map<String, Object>> result = executeQuery(sourceDTO, customQueryDTO);
        if (CollectionUtils.isEmpty(result)) {
            return StringUtils.EMPTY;
        }
        return result
                .stream().findAny().map(Map::values).orElse(Collections.emptyList())
                .stream().filter(Objects::nonNull).findAny().orElse(StringUtils.EMPTY)
                .toString();

    }

    @Override
    public IDownloader getDownloader(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) throws Exception {
        SapHana1SourceDTO sapHanaSourceDTO = (SapHana1SourceDTO) sourceDTO;
        SapHanaDownloader sapHanaDownloader = new SapHanaDownloader(getCon(sourceDTO), queryDTO.getSql(), sapHanaSourceDTO.getSchema());
        sapHanaDownloader.configure();
        return sapHanaDownloader;
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
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(DB_EXISTS, dbName)).build()));
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name is not empty");
        }
        String executeSql = String.format(TABLE_IS_IN_SCHEMA, dbName, tableName)
                + " UNION "
                + String.format(VIEW_IS_IN_SCHEMA, dbName, tableName);
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(executeSql).build()));
    }

    /**
     * 获取指定schema下的表，如果没有填schema，默认使用当前schema。支持正则匹配查询、条数限制
     *
     * @param sourceDTO 数据源信息
     * @param queryDTO  查询条件
     * @return 拼装后的 sql
     */
    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {

        // 判断 schema 是否存在, 不传 schema 获取所有表
        String schema = SchemaUtil.getSchema(sourceDTO, queryDTO);
        log.info("current used schema：{}", schema);

        // 最终查询的 sql
        StringBuilder querySql = new StringBuilder();

        // 条件查询表
        String showTable;
        if (StringUtils.isBlank(schema)) {
            showTable = SHOW_SCHEMA_TABLE_SQL;
        } else {
            showTable = String.format(SHOW_TABLE_BY_SCHEMA_SQL, schema);
        }
        querySql.append(String.format(SHOW_TABLE_BASE, showTable));
        // 拼接模糊查询, 忽略大小写
        if (StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            querySql.append(String.format(TABLE_SEARCH_SQL, addFuzzySign(queryDTO)));
        }

        if (BooleanUtils.isTrue(queryDTO.getView())) {

            // 拼接 UNION
            querySql.append(" UNION ");

            // 条件查询视图
            String showView;
            if (StringUtils.isBlank(schema)) {
                showView = SHOW_SCHEMA_VIEW_SQL;
            } else {
                showView = String.format(SHOW_VIEW_BY_SCHEMA_SQL, schema);
            }
            querySql.append(String.format(SHOW_VIEW_BASE, showView));

            // 拼接模糊查询, 忽略大小写
            if (StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
                querySql.append(String.format(VIEW_SEARCH_SQL, addFuzzySign(queryDTO)));
            }

        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            querySql.append(String.format(LIMIT_SQL, queryDTO.getLimit()));
        }
        return querySql.toString();
    }

    /**
     * 处理 schema和tableName，适配schema和tableName中有.的情况
     *
     * @param schema    schema 名称
     * @param tableName 表名称
     * @return 处理后的 schema tableName 格式
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (!tableName.startsWith("\"") || !tableName.endsWith("\"")) {
            tableName = String.format("\"%s\"", tableName);
        }
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        if (!schema.startsWith("\"") || !schema.endsWith("\"")) {
            schema = String.format("\"%s\"", schema);
        }
        return String.format("%s.%s", schema, tableName);
    }

    /**
     * 获取 schema, 入参中获取不到则获取当前使用的 schema
     *
     * @param sourceDTO   数据源连接信息
     * @param sqlQueryDTO 查询条件
     * @return schema 信息
     */
    public String getSchema(ISourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        String inputSchema = SchemaUtil.getSchema(sourceDTO, sqlQueryDTO);
        return StringUtils.isBlank(inputSchema) ? getCurrentDatabase(sourceDTO) : inputSchema;
    }

    @Override
    protected String getShowDbSql() {
        return SHOW_SCHEMA;
    }

    @Override
    public String getCreateTableSql(ISourceDTO source, SqlQueryDTO queryDTO) {
        String schema = getSchema(source, queryDTO);
        String sql = String.format(CREATE_TABLE_SQL, schema, queryDTO.getTableName());
        Connection connection = getCon(source, queryDTO);
        Statement statement = null;
        ResultSet rs = null;
        String createTableSql = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                createTableSql = rs.getString(5);
                break;
            }
        } catch (Exception e) {
            throw new SourceException(String.format("failed to get the create table sql：%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return createTableSql;
    }
}
