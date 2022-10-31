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
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.TaskConfiguration;
import com.dtstack.taier.develop.service.schedule.JobExpandService;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
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
public class DevelopDownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopDownloadService.class);

    public static final Integer DEFAULT_LOG_PREVIEW_BYTES = 16383;

    @Autowired
    private JobExpandService jobExpandService;

    @Autowired
    private TaskConfiguration taskConfiguration;

    /**
     * 文件下载处理
     *
     * @param response
     * @param downloadType
     * @param jobId
     */
    public void handleDownload(HttpServletResponse response, DownloadType downloadType, String jobId,
                               Long tenantId, Integer taskType) {
        ITaskRunner taskRunner = taskConfiguration.get(taskType);
        String downFileName = getDownloadFileName(downloadType);
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("download error，{}", jobId, e);
        }
        response.setHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", String.format("attachment;filename=%s", downFileName));
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");

        IDownload iDownload = taskRunner.logDownLoad(tenantId, jobId, Integer.MAX_VALUE);
        try {
            if (iDownload == null) {
                try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
                    ExecuteResultVO executeResultVO = taskRunner.runLog(jobId, taskType, tenantId, null);
                    if (null != executeResultVO && StringUtils.isNotBlank(executeResultVO.getMsg())) {
                        bos.write(executeResultVO.getMsg().getBytes());
                    }
                } catch (Exception e) {
                    LOGGER.error("download error，{}", jobId, e);
                }
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
                        LOGGER.error("download error，{}", jobId, e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("download error，{}", jobId, e);
            if (e instanceof FileNotFoundException) {
                writeFileWithEngineLog(response, jobId);
            } else {
                try (OutputStream os = response.getOutputStream(); BufferedOutputStream bos = new BufferedOutputStream(os)) {
                    bos.write(String.format("下载文件异常:", e.getMessage()).getBytes());
                } catch (Exception e1) {
                    LOGGER.error("download error，{}", jobId, e1);
                }
            }
        } finally {
            if (iDownload != null) {
                try {
                    iDownload.close();
                } catch (Exception e) {
                    LOGGER.error("download error，{}", jobId, e);
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
            LOGGER.error("download sync log error", e);
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
        return String.format("taier_%s%s", UUID.randomUUID(), downFileNameSuf);
    }

}
