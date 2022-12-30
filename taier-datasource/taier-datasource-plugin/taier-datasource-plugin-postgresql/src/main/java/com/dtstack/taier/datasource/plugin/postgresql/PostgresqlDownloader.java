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

package com.dtstack.taier.datasource.plugin.postgresql;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SqlFormatUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Postgresql download
 *
 * @author ：wangchuan
 * date：Created in 下午2:42 2021/6/10
 * company: www.dtstack.com
 */
@Slf4j
public class PostgresqlDownloader implements IDownloader {

    private final List<String> columnNames = Lists.newArrayList();

    private final Connection connection;

    private final String sql;

    private final String schema;

    private Statement statement;

    private int pageNum = 1;

    private final int pageSize = 100;

    private int pageAll;

    private int columnCount;

    // 切换 schema 命令
    private static final String SWITCH_SCHEMA = "set search_path to %s";

    public PostgresqlDownloader(Connection connection, String sql, String schema) {
        this.connection = connection;
        this.sql = SqlFormatUtil.formatSql(sql);
        this.schema = schema;
    }

    @Override
    public boolean configure() throws Exception {
        if (Objects.isNull(connection) || StringUtils.isEmpty(sql)) {
            throw new SourceException("connection is close or sql is null");
        }
        int totalLine = 0;
        statement = connection.createStatement();
        if (StringUtils.isNotBlank(schema)) {
            try {
                // 切换 schema
                statement.execute(String.format(SWITCH_SCHEMA, schema));
            } catch (Exception e) {
                log.error("switch schema to {} error", schema);
            }
        }

        String countSQL = String.format("SELECT COUNT(1) FROM (%s) temp", sql);
        String showColumns = String.format("SELECT * FROM (%s) t limit 1", sql);
        try (ResultSet totalResultSet = statement.executeQuery(countSQL);
             ResultSet columnsResultSet = statement.executeQuery(showColumns)) {
            while (totalResultSet.next()) {
                //获取总行数
                totalLine = totalResultSet.getInt(1);
            }
            //获取列信息
            columnCount = columnsResultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(columnsResultSet.getMetaData().getColumnLabel(i));
            }
            // 获取总页数
            pageAll = (int) Math.ceil(totalLine / (double) pageSize);
        } catch (Exception e) {
            throw new SourceException("build Postgresql downloader message exception : " + e.getMessage(), e);
        }
        return true;
    }

    @Override
    public List<String> getMetaInfo() {
        return columnNames;
    }

    @Override
    public List<List<String>> readNext() {
        //分页查询，一次一百条
        String limitSQL = String.format("SELECT * FROM (%s) t limit %s offset %s", sql, pageSize, pageSize * (pageNum - 1));
        List<List<String>> pageTemp = new ArrayList<>(100);

        try (ResultSet resultSet = statement.executeQuery(limitSQL)) {
            while (resultSet.next()) {
                List<String> columns = new ArrayList<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(resultSet.getString(i));
                }
                pageTemp.add(columns);
            }
        } catch (Exception e) {
            throw new SourceException("read Postgresql message exception : " + e.getMessage(), e);
        }

        pageNum++;
        return pageTemp;
    }

    @Override
    public boolean reachedEnd() {
        return pageAll < pageNum;
    }

    @Override
    public boolean close() throws Exception {
        DBUtil.closeDBResources(null, statement, connection);
        return true;
    }
}
