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

import com.dtstack.taier.common.enums.DownloadType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.develop.service.develop.IDataDownloadService;
import com.dtstack.taier.develop.service.develop.MultiEngineServiceFactory;
import com.dtstack.taier.develop.service.schedule.JobExpandService;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.UUID;


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

    @Autowired
    private JobExpandService jobExpandService;

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
        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(taskType);
        Preconditions.checkNotNull(dataDownloadService, String.format("not support engineType %d", taskType));
        return dataDownloadService.buildIDownLoad(jobId, taskType, tenantId, limitNum);
    }

    public String downloadAppTypeLog(Long tenantId, String jobId, Integer limitNum, String logType, Integer taskType) {
        IDataDownloadService dataDownloadService = multiEngineServiceFactory.getDataDownloadService(taskType);
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

    /**
     * 返回下载jobLog的downloader
     *
     * @param jobId
     * @param taskType      除数据同步、虚节点和工作流都可以导出jobLog
     * @param dtuicTenantId
     * @return
     * @throws Exception
     */
    public IDownload downloadJobLog(String jobId, Integer taskType, Long dtuicTenantId) {
        return buildIDownLoad(jobId, taskType, dtuicTenantId, Integer.MAX_VALUE);
    }

    /**
     * 文件下载处理
     *
     * @param response
     * @param iDownload
     * @param downloadType
     * @param jobId
     */
    public void handleDownload(HttpServletResponse response, IDownload iDownload, DownloadType downloadType, String jobId) {
        String downFileName = getDownloadFileName(downloadType);
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("解码失败：{}", e);
        }
        response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", String.format("attachment;filename=%s", downFileName));
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        try {
            if (iDownload == null) {
                writeFileWithEngineLog(response, jobId);
            } else {
                if (iDownload instanceof SyncDownload) {
                    writeFileWithSyncLog(response, iDownload);
                } else {
                    try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
                        while (!iDownload.reachedEnd()) {
                            Object row = iDownload.readNext();
                            bos.write(row.toString().getBytes());
                        }
                    } catch (Exception e) {
                        LOGGER.error("下载日志异常，{}", e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("",e);
            if (e instanceof FileNotFoundException) {
                writeFileWithEngineLog(response, jobId);
            } else {
                try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
                    bos.write(String.format("下载文件异常:", e.getMessage()).getBytes());
                } catch (Exception e1) {
                    LOGGER.error("", e1);
                }
            }
        } finally {
            if (iDownload != null) {
                try {
                    iDownload.close();
                } catch (Exception e) {
                    LOGGER.error("iDownload:{}", e);
                }
            }
        }
    }

    /**
     * 输出engine提供的日志
     *
     * @param response
     * @param jobId
     */
    private void writeFileWithEngineLog(HttpServletResponse response, String jobId) {
        //hdfs没有日志就下载engine里的日志
        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
            String log = getLog(jobId);
            if (StringUtils.isNotBlank(log)) {
                bos.write(log.getBytes());
            }
        }catch (Exception e) {
            LOGGER.error("下载engineLog异常，{}", e);
        }
    }

    /**
     * 获取log
     *
     * @param jobId
     */
    private String getLog(String jobId){
        StringBuilder log = new StringBuilder();
        //hdfs没有日志就下载engine里的日志
        if (StringUtils.isNotBlank(jobId)) {
            ScheduleJobExpand scheduleJobExpand = jobExpandService.selectOneByJobId(jobId);
            if (Objects.nonNull(scheduleJobExpand)) {
                log.append("=====================提交日志========================\n");
                if (StringUtils.isNotBlank(scheduleJobExpand.getLogInfo())) {
                    log.append(scheduleJobExpand.getLogInfo().replace("\\n", "\n").replace("\\t", " "));
                }
                log.append("\n\n\n");
                if (StringUtils.isNotBlank(scheduleJobExpand.getEngineLog())) {
                    log.append("=====================运行日志========================\n");
                    log.append(scheduleJobExpand.getEngineLog().replace("\\n", "\n").replace("\\t", " "));
                    log.append("\n\n\n");
                }
            }
        }
        return log.toString();
    }

    /**
     * 输出数据同步任务日志
     * @param response
     * @param downloadInvoke
     */
    private void writeFileWithSyncLog(HttpServletResponse response, IDownload downloadInvoke) {
        try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
            String logInfo = ((SyncDownload) downloadInvoke).getLogInfo()
                    .replace("\\n\"","\n").replace("\\n\\t","\n");
            bos.write(logInfo.getBytes());
        } catch (Exception e) {
            LOGGER.error("下载数据同步任务运行日志异常，{}", e);
        }
    }

    /**
     * 根据类型生成下载的文件名
     * @param downloadType 文件下载类型
     * @return
     */
    private String getDownloadFileName(DownloadType downloadType) {
        String downFileNameSuf;
        if (downloadType == DownloadType.DEVELOP_LOG) {
            downFileNameSuf = ".log";
        } else {
            throw new RdosDefineException("未知的文件下载类型");
        }
        return String.format("dtstack_ide_%s%s", UUID.randomUUID().toString(), downFileNameSuf);
    }

}
