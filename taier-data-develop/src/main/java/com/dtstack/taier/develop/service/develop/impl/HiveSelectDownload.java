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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.develop.utils.develop.common.IDownload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * hive查询数据下载器
 *
 * @author ：wangchuan
 * date：Created in 3:25 上午 2020/11/26
 * company: www.dtstack.com
 */
public class HiveSelectDownload implements IDownload {

    /**
     * common-loader中定义的download
     */
    private IDownloader pluginDownloader;

    private ISourceDTO iSourceDTO;

    /**
     * 查询字段集合
     */
    private List<String> queryFieldNames;

    /**
     * 查询的表名
     */
    private String tableName;

    /**
     * hive空字符串处理
     */
    private static final String TEXT_STORE_NULL = "\\N";

    public HiveSelectDownload(ISourceDTO iSourceDTO, String tableName) throws Exception{
        this.iSourceDTO = iSourceDTO;
        this.tableName = tableName;
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        Table table = client.getTable(iSourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
        this.queryFieldNames = table.getColumns().stream().map(ColumnMetaDTO::getKey)
                .collect(Collectors.toList());
        SqlQueryDTO queryDTO = SqlQueryDTO.builder()
                .tableName(tableName)
                .columns(queryFieldNames)
                .build();
        pluginDownloader = client.getDownloader(iSourceDTO, queryDTO);
    }

    @Override
    public List<String> getMetaInfo() {
        try {
            return pluginDownloader.getMetaInfo();
        } catch (Exception e) {
            throw new DtCenterDefException("下载器getMetaInfo失败", e);
        }
    }

    @Override
    public Object readNext() {
        try {
            List<String> row = (List<String>) pluginDownloader.readNext();
            List<String> data = new ArrayList<>();
            for (int index = 0; index < queryFieldNames.size(); index++) {
                String source = row.get(index);
                data.add(dealHiveTextNull(source));
            }
            return data;
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器readNext失败:%s", e.getMessage()), e);
        }
    }

    @Override
    public boolean reachedEnd() {
        try {
            return pluginDownloader.reachedEnd();
        } catch (Exception e) {
            throw new DtCenterDefException("下载器reachedEnd失败", e);
        }
    }

    @Override
    public void close() {
        try {
            pluginDownloader.close();
        } catch (Exception e) {
            throw new DtCenterDefException("下载器close失败", e);
        }
    }

    /**
     * @param source 单独字段值
     * 处理hive存储为null字段
     */
    private String dealHiveTextNull(String source) {
        return TEXT_STORE_NULL.equals(source) ? null : source;
    }
}
