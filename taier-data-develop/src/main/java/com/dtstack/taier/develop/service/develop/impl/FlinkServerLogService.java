package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.mapper.DevelopTaskMapper;
import com.dtstack.taier.develop.dto.devlop.FlinkServerLogVO;
import com.dtstack.taier.develop.dto.devlop.ServerLogsVO;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.dtstack.taier.scheduler.service.ScheduleJobExpandService;
import com.dtstack.taier.scheduler.vo.action.ActionRetryLogVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by qianyi on 2022/3/16.
 */
@Service
public class FlinkServerLogService {

    @Autowired
    private DevelopTaskMapper developTaskMapper;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private FlinkRuntimeLogService flinkRuntimeLogService;

    private static final Logger logger = LoggerFactory.getLogger(FlinkServerLogService.class);

    private static final String DOWNLOAD_LOG = ConfigConstant.REQUEST_PREFIX + "/download/streamDownload/downloadJobLog?jobId=%s&taskType=%s&taskManagerId=%s";

    /**
     * 针对引擎获取的基本日志可能是String或者json
     * @param logInfo
     * @return
     */
    public String getStringLog(String logInfo) {
        if (StringUtils.isEmpty(logInfo)) {
            return "";
        }
        JSONObject info;
        try {
            info = JSON.parseObject(logInfo);
        } catch (Exception e) {
            return logInfo;
        }
        if (StringUtils.isNotEmpty(info.getString("msg_info"))) {
            return info.getString("msg_info");
        }
        return logInfo;
    }

    public FlinkServerLogVO getLogsByTaskId(ServerLogsVO logsVO) {
        Task task = developTaskMapper.selectById(logsVO.getTaskId());
        if (task == null) {
            throw new DtCenterDefException("任务不存在");
        }

        ScheduleJobExpand scheduleJobExpand = null;
        ScheduleJob scheduleJob = null;
        try {
            scheduleJob = flinkRuntimeLogService.getByJobId(task.getJobId());
            scheduleJobExpand = scheduleJobExpandService.getByJobId(task.getJobId());
        } catch (Exception e) {
            logger.error("任务{}从Engine获取任务失败{}", task.getJobId(), e.getMessage(), e);
            throw new DtCenterDefException(String.format("从Engine获取任务失败,Caused by: %s", e.getMessage()), e);
        }
        if (scheduleJobExpand == null) {
            return null;
        }

        String downLoadUrl = null;
        String taskManagerId = logsVO.getTaskManagerId();
        if (StringUtils.isBlank(taskManagerId)) {
            taskManagerId = "";
        }
        String logInfo = getStringLog(scheduleJobExpand.getLogInfo());
        //展示重试日志
        List<ActionRetryLogVO> apiResponse = actionService.retryLog(task.getJobId());
        if (CollectionUtils.isNotEmpty(apiResponse)) {
            int logSize = Math.min(apiResponse.size(), 3);
            //取最近的提交日志
            List<ActionRetryLogVO> subLogs = apiResponse.subList(0, logSize);
            Collections.reverse(subLogs);
            int retryNum = 1;
            StringBuilder str = new StringBuilder();
            for (ActionRetryLogVO actionRetryLogVO : subLogs) {
                str.append(String.format("retry num %s", retryNum)).append("\n").append(getStringLog(actionRetryLogVO.getLogInfo())).append("\n");
                retryNum++;
            }
            logInfo = str.toString();
        }

        scheduleJobExpand.setLogInfo(logInfo);
        if (task.getTaskType().equals(EScheduleJobType.DATA_ACQUISITION.getVal())) {
            if (StringUtils.isNotEmpty(scheduleJobExpand.getJobId())) {
                String engineLog = scheduleJobExpand.getEngineLog() == null ? "" : scheduleJobExpand.getEngineLog() + "\n";
                //yarn日志下载
                downLoadUrl = String.format(DOWNLOAD_LOG, task.getJobId(), task.getTaskType(), taskManagerId);
                scheduleJobExpand.setEngineLog(engineLog + flinkRuntimeLogService.loadJobLogWithEngineJob(logsVO.getTenantId(), task.getTaskType(), scheduleJob.getApplicationId(), null, taskManagerId));

            }
        } else if (task.getTaskType().equals(EScheduleJobType.FLINK_SQL.getVal())) {
            //yarn日志下载
            scheduleJobExpand.setEngineLog(flinkRuntimeLogService.loadJobLogWithEngineJob(logsVO.getTenantId(), task.getTaskType(), scheduleJob.getApplicationId(), null, taskManagerId));
            downLoadUrl = String.format(DOWNLOAD_LOG, task.getJobId(), task.getTaskType(), taskManagerId);
        }
        return new FlinkServerLogVO(scheduleJobExpand, downLoadUrl, "");
    }


    public String getFailoverLogsByTaskId(ServerLogsVO logsVO) {
        Task task = developTaskMapper.selectById(logsVO.getTaskId());
        ScheduleJobExpand  scheduleJobExpand = scheduleJobExpandService.getByJobId(task.getJobId());
        return scheduleJobExpand.getEngineLog();
    }

}
