package com.dtstack.taier.develop.service.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.constant.CommonConstant;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DataFilter;
import com.dtstack.taier.dao.domain.ScheduleEngineJobRetry;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.dto.ScheduleTaskParamShade;
import com.dtstack.taier.develop.service.develop.impl.DevelopServerLogService;
import com.dtstack.taier.develop.vo.schedule.ReturnJobLogVO;
import com.dtstack.taier.scheduler.dto.schedule.ActionJobKillDTO;
import com.dtstack.taier.scheduler.enums.RestartType;
import com.dtstack.taier.scheduler.jobdealer.JobStopDealer;
import com.dtstack.taier.scheduler.server.action.restart.RestartJobRunnable;
import com.dtstack.taier.scheduler.server.pipeline.JobParamReplace;
import com.dtstack.taier.scheduler.service.ScheduleTaskShadeInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 9:45 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);

    @Autowired
    private JobService jobService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private JobRetryService jobRetryService;

    @Autowired
    private JobExpandService jobExpandService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScheduleTaskShadeInfoService scheduleTaskShadeInfoService;

    @Autowired
    private DevelopServerLogService developServerLogService;

    /**
     * 重跑实例
     *
     * @param restartType 重跑类型
     * @param jobIds      勾选的实例id
     * @return 是否开始重跑
     */
    public boolean restartJob(RestartType restartType, List<String> jobIds) {
        CompletableFuture.runAsync(new RestartJobRunnable(jobIds, restartType, environmentContext, applicationContext));
        return true;
    }

    /**
     * 批量杀死实例
     *
     * @param jobIds 实例id
     * @return 操作数
     */
    public Integer batchStopJobs(List<String> jobIds) {
        List<ScheduleJob> scheduleJobList = jobService.lambdaQuery()
                .in(ScheduleJob::getJobId, jobIds)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .list();
        return jobStopDealer.addStopJobs(scheduleJobList);
    }

    /**
     * 按照补数据批量杀死实例
     *
     * @param fillId 补数据id
     * @return 操作数
     */
    public Integer stopFillDataJobs(Long fillId) {
        List<ScheduleJob> scheduleJobList = jobService.lambdaQuery()
                .eq(ScheduleJob::getFillId, fillId)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .list();
        return jobStopDealer.addStopJobs(scheduleJobList);
    }

    /**
     * 按照条件杀死实例
     *
     * @param dto 条件dto
     * @return 操作数
     */
    public Integer stopJobByCondition(ActionJobKillDTO dto) {
        List<ScheduleJob> scheduleJobList = jobService.lambdaQuery()
                .eq(ScheduleJob::getTenantId, dto.getTenantId())
                .eq(ScheduleJob::getCreateUserId, dto.getUserId())
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .between(dto.getCycStartDay() != null && dto.getCycEndTimeDay() != null, ScheduleJob::getCycTime, jobService.getCycTime(dto.getCycStartDay()), jobService.getCycTime(dto.getCycEndTimeDay()))
                .eq(dto.getType() != null, ScheduleJob::getType, dto.getType())
                .in(CollectionUtils.isNotEmpty(dto.getTaskPeriodList()), ScheduleJob::getPeriodType, dto.getTaskPeriodList())
                .in(CollectionUtils.isNotEmpty(dto.getTaskIds()), ScheduleJob::getTaskId, dto.getTaskIds())
                .list();
        return jobStopDealer.addStopJobs(scheduleJobList);
    }

    /**
     * 查看周期实例日志
     *
     * @param jobId    实例id
     * @param pageInfo 第几次重试日志
     * @return 日志信息
     */
    public ReturnJobLogVO queryJobLog(String jobId, Integer pageInfo) {
        if (pageInfo == null) {
            pageInfo = 1;
        }

        // 查询周期实例
        ScheduleJob scheduleJob = jobService.lambdaQuery().eq(ScheduleJob::getJobId, jobId)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .one();

        if (scheduleJob == null) {
            throw new RdosDefineException("not find job,please contact the administrator");
        }

        //取最新
        if (0 == pageInfo) {
            pageInfo = scheduleJob.getRetryNum();
        }

        ReturnJobLogVO jobLogVO = new ReturnJobLogVO();
        jobLogVO.setPageIndex(pageInfo);
        jobLogVO.setPageSize(scheduleJob.getRetryNum());
        // 如果RetryNum>1 说明实例已经进行了一次重试，所以取查询重试日志
        if (scheduleJob.getRetryNum() > 1) {
            // 查询重试日志
            ScheduleEngineJobRetry scheduleEngineJobRetry = jobRetryService.lambdaQuery()
                    .eq(ScheduleEngineJobRetry::getJobId, jobId)
                    .eq(ScheduleEngineJobRetry::getRetryNum, pageInfo)
                    .eq(ScheduleEngineJobRetry::getIsDeleted, Deleted.NORMAL.getStatus())
                    .orderBy(true, false, ScheduleEngineJobRetry::getId)
                    .one();

            if (scheduleEngineJobRetry != null) {
                jobLogVO.setLogInfo(scheduleEngineJobRetry.getLogInfo());
                jobLogVO.setEngineLog(scheduleEngineJobRetry.getEngineLog());
            }
            jobLogVO.setPageIndex(pageInfo);
            jobLogVO.setPageSize(scheduleJob.getMaxRetryNum());

        } else {
            // 查询当前日志
            ScheduleJobExpand scheduleJobExpand = jobExpandService.lambdaQuery()
                    .eq(ScheduleJobExpand::getIsDeleted, Deleted.NORMAL.getStatus())
                    .eq(ScheduleJobExpand::getJobId, jobId)
                    .one();

            if (scheduleJobExpand != null) {
                jobLogVO.setLogInfo(scheduleJobExpand.getLogInfo());
                jobLogVO.setEngineLog(scheduleJobExpand.getEngineLog());
            }
        }

        // 封装sql信息
        ScheduleTaskShade scheduleTaskShade = taskService.lambdaQuery()
                .eq(ScheduleTaskShade::getTaskId, scheduleJob.getTaskId())
                .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                .one();


        if (null != scheduleTaskShade) {
            JSONObject shadeInfo = scheduleTaskShadeInfoService.getInfoJSON(scheduleTaskShade.getTaskId());
            String taskParams = shadeInfo.getString("taskParamsToReplace");
            List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray(taskParams, ScheduleTaskParamShade.class);
            String sqlText = scheduleTaskShade.getSqlText();
            if (EScheduleJobType.SYNC.getType().equals(scheduleTaskShade.getTaskType())) {
                // 密码脱敏，日志中sqlText只展示job部分。
                try {
                    //job需要反序列化后脱敏
                    JSONObject jobJson = JSON.parseObject(sqlText).getJSONObject("job");
                    DataFilter.passwordFilter(jobJson);
                    sqlText = jobJson.toJSONString();
                } catch (final Exception e) {
                    sqlText = sqlText.replaceAll("(\"password\"[^\"]+\")([^\"]+)(\")", "$1******$3");
                }
            }
            sqlText = JobParamReplace.paramReplace(sqlText, taskParamsToReplace, scheduleJob.getCycTime());
            jobLogVO.setSqlText(sqlText);
            Timestamp execStartTime = scheduleJob.getExecStartTime();
            Timestamp execEndTime = scheduleJob.getExecEndTime();
            if (EScheduleJobType.SYNC.getType().equals(scheduleTaskShade.getTaskType())) {
                String syncLog = null;
                try {
                    syncLog = developServerLogService.formatPerfLogInfo(scheduleJob.getEngineJobId(), scheduleJob.getJobId(),
                            Optional.ofNullable(execStartTime).orElse(Timestamp.valueOf(LocalDateTime.now())).getTime(),
                            Optional.ofNullable(execEndTime).orElse(Timestamp.valueOf(LocalDateTime.now())).getTime(),
                            scheduleJob.getTenantId());
                } catch (Exception e) {
                    LOGGER.error("queryJobLog {} sync log error", jobId, e);
                }
                jobLogVO.setSyncLog(syncLog);
            }

            if (EScheduleJobType.SPARK_SQL.getType().equals(scheduleTaskShade.getTaskType())) {
                jobLogVO.setDownLoadUrl(String.format(CommonConstant.DOWNLOAD_LOG, scheduleJob.getJobId(), scheduleJob.getTaskType(), scheduleJob.getTenantId()));
            }
        }
        return jobLogVO;
    }
}
