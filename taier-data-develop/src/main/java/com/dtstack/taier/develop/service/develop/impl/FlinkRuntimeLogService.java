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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IRestful;
import com.dtstack.taier.datasource.api.dto.restful.Response;
import com.dtstack.taier.datasource.api.dto.source.RestfulSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.AssertUtils;
import com.dtstack.taier.dao.domain.ScheduleJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobHistory;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopTaskMapper;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.dto.devlop.DownloadLogVO;
import com.dtstack.taier.develop.dto.devlop.FlinkSqlRuntimeLogDTO;
import com.dtstack.taier.develop.dto.devlop.FlinkSqlTaskManagerVO;
import com.dtstack.taier.develop.dto.devlop.RuntimeLogResultVO;
import com.dtstack.taier.develop.enums.develop.ClusterMode;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.develop.hive.service.LogPluginDownload;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.vo.action.ActionLogVO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dtstack.taier.develop.service.develop.impl.DevelopDownloadService.DEFAULT_LOG_PREVIEW_BYTES;

@Service
public class FlinkRuntimeLogService {

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private ScheduleJobCacheService ScheduleJobCacheService;

    @Autowired
    private JobService jobService;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    FlinkDownloadLogService flinkDownloadLogService;

    @Autowired
    private DevelopTaskMapper developTaskMapper;

    @Autowired
    private ClusterService clusterService;


    private static Logger logger = LoggerFactory.getLogger(FlinkRuntimeLogService.class);

    private static final String JOB_MANAGER = "jobmanager";

    private static final String TASK_MANAGER = "taskmanager";

    private static final String TOTAL_BYTES = "totalBytes";

    private static final String DOWNLOAD_URL = "url";

    private static final String LOG_NAME = "name";

    /**
     * taskManager日志每页字节数 1MB
     */
    private static final Integer MAX_PAGE_SIZE = 1024 * 1024;


