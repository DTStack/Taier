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

package com.dtstack.taier.datasource.plugin.hbase;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.HbaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.NamespaceNotFoundException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:59 2020/2/27
 * @Description：Hbase 客户端
 */
@Slf4j
public class HbaseClient extends AbsNoSqlClient {
    private HbaseConnFactory connFactory = new HbaseConnFactory();

    private static final String ROWKEY = "rowkey";

    private static final String FAMILY_QUALIFIER = "%s:%s";

    private static final String TIMESTAMP = "timestamp";

    // 默认重试次数
    private static final int DEFAULT_RETRY_NUM = 3;

    // 默认重试间隔
    private static final long DEFAULT_RETRY_INTERVAL = TimeUnit.SECONDS.toMillis(2);

    @Override
    public Boolean testCon(ISourceDTO iSource) {
        return connFactory.testConn(iSource);
    }

    @Override
    public List<String> getTableList(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) iSource;
        Connection hConn = null;
        Admin admin = null;
        List<String> tableList = new ArrayList<>();
        try {
            hConn = HbaseConnFactory.getHbaseConn(hbaseSourceDTO, queryDTO);
            admin = hConn.getAdmin();
            TableName[] tableNames = admin.listTableNames();
            if (tableNames != null) {
                for (TableName tableName : tableNames) {
                    tableList.add(tableName.getNameAsString());
                }
            }
        } catch (IOException e) {
            throw new SourceException(String.format("get hbase table list exception,%s", e.getMessage()), e);
        } finally {
            closeAdmin(admin);
            closeConnection(hConn,hbaseSourceDTO);
            destroyProperty();
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    private static void closeConnection(Connection hConn, HbaseSourceDTO hbaseSourceDTO) {
        if ((hbaseSourceDTO.getPoolConfig() == null || MapUtils.isNotEmpty(hbaseSourceDTO.getKerberosConfig())) && hConn != null) {
            try {
                hConn.close();
            } catch (IOException e) {
                log.error("hbase Close connection exception", e);
            }
        }
    }

    private static void closeAdmin(Admin admin) {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                log.error("hbase Close connection exception", e);
            }
        }
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) iSource;
        Connection hConn = null;
        Table tb = null;
        List<ColumnMetaDTO> cfList = new ArrayList<>();
        try {
            hConn = HbaseConnFactory.getHbaseConn(hbaseSourceDTO, queryDTO);
            TableName tableName = TableName.valueOf(queryDTO.getTableName());
            tb = hConn.getTable(tableName);
            HTableDescriptor hTableDescriptor = tb.getTableDescriptor();
            HColumnDescriptor[] columnDescriptors = hTableDescriptor.getColumnFamilies();
            for(HColumnDescriptor columnDescriptor: columnDescriptors) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                columnMetaDTO.setKey(columnDescriptor.getNameAsString());
                cfList.add(columnMetaDTO);
            }
        } catch (IOException e) {
            throw new SourceException(String.format("hbase list column families error,%s", e.getMessage()), e);
        } finally {
            closeTable(tb);
            closeConnection(hConn,hbaseSourceDTO);
            destroyProperty();
        }
        return cfList;
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) source;
        Connection connection = null;
        Table table = null;
        ResultScanner rs = null;
        List<Result> results = Lists.newArrayList();
        List<List<Object>> executeResult = Lists.newArrayList();
        try {
            //获取hbase连接
            connection = HbaseConnFactory.getHbaseConn(hbaseSourceDTO, queryDTO);
            TableName tableName = TableName.valueOf(queryDTO.getTableName());
            table = connection.getTable(tableName);
            Scan scan = new Scan();
            //数据预览限制返回条数
            scan.setMaxResultSize(queryDTO.getPreviewNum());
            rs = table.getScanner(scan);
            for (Result row : rs) {
                if (CollectionUtils.isEmpty(row.listCells())) {
                    continue;
                }
                results.add(row);
                if (results.size() >= queryDTO.getPreviewNum()) {
                    break;
                }
            }
        } catch (Exception e){
            throw new SourceException(String.format("Data preview failed,%s", e.getMessage()), e);
        } finally {
            if (hbaseSourceDTO.getPoolConfig() == null || MapUtils.isNotEmpty(hbaseSourceDTO.getKerberosConfig())) {
                close(rs, table, connection);
            } else {
                close(rs, table, null);
            }
            destroyProperty();
        }

        //理解为一行记录
        for (Result result : results) {
            List<Cell> cells = result.listCells();
            long timestamp = 0L;
            HashMap<String, Object> row = Maps.newHashMap();
            for (Cell cell : cells){
                row.put(ROWKEY, Bytes.toString(cell.getRowArray(), cell.getRowOffset(),cell.getRowLength()));
                String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),cell.getFamilyLength());
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),cell.getValueLength());
                row.put(String.format(FAMILY_QUALIFIER, family, qualifier), value);
                //取到最新变动的时间
                if (cell.getTimestamp() > timestamp) {
                    timestamp = cell.getTimestamp();
                }
            }
            row.put(TIMESTAMP, timestamp);
            executeResult.add(Lists.newArrayList(row));
        }
        return executeResult;
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) source;
        Connection connection = null;
        Admin admin = null;
        List<String> namespaces = Lists.newArrayList();
        try {
            //获取hbase连接
            connection = HbaseConnFactory.getHbaseConn(hbaseSourceDTO);
            admin = connection.getAdmin();
            NamespaceDescriptor[] descriptors = admin.listNamespaceDescriptors();
            for (NamespaceDescriptor descriptor : descriptors) {
                namespaces.add(descriptor.getName());
            }
        } catch (Exception e) {
            throw new SourceException(String.format("get namespace list exception：%s", e.getMessage()), e);
        } finally {
            close(admin);
            closeConnection(connection, hbaseSourceDTO);
            destroyProperty();
        }
        return namespaces;
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        if (Objects.isNull(queryDTO) || StringUtils.isBlank(queryDTO.getSchema())) {
            throw new SourceException("namespace not empty！");
        }
        HbaseSourceDTO hbaseSourceDTO = (HbaseSourceDTO) source;
        Connection connection = null;
        Admin admin = null;
        List<String> tables = Lists.newArrayList();
        try {
            //获取hbase连接
            connection = HbaseConnFactory.getHbaseConn(hbaseSourceDTO);
            admin = connection.getAdmin();
            TableName[] tableNames = admin.listTableNamesByNamespace(queryDTO.getSchema());
            for (TableName tableName : tableNames) {
                tables.add(tableName.getNameAsString());
            }
        } catch (NamespaceNotFoundException noe) {
            throw new SourceException(String.format("namespace not exits！：%s", noe.getMessage()), noe);
        } catch (Exception e) {
            throw new SourceException(String.format("Get the table exception under the specified namespace：%s", e.getMessage()), e);
        } finally {
            close(admin);
            closeConnection(connection, hbaseSourceDTO);
            destroyProperty();
        }
        return tables;
    }

    public static void closeTable(Table table) {
        if(table != null) {
            try {
                table.close();
            } catch (IOException e) {
                throw new SourceException(String.format("hbase can not close table error,%s", e.getMessage()), e);
            }
        }
    }

    private void close(Closeable... closeables) {
        try {
            if (Objects.nonNull(closeables)) {
                for (Closeable closeable : closeables) {
                    if (Objects.nonNull(closeable)) {
                        closeable.close();
                    }
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("hbase can not close table error,%s", e.getMessage()), e);
        }
    }

    public static void destroyProperty() {
        System.clearProperty("java.security.auth.login.config");
        System.clearProperty("javax.security.auth.useSubjectCredsOnly");
    }
}
