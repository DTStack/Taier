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

package com.dtstack.taier.develop.utils.develop.hive.service;

import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IHdfsFile;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.develop.utils.develop.common.IDownload;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chener
 * @Classname LogPluginDownloader
 * @Description TODO
 * @Date 2020/8/8 17:12
 * @Created chener@dtstack.com
 */
public class LogPluginDownload implements IDownload {

    private IDownloader hdfsLogDownloader;

    private String applicationStr;

    private Map<String, Object> yarnConf;

    private Map<String, Object> hdfsConf;

    private String user;

    private Integer readLimit;

    private String taskManagerId;

    private HdfsSourceDTO sourceDTO;

    public LogPluginDownload(String applicationStr, Map<String, Object> yarnConf, Map<String, Object> hdfsConf, String user, Integer readLimit) throws Exception {
        this.applicationStr = applicationStr;
        this.yarnConf = yarnConf;
        this.hdfsConf = hdfsConf;
        this.user = user;
        this.readLimit = readLimit;
        init();
    }

    public LogPluginDownload(String applicationStr, Map<String, Object> yarnConf, Map<String, Object> hdfsConf, String user, String taskManagerId, Integer readLimit) throws Exception {
        this.applicationStr = applicationStr;
        this.yarnConf = yarnConf;
        this.hdfsConf = hdfsConf;
        this.user = user;
        this.readLimit = readLimit;
        this.taskManagerId = taskManagerId;
        init();
    }

    private void init() throws Exception {
        Object kerberosConfig = hdfsConf.get("kerberosConfig");
        Map<String, Object> kerberosConfMap = null;
        if (Objects.nonNull(kerberosConfig)) {
            if (kerberosConfig instanceof String) {
                kerberosConfMap = PublicUtil.objectToMap(kerberosConfig);
            } else if (kerberosConfig instanceof Map) {
                kerberosConfMap = (Map<String, Object>) kerberosConfig;
            }
        }
        sourceDTO = HdfsSourceDTO.builder()
                .config(PublicUtil.objectToStr(hdfsConf))
                .defaultFS(hdfsConf.getOrDefault("fs.defaultFS","").toString())
                .kerberosConfig(kerberosConfMap)
                .yarnConf(yarnConf)
                .appIdStr(applicationStr)
                .readLimit(readLimit)
                .user(user)
                .containerId(taskManagerId)
                .build();
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                .build();
        hdfsLogDownloader = hdfsClient.getLogDownloader(sourceDTO, sqlQueryDTO);
    }

    public List<String> getTaskManagerList() {
        IHdfsFile hdfsClient = ClientCache.getHdfs(DataSourceType.HDFS.getVal());
        return hdfsClient.getTaskManagerList(sourceDTO, applicationStr);
    }

    @Override
    public List<String> getMetaInfo() {
        try {
            return hdfsLogDownloader.getMetaInfo();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器getMetaInfo失败，原因是：%s", e.getMessage()));
        }
    }

    @Override
    public Object readNext() {
        try {
            return hdfsLogDownloader.readNext();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器readNext失败，原因是：%s", e.getMessage()));
        }
    }

    @Override
    public boolean reachedEnd() {
        try {
            return hdfsLogDownloader.reachedEnd();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器reachedEnd失败，原因是：%s", e.getMessage()));
        }
    }

    @Override
    public void close() {
        try {
            hdfsLogDownloader.close();
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("下载器close失败，原因是：%s", e.getMessage()));
        }
    }

    public IDownloader getHdfsLogDownloader() {
        return hdfsLogDownloader;
    }
}
