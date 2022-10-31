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

package com.dtstack.taier.datasource.plugin.spark.downloader;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.Column;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author luming
 * @date 2022/4/24
 */
@Slf4j
public class SparkJdbcDownload implements IDownloader {
    /**
     * 执行的 sql
     */
    private final String sql;

    /**
     * 数据库连接
     */
    private final Connection connection;

    /**
     * 连接操作对象
     */
    private Statement statement;

    /**
     * 当前读取数据行数
     */
    private int currentLine = 0;

    /**
     * 列字段
     */
    private List<Column> columnNames;

    /**
     * 表字段条数
     */
    private int columnCount;

    /**
     * 是否已经调用 configure 方法
     */
    private boolean isConfigure = false;

    /**
     * 是否已经读到最后一页数据
     */
    private boolean lastRead = false;

    /**
     * 拉取100条数据保存在队列中，每次读取一条
     */
    private final Queue<List<String>> lineQueue = new LinkedBlockingQueue<>();

    public SparkJdbcDownload(Connection connection, String sql) {
        this.connection = connection;
        this.sql = sql;
    }

    @Override
    public boolean configure() throws Exception {
        if (BooleanUtils.isTrue(isConfigure)) {
            // 避免 configure 方法重复调用
            return true;
        }
        if (null == connection || StringUtils.isEmpty(sql)) {
            throw new SourceException("connection acquisition failed or execution SQL is empty");
        }
        statement = connection.createStatement();

        // 获取列信息
        String showColumns = String.format("SELECT * FROM (%s) t LIMIT 1", sql);
        try (ResultSet resultSet = statement.executeQuery(showColumns)) {
            columnNames = new ArrayList<>();
            columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Column column = new Column();
                column.setName(resultSet.getMetaData().getColumnName(i));
                column.setType(resultSet.getMetaData().getColumnTypeName(i));
                column.setIndex(i);
                columnNames.add(column);
            }
        }
        isConfigure = true;
        log.info("Download: executed SQL:{}, columnNames:{}, columnCount{}", sql, columnNames, columnCount);
        return true;
    }

    @Override
    public List<String> getMetaInfo() {
        if (CollectionUtils.isNotEmpty(columnNames)) {
            return columnNames.stream().map(Column::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> readNext() {
        if (!lineQueue.isEmpty()) {
            currentLine++;
            return lineQueue.poll();
        } else {
            //分页查询，一次一百条
            String limitSQL = String.format("SELECT * FROM (%s) t limit %s,%s", sql, currentLine, 100);
            try (ResultSet resultSet = statement.executeQuery(limitSQL)) {
                while (resultSet.next()) {
                    List<String> columns = new ArrayList<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(resultSet.getString(i));
                    }
                    lineQueue.add(columns);
                    //如果当前队列数小于100，说明此次查询已经查到数据表末尾了
                    if (lineQueue.size() < 100) {
                        lastRead = true;
                    }
                }
                return readNext();
            } catch (Exception e) {
                throw new SourceException("read message exception : " + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean reachedEnd() {
        return lastRead && lineQueue.isEmpty();
    }

    @Override
    public boolean close() throws Exception {
        DBUtil.closeDBResources(null, statement, connection);
        return true;
    }
}
