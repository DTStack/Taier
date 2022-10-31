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

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.develop.dto.devlop.DownloadLogVO;
import com.dtstack.taier.develop.utils.develop.hive.service.LogPluginDownload;
import com.dtstack.taier.scheduler.service.ClusterService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class FlinkDownloadLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkDownloadLogService.class);

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private ClusterService clusterService;

    /**
     * 根据 UIC 租户 ID 和 yarn 上任务 ID 下载任务日志
     *
     * @param applicationId yarn 任务 ID
     * @param tenantId      UIC 租户 ID
     * @param limitNum      限制大小
     * @param taskManagerId taskManagerId
     * @return 日志下载器
     */
    public IDownloader downloadJobLogWithAppId(String applicationId, Long tenantId, Integer limitNum, String taskManagerId) throws Exception {
        if (StringUtils.isBlank(applicationId)) {
            throw new DtCenterDefException("applicationId 不能为空,可能任务尚未完成或提交失败,请检查运行日志");
        }
        Map yarnConf = clusterService.getComponentByTenantId(tenantId, EComponentType.YARN.getTypeCode(), false,
                Map.class, null);
        Map hadoopConf = clusterService.getComponentByTenantId(tenantId, EComponentType.HDFS.getTypeCode(), false,
                Map.class, null);
        return new LogPluginDownload(applicationId, hadoopConf, yarnConf, null, limitNum).getHdfsLogDownloader();
    }

    /**
     * 返回下载jobLog的downloader
     *
     * @param downloadLogVO
     * @return
     * @throws Exception
     */
    public IDownloader downloadJobLog(DownloadLogVO downloadLogVO) {
        AssertUtils.notBlank(downloadLogVO.getJobId(), "jobId不能为空");
        try {
            ScheduleJob scheduleJob = getByJobId(downloadLogVO.getJobId());
            AssertUtils.notNull(scheduleJob, "job不能为空");
            return downloadJobLogWithAppId(scheduleJob.getApplicationId(), downloadLogVO.getTenantId(), Integer.MAX_VALUE, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DtCenterDefException(e.getMessage(), e);
        }
    }

    public ScheduleJob getByJobId(String jobId) {
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.getRdosJobByJobIds(Arrays.asList(jobId));
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(scheduleJobs)) {
            return scheduleJobs.get(0);
        } else {
            return null;
        }
    }
}
