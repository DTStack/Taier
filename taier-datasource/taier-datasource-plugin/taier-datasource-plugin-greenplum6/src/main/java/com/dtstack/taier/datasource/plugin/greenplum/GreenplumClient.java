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

package com.dtstack.taier.datasource.plugin.greenplum;

import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.Greenplum6SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:11 2020/4/10
 * @Description：Greenplum 客户端
 */
public class GreenplumClient extends AbsRdbmsClient {

    private static final String TABLE_QUERY = "SELECT relname from pg_class a,pg_namespace b where relname not like " +
            "'%%prt%%' and relkind ='r'  and a.relnamespace=b.oid and  nspname = '%s';";

    private static final String TABLE_QUERY_WITHOUT_SCHEMA = "\n" +
            "select table_schema ||'.'||table_name as tableName from information_schema.tables where table_schema in " +
            "(SELECT n.nspname AS \"Name\"  FROM pg_catalog.pg_namespace n WHERE n.nspname !~ '^pg_' AND n.nspname <>" +
            " 'gp_toolkit' AND n.nspname <> 'information_schema' ORDER BY 1)";

    private static final String TABLE_COMMENT_QUERY = "select de.description\n" +
            "          from (select pc.oid as ooid,pn.nspname,pc.*\n" +
            "      from pg_class pc\n" +
            "           left outer join pg_namespace pn\n" +
            "                        on pc.relnamespace = pn.oid\n" +
            "      where 1=1\n" +
            "       and pc.relkind in ('r')\n" +
            "       and pn.nspname not in ('pg_catalog','information_schema')\n" +
            "       and pn.nspname not like 'pg_toast%%'\n" +
            "       and pc.oid not in (\n" +
            "          select inhrelid\n" +
            "            from pg_inherits\n" +
            "       )\n" +
            "       and pc.relname not like '%%peiyb%%'\n" +
            "    order by pc.relname) tab\n" +
            "               left outer join (select pd.*\n" +
            "     from pg_description pd\n" +
            "    where 1=1\n" +
            "      and pd.objsubid = 0) de\n" +
            "                            on tab.ooid = de.objoid\n" +
            "         where 1=1 and tab.relname='%s' and tab.nspname = '%s'";

    private static final String DATABASE_QUERY = "select nspname from pg_namespace";

    private static final String CREATE_SCHEMA_SQL_TMPL = "create schema %s";

    // 判断db是否存在
    private static final String DATABASE_IS_EXISTS = "select nspname from pg_namespace where nspname = '%s'";

    private static final String TABLES_IS_IN_SCHEMA = "select table_name from information_schema.tables WHERE table_schema = '%s' and table_name = '%s'";

    // 获取正在使用数据库
    private static final String CURRENT_DB = "select current_database()";

    // 获取正在使用 schema
    private static final String CURRENT_SCHEMA = "select current_schema()";

    // 获取当前版本号
    private static final String SHOW_VERSION = "select version()";

    // 获取指定schema下的表，不包括视图
    private static final String SHOW_TABLE_BY_SCHEMA_SQL = "SELECT table_name FROM information_schema.tables WHERE table_schema = '%s' AND table_type = 'BASE TABLE' %s";

    // 根据schema选表表名模糊查询
    private static final String SEARCH_SQL = " AND table_name LIKE '%s' ";

    // 限制条数语句
    private static final String LIMIT_SQL = " LIMIT %s ";

    @Override
    protected ConnFactory getConnFactory() {
        return new GreenplumFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.GREENPLUM6;
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Greenplum6SourceDTO greenplum6SourceDTO = (Greenplum6SourceDTO) sourceDTO;

        // 校验 schema，特殊处理表名中带了 schema 信息的
        String tableName = queryDTO.getTableName();
        String schema = greenplum6SourceDTO.getSchema();
        if (StringUtils.isEmpty(greenplum6SourceDTO.getSchema())) {
            if (!queryDTO.getTableName().contains(".")) {
                throw new SourceException("The greenplum data source requires schema parameters");
            }
            schema = queryDTO.getTableName().split("\\.")[0];
            tableName = queryDTO.getTableName().split("\\.")[1];
        }

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(String.format(TABLE_COMMENT_QUERY, tableName, schema));
            while (resultSet.next()) {
                return resultSet.getString(1);
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
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Greenplum6SourceDTO greenplum6SourceDTO = (Greenplum6SourceDTO) sourceDTO;

        Statement statement = null;
        ResultSet resultSet = null;
        List<String> tableList = new ArrayList<>();
        try {
            statement = connection.createStatement();
            DBUtil.setFetchSize(statement, queryDTO);
            if (StringUtils.isBlank(greenplum6SourceDTO.getSchema())) {
                resultSet = statement.executeQuery(TABLE_QUERY_WITHOUT_SCHEMA);
            } else {
                resultSet = statement.executeQuery(String.format(TABLE_QUERY, greenplum6SourceDTO.getSchema()));
            }
            while (resultSet.next()) {
                tableList.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new SourceException(String.format("get table: %s's information error. Please contact the DBA to check the database、table information.",
                    greenplum6SourceDTO.getSchema()), e);
        } finally {
            DBUtil.closeDBResources(resultSet, statement, connection);
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        Greenplum6SourceDTO greenplum6SourceDTO = (Greenplum6SourceDTO) source;
        String schema = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : greenplum6SourceDTO.getSchema();
        GreenplumDownloader greenplumDownloader = new GreenplumDownloader(getCon(greenplum6SourceDTO),
                queryDTO.getSql(), schema);
        greenplumDownloader.configure();
        return greenplumDownloader;
    }

    @Override
    public String getCreateTableSql(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException("Not Support");
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException("Not Support");
    }

    @Override
    protected String getCreateDatabaseSql(String dbName, String comment) {
        return String.format(CREATE_SCHEMA_SQL_TMPL, dbName);
    }

    /**
     * 此处方法为判断schema是否存在
     *
     * @param source 数据源信息
     * @param dbName schema 名称
     * @return 是否存在结果
     */
    @Override
    public Boolean isDatabaseExists(ISourceDTO source, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("schema  is not empty");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(DATABASE_IS_EXISTS, dbName)).build()));
    }

    /**
     * 此处方法为判断指定schema 是否有该表
     *
     * @param source 数据源信息
     * @param tableName 表名
     * @param dbName schema名
     * @return 判断结果
     */
    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("schema  is not empty");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(TABLES_IS_IN_SCHEMA, dbName, tableName)).build()));
    }

    @Override
    public String getShowDbSql() {
        return DATABASE_QUERY;
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }

    @Override
    protected String getCurrentSchemaSql() {
        return CURRENT_SCHEMA;
    }

    @Override
    protected String getVersionSql() {
        return SHOW_VERSION;
    }

    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) sourceDTO;
        String schema = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : rdbmsSourceDTO.getSchema();
        // 如果不传scheme，默认使用当前连接使用的schema
        if (StringUtils.isBlank(schema)) {
            throw new SourceException("schema is not empty...");
        }
        StringBuilder constr = new StringBuilder();
        if (StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            constr.append(String.format(SEARCH_SQL, addFuzzySign(queryDTO)));
        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            constr.append(String.format(LIMIT_SQL, queryDTO.getLimit()));
        }
        return String.format(SHOW_TABLE_BY_SCHEMA_SQL, schema, constr.toString());
    }

    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        return String.format("%s.%s", schema, tableName);
    }
}
