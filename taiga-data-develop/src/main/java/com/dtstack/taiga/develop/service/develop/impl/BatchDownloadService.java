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

package com.dtstack.taiga.develop.service.develop.impl;

import com.dtstack.taiga.common.enums.MultiEngineType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.service.develop.IDataDownloadService;
import com.dtstack.taiga.develop.service.develop.MultiEngineServiceFactory;
import com.dtstack.taiga.develop.utils.develop.common.IDownload;
import com.dtstack.taiga.develop.utils.develop.mapping.TaskTypeEngineTypeMapping;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 下载功能
 * Date: 2018/5/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchDownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchDownloadService.class);

    public static final Integer DEFAULT_LOG_PREVIEW_BYTES = 16383;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    /**
     * 按行数获取job的log
     *
     * @param tenantId
     * @param taskType      除数据同步和虚节点都可以导出jobLog
     * @param jobId
     * @param byteNum
     * @return
     * @throws Exception
     */
    public String loadJobLog(Long tenantId, Integer taskType, String jobId, Integer byteNum) {
        LOGGER.info("获取job日志下载器-->jobId:{}", jobId);
        IDownload downloader = buildIDownLoad(jobId, taskType, tenantId, byteNum == null ? DEFAULT_LOG_PREVIEW_BYTES : byteNum);
        LOGGER.info("获取job日志下载器完成-->jobId:{}", jobId);
        if (Objects.isNull(downloader)) {
            LOGGER.error("-----日志文件导出失败-----");
            return "";
        }
        StringBuilder result = new StringBuilder();
        while (!downloader.reachedEnd()) {
            Object row = downloader.readNext();
            result.append(row);
        }
        return result.toString();
    }

    private IDownload buildIDownLoad(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("engineJobId 不能为空");
        }
        MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(taskType);
        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(multiEngineType.getType());
        Preconditions.checkNotNull(dataDownloadService, String.format("not support engineType %d", multiEngineType.getType()));
        return dataDownloadService.buildIDownLoad(jobId, taskType, tenantId, limitNum);
    }

    public String downloadAppTypeLog(Long tenantId, String jobId, Integer limitNum, String logType, Integer taskType) {
        MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(taskType);
        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(multiEngineType.getType());
        IDownload downloader = dataDownloadService.typeLogDownloader(tenantId, jobId, limitNum == null ? Integer.MAX_VALUE : limitNum, logType);
        if (Objects.isNull(downloader)) {
            LOGGER.error("-----日志文件导出失败-----");
            return "-----日志文件不存在-----";
        }
        StringBuilder result = new StringBuilder();
        while (!downloader.reachedEnd()) {
            Object row = downloader.readNext();
            result.append(row);
        }
        return result.toString();
    }

}