    /**
     * 获取taskManager信息
     *
     * @param taskId   任务id
     * @param tenantId
     * @return
     * @throws Exception
     */
    public List<FlinkSqlTaskManagerVO> listTaskManagerByJobId(Long taskId, Long tenantId) {
        Task task = developTaskMapper.selectById(taskId);
        AssertUtils.notNull(task, "任务不存在");
        List<FlinkSqlRuntimeLogDTO> runtimeLogs = getTaskLogList(task.getJobId(), tenantId);
        List<FlinkSqlTaskManagerVO> taskManagerList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(runtimeLogs)) {
            return Lists.newArrayList();
        }
        for (FlinkSqlRuntimeLogDTO runtimeLog : runtimeLogs) {
            if (TASK_MANAGER.equals(runtimeLog.getTypeName()) && StringUtils.isNotBlank(runtimeLog.getOtherInfo())) {
                List<Map<String, Object>> logs = runtimeLog.getLogs();
                String url = null;
                int total = 0;
                String name = null;
                if (logs != null && !logs.isEmpty()) {
                    //对logs中的几种日志取.log结尾的日志
                    Map<String, Object> taskLog = logs.stream()
                            .filter(log -> MapUtils.getString(log, LOG_NAME).trim().endsWith(".log"))
                            .findAny()
                            .orElseThrow(() -> new DtCenterDefException("日志文件不存在！"));
                    url = MapUtils.getString(taskLog, DOWNLOAD_URL);
                    name = MapUtils.getString(taskLog, LOG_NAME);
                    total = MapUtils.getInteger(taskLog, TOTAL_BYTES);
                }
                //解析taskManager信息
                JSONObject taskManagerJsonObj = JSON.parseObject(runtimeLog.getOtherInfo());
                FlinkSqlTaskManagerVO managerVO = JsonUtils.objectToObject(taskManagerJsonObj, FlinkSqlTaskManagerVO.class);
                managerVO.setDownLoadLog(url);
                managerVO.setTotalBytes(total);
                managerVO.setName(name);
                taskManagerList.add(managerVO);
            }
        }
        taskManagerList.sort(Comparator.comparing(FlinkSqlTaskManagerVO::getId));
        return taskManagerList;
    }

    public RuntimeLogResultVO getJobManagerLog(Long taskId, Integer place, Long tenantId) {
        return getTaskRunTimeLog(taskId, null, null, place, JOB_MANAGER, tenantId);
    }

    public RuntimeLogResultVO getTaskManagerLog(Long taskId, String taskManagerId, Integer currentPage, int place, Long dtuicTenantId) {
        return getTaskRunTimeLog(taskId, taskManagerId, currentPage, place, TASK_MANAGER, dtuicTenantId);
    }

    private RuntimeLogResultVO getTaskRunTimeLog(Long taskId, String taskManagerId, Integer page, Integer place, String logType, Long tenantId) {
        Task task = developTaskMapper.selectById(taskId);
        AssertUtils.notNull(task, "任务不存在");
        RuntimeLogResultVO runtimeLog = new RuntimeLogResultVO();
        try {
            runtimeLog = dealRuntimeLog(taskId, taskManagerId, page, place, logType, tenantId);
            return runtimeLog;
        } catch (Exception e) {
            //记录日志，不做其他处理
            logger.error("get taskManager log error,{}", e.getMessage(), e);
        }
        try {
            ActionLogVO log = actionService.log(task.getJobId());
            if (log != null) {
                runtimeLog.setEngineLog(log.getEngineLog());
                runtimeLog.setTotalBytes(0);
            }
        } catch (Exception e) {
            //记录日志，不做其他处理
            logger.error("get log error {}", e.getMessage(), e);
        }
        return runtimeLog;
    }

    /**
     * 分页处理taskManager实时日志，一页1MB
     *
     * @param taskId
     * @param taskManagerId
     * @param page          页码
     * @param place         起始位
     * @return
     */
    private RuntimeLogResultVO dealRuntimeLog(Long taskId, String taskManagerId, Integer page, Integer place, String logType, Long tenantId) throws Exception {
        Map<String, Object> logInfo = null;
        if (JOB_MANAGER.equals(logType)) {
            //获取jobManager日志相关信息
            logInfo = getJobManagerLogInfo(taskId, tenantId);
        } else if (TASK_MANAGER.equals(logType)) {
            //获取taskManager日志相关信息
            logInfo = getTaskManagerLogInfo(taskId
                    , taskManagerId, tenantId);
        }
        if (CollectionUtils.isEmpty(logInfo)) {
            return new RuntimeLogResultVO();
        }
        RuntimeLogResultVO runtimeLog = new RuntimeLogResultVO();
        //获取集群类型
        String clusterMode = getClusterMode(tenantId);
        //获取到总字节数
        int totalBytes = MapUtils.getIntValue(logInfo, TOTAL_BYTES, 0);
        //计算总页码
        int totalPage = totalBytes % MAX_PAGE_SIZE == 0 ? totalBytes / MAX_PAGE_SIZE : totalBytes / MAX_PAGE_SIZE + 1;
        runtimeLog.setTotalPage(totalPage);
        runtimeLog.setPlace(totalBytes);
        //获取日志地址
        String url = (String) logInfo.get(DOWNLOAD_URL);
        //起始位置
        int start = 0;
        //结束位置
        int end = 0;
        //处理taskManager日志分页
        if (TASK_MANAGER.equals(logType)) {

            //正序分页
            start = (page - 1) * MAX_PAGE_SIZE;
            end = (page) * MAX_PAGE_SIZE;

        }
        //处理jobManager日志分页
        if (JOB_MANAGER.equals(logType)) {
            if (place < 0) {
                start = (totalPage - 1) * MAX_PAGE_SIZE;
                end = totalBytes;
            } else {
                start = place;
                end = totalBytes;
            }
        }
        AssertUtils.isTrue(ClusterMode.YARN.getVal().equals(clusterMode), "暂不支持" + clusterMode + "调度引擎类型的日志获取");
        //最终url
        String logUrl = String.format("%s?start=%s&end=%s", url, start, end);
        logger.info("get log Url{}", logUrl);
        IRestful restful = ClientCache.getRestful(DataSourceType.RESTFUL.getVal());
        RestfulSourceDTO sourceDTO = RestfulSourceDTO.builder().url(logUrl).build();
        Response restResponse = restful.get(sourceDTO, null, null, null);
        runtimeLog.setEngineLog(getEntityPre(restResponse.getContent()));


        return runtimeLog;
    }

    /**
     * 使用Jsoup解析网页上日志
     *
     * @param responseEntity
     * @return
     */
    private String getEntityPre(String responseEntity) {
        if (StringUtils.isBlank(responseEntity)) {
            logger.warn("获取网页信息为空");
            return StringUtils.EMPTY;
        }
        Document document = Jsoup.parse(responseEntity);
        Elements pres = document.getElementsByTag("pre");
        if (pres.size() == 0) {
            return StringUtils.EMPTY;
        }
        return pres.get(0).html().replace("&lt;", "<").replace("&gt;", ">");
    }

    /**
     * 获取taskManagerLog信息，包括日志名称 name、日志总字节数 totalbytes、日志路径 url
     *
     * @param taskId        任务id
     * @param taskManagerId
     * @return
     */
    private Map<String, Object> getTaskManagerLogInfo(Long taskId, String taskManagerId, Long tenantId) {
        List<FlinkSqlTaskManagerVO> managerVOS = listTaskManagerByJobId(taskId, tenantId);
        HashMap<String, Object> taskInfo = Maps.newHashMap();
        if (managerVOS != null && !managerVOS.isEmpty()) {
            for (FlinkSqlTaskManagerVO managerVO : managerVOS) {
                if (taskManagerId.equals(managerVO.getId())) {
                    taskInfo.put(DOWNLOAD_URL, managerVO.getDownLoadLog());
                    taskInfo.put(TOTAL_BYTES, managerVO.getTotalBytes());
                    taskInfo.put(LOG_NAME, managerVO.getName());
                }
            }
            return taskInfo;
        }
        return taskInfo;
    }

    /**
     * 获取jobManagerLog信息，包括日志名称 name、日志总字节数 totalbytes、日志路径 url
     *
     * @param taskId
     * @return
     */
    private Map<String, Object> getJobManagerLogInfo(Long taskId, Long tenantId) {
        Task task = developTaskMapper.selectById(taskId);
        AssertUtils.notNull(task, "任务不存在");
        List<FlinkSqlRuntimeLogDTO> runtimeLogs = getTaskLogList(task.getJobId(), tenantId);
        if (CollectionUtils.isEmpty(runtimeLogs)) {
            return new HashMap<>();
        }
        HashMap<String, Object> jobInfo = Maps.newHashMap();
        for (FlinkSqlRuntimeLogDTO runtimeLog : runtimeLogs) {
            if (JOB_MANAGER.equals(runtimeLog.getTypeName())) {
                List<Map<String, Object>> logs = runtimeLog.getLogs();
                String url = null;
                int total = 0;
                String name = null;
                if (logs != null && !logs.isEmpty()) {
                    //对logs中的几种日志取.log结尾的日志
                    Map<String, Object> jobLog = logs.stream()
                            .filter(log -> MapUtils.getString(log, LOG_NAME).trim().endsWith(".log"))
                            .findAny()
                            .orElseThrow(() -> new DtCenterDefException("日志文件不存在！"));
                    url = MapUtils.getString(jobLog, DOWNLOAD_URL);
                    name = MapUtils.getString(jobLog, LOG_NAME);
                    total = MapUtils.getInteger(jobLog, TOTAL_BYTES);
                }
                jobInfo.put(DOWNLOAD_URL, url);
                jobInfo.put(TOTAL_BYTES, total);
                jobInfo.put(LOG_NAME, name);
            }
        }
        return jobInfo;
    }

    public String getClusterMode(Long tenantId) {
        //todo 目前只有yarn
        return ClusterMode.YARN.getVal();
    }

    /**
     * 获取StreamRuntimeLogDTO集合
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    private List<FlinkSqlRuntimeLogDTO> getTaskLogList(String jobId, Long tenantId) {
        if (clusterService.hasStandalone(tenantId, EComponentType.FLINK.getTypeCode())) {
            //todo standalone 暂不支持
            return Lists.newArrayList();
        }
        ScheduleJob scheduleJob = getByJobId(jobId);
        AssertUtils.notNull(scheduleJob, "任务不存在");
        Integer status = scheduleJob.getStatus();
        List<FlinkSqlRuntimeLogDTO> streamTaskLogList = Lists.newArrayList();
        String clusterMode = getClusterMode(tenantId);
        //不是运行状态的任务无法通过engine获取到taskManagerId，通过轮询日志来获取
        if (!TaskStatus.RUNNING.getStatus().equals(status)
                && ClusterMode.YARN.getVal().equals(clusterMode)) {
            //从yarn聚合日志中获取
            DownloadLogVO downloadLogVO = new DownloadLogVO();
            downloadLogVO.setJobId(jobId);
            downloadLogVO.setTenantId(tenantId);
            LogPluginDownload download = (LogPluginDownload) flinkDownloadLogService.downloadJobLog(downloadLogVO);
            if (Objects.nonNull(download)) {
                List<String> containers = download.getTaskManagerList();
                for (String container : containers) {
                    JSONObject otherInfo = new JSONObject();
                    otherInfo.put("id", container);
                    FlinkSqlRuntimeLogDTO logDTO = new FlinkSqlRuntimeLogDTO();
                    logDTO.setTypeName(TASK_MANAGER);
                    logDTO.setOtherInfo(otherInfo.toJSONString());
                    streamTaskLogList.add(logDTO);
                }
            }
            return streamTaskLogList;
        }
        //获取engine传过来的日志相关信息
        List<String> logInfo = getRunningTaskLogUrl(jobId);

        if (logInfo == null || CollectionUtils.isEmpty(logInfo)) {
            return new ArrayList<>();
        }
        for (String logs : logInfo) {
            streamTaskLogList.add(JsonUtils.objectToObject(JSON.parseObject(logs), FlinkSqlRuntimeLogDTO.class));
        }
        return streamTaskLogList;
    }


    public List<String> getRunningTaskLogUrl(String jobId) {
        ScheduleJob scheduleJob = jobService.getScheduleJob(jobId);
        Preconditions.checkState(org.apache.commons.lang3.StringUtils.isNotEmpty(jobId), "jobId can't be empty");
        Preconditions.checkNotNull(scheduleJob, "can't find record by jobId" + jobId);

        //只获取运行中的任务的log—url
        Integer status = scheduleJob.getStatus();
        if (!TaskStatus.RUNNING.getStatus().equals(status)) {
            throw new TaierDefineException(String.format("job:%s not running status ", jobId), ErrorCode.INVALID_TASK_STATUS);
        }

        String applicationId = scheduleJob.getApplicationId();

        if (org.apache.commons.lang3.StringUtils.isEmpty(applicationId)) {
            throw new TaierDefineException(String.format("job %s not running in perjob", jobId), ErrorCode.INVALID_TASK_RUN_MODE);
        }
        try {
            ScheduleJobCache engineJobCache = ScheduleJobCacheService.getByJobId(jobId);
            if (engineJobCache == null) {
                throw new TaierDefineException(String.format("job:%s not exist in job cache table ", jobId), ErrorCode.JOB_CACHE_NOT_EXIST);
            }
            String jobInfo = engineJobCache.getJobInfo();
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);

            JobIdentifier jobIdentifier = new JobIdentifier(scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), jobId, scheduleJob.getTenantId(),
                    scheduleJob.getTaskType(),
                    EDeployMode.PERJOB.getType(), scheduleJob.getCreateUserId(), null, paramAction.getComponentVersion(), paramAction.getQueueName());
            return workerOperator.getRollingLogBaseInfo(jobIdentifier);
        } catch (Exception e) {
            throw new TaierDefineException(String.format("get job:%s ref application url error..", jobId), ErrorCode.UNKNOWN_ERROR, e);
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


    public String loadJobLogWithEngineJob(Long tenantId, Integer taskType, String applicationId, Integer byteNum, String taskManagerId) {
        if (clusterService.hasStandalone(tenantId, EComponentType.FLINK.getTypeCode())) {
            //todo standalone 暂不支持
            return "";
        }
        IDownloader downloader = this.downloadJobLogWithEngineJob(applicationId, taskType, tenantId, byteNum == null ? DEFAULT_LOG_PREVIEW_BYTES : byteNum, taskManagerId);

        return readJobLog(downloader);
    }

    private String readJobLog(IDownloader downloader) {
        if (downloader == null) {
            logger.error("-----log download is null -----");
            return "";
        }
        StringBuilder result = new StringBuilder();
        while (!downloader.reachedEnd()) {
            Object row = downloader.readNext();
            result.append(row);
        }
        return result.toString();
    }

    public IDownloader downloadJobLogWithEngineJob(String applicationId, Integer taskType, Long tenantId, Integer limitNum, String taskManagerId) {
        Map yarnConf = clusterService.getComponentByTenantId(tenantId, EComponentType.YARN.getTypeCode(), false,
                Map.class, null);
        Map hadoopConf = clusterService.getComponentByTenantId(tenantId, EComponentType.HDFS.getTypeCode(), false,
                Map.class, null);
        AssertUtils.notNull(yarnConf, "获取yarn集群信息失败");
        AssertUtils.notNull(hadoopConf, "获取hdfs信息失败");
        try {
            return new LogPluginDownload(applicationId, yarnConf, hadoopConf, null, taskManagerId, limitNum).getHdfsLogDownloader();
        } catch (Exception e) {
            logger.error("downloadJobLog 失败:" + e.getMessage(), e);
            return null;
        }
    }

    //todo
    public PageResult<List<ScheduleJobHistory>> getHistoryList(Long taskId, Integer currentPage, Integer pageSize) {
        return null;
    }
}
