//package com.dtstack.batch.service.schedule;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.dtstack.engine.common.CustomThreadRunsPolicy;
//import com.dtstack.engine.common.enums.IsDeletedEnum;
//import com.dtstack.engine.common.env.EnvironmentContext;
//import com.dtstack.engine.common.util.AddressUtil;
//import com.dtstack.engine.common.util.DtJobIdWorker;
//import com.dtstack.engine.domain.EngineJobRetry;
//import com.dtstack.engine.domain.ScheduleJob;
//import com.dtstack.engine.domain.ScheduleJobExpand;
//import com.dtstack.engine.domain.ScheduleTaskShade;
//import com.dtstack.engine.master.action.restart.RestartJobRunnable;
//import com.dtstack.engine.master.enums.RestartType;
//import com.dtstack.engine.master.jobdealer.JobDealer;
//import com.dtstack.engine.master.vo.JobLogVO;
//import com.dtstack.engine.master.vo.action.ActionRetryLogVO;
//import com.dtstack.engine.pluginapi.CustomThreadFactory;
//import com.dtstack.engine.pluginapi.exception.ErrorCode;
//import com.dtstack.engine.pluginapi.exception.RdosDefineException;
//import com.google.common.base.Strings;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Auther: dazhi
// * @Date: 2021/12/28 9:45 AM
// * @Email:dazhi@dtstack.com
// * @Description:
// */
//@Service
//public class ActionService {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);
//
//    private DtJobIdWorker jobIdWorker;
//
//    @Autowired
//    private JobDealer jobDealer;
//
//    @Autowired
//    private JobService jobService;
//
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private JobExpandService jobExpandService;
//
//    @Autowired
//    private EnvironmentContext environmentContext;
//
//    private ThreadPoolExecutor logTimeOutPool =  new ThreadPoolExecutor(5, 5,
//            60L,TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
//            new CustomThreadFactory("logTimeOutPool"),
//            new CustomThreadRunsPolicy("logTimeOutPool", "log"));
//
//    /**
//     * 查看日志
//     *
//     * @param jobId 实例id
//     * @param pageInfo 分页信息
//     * @return 日志信息
//     */
//    public JobLogVO logUnite(String jobId, Integer pageInfo) {
//        ScheduleJob scheduleJob = jobService.lambdaQuery().eq(ScheduleJob::getJobId,jobId).eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType()).one();
//
//        if (scheduleJob == null) {
//            throw new RdosDefineException("job is not exist");
//        }
//
//        ScheduleTaskShade taskShade = taskService.lambdaQuery().eq(ScheduleTaskShade::getTaskId,scheduleJob.getTaskId()).eq(ScheduleTaskShade::getIsDeleted,IsDeletedEnum.NOT_DELETE.getType()).one();
//
//        if (taskShade == null) {
//            throw new RdosDefineException("task is not exist");
//        }
//
//        ScheduleJobExpand scheduleJobExpand = jobExpandService.lambdaQuery().eq(ScheduleJobExpand::getJobId, jobId).eq(ScheduleJobExpand::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType()).one();
//
//        if (scheduleJobExpand == null) {
//            throw new RdosDefineException("jobExpand is not exist");
//        }
//
//        JobLogVO jobLogVO = new JobLogVO();
//        jobLogVO.setName(taskShade.getName());
//        jobLogVO.setComputeType(taskShade.getComputeType());
//        jobLogVO.setTaskType(taskShade.getTaskType());
//
//        String engineLog = getEngineLog(jobId, scheduleJob,scheduleJobExpand);
//        jobLogVO.setEngineLog(engineLog);
//
//        // 封装日志信息
//        JSONObject info = new JSONObject();
//        try {
//            info = JSON.parseObject(scheduleJobExpand.getLogInfo());
//        } catch (final Exception e) {
//            LOGGER.error("parse jobId {} } logInfo error {}", jobId, scheduleJobExpand.getLogInfo());
//            info.put("msg_info", scheduleJobExpand.getLogInfo());
//        }
//
//        if (info == null) {
//            info = new JSONObject();
//        }
//
//        info.put("sql", taskShade.getSqlText());
//        info.put("engineLogErr", engineLog);
//        jobLogVO.setLogInfo(info.toJSONString());
//        try {
//            if (scheduleJob.getRetryNum() > 0) {
//                String retryLog = buildRetryLog(scheduleJob.getJobId(), pageInfo, jobLogVO);
//                if (StringUtils.isNotBlank(retryLog)) {
//                    jobLogVO.setLogInfo(retryLog);
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("", e);
//        }
//
//        return jobLogVO;
//    }
//
//    private String buildRetryLog(final String jobId, Integer pageInfo,JobLogVO batchServerLogVO) throws Exception {
//        //先获取engine的日志总数信息
//        List<ActionRetryLogVO> actionRetryLogVOs = retryLog(jobId);
//        if (CollectionUtils.isEmpty(actionRetryLogVOs)) {
//            return "";
//        }
//        batchServerLogVO.setPageSize(actionRetryLogVOs.size());
//        if(Objects.isNull(pageInfo)){
//            pageInfo = 0;
//        }
//        //engine 的 retryNum 从1 开始
//        if (0 == pageInfo) {
//            pageInfo = actionRetryLogVOs.size();
//        }
//        if (pageInfo > actionRetryLogVOs.size()) {
//            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
//        }
//        //获取对应的日志
//        ActionRetryLogVO retryLogContent = retryLogDetail(jobId, pageInfo);
//        StringBuilder builder = new StringBuilder();
//        if (Objects.isNull(retryLogContent)) {
//            return "";
//        }
//        Integer retryNumVal = retryLogContent.getRetryNum();
//        int retryNum = 0;
//        if(Objects.nonNull(retryNumVal)){
//            retryNum = retryNumVal + 1;
//        }
//        String logInfo = retryLogContent.getLogInfo();
//        String engineInfo = retryLogContent.getEngineLog();
//        String retryTaskParams = retryLogContent.getRetryTaskParams();
//        builder.append("====================第 ").append(retryNum).append("次重试====================").append("\n");
//
//        if (!Strings.isNullOrEmpty(logInfo)) {
//            builder.append("====================LogInfo start====================").append("\n");
//            builder.append(logInfo).append("\n");
//            builder.append("=====================LogInfo end=====================").append("\n");
//        }
//        if (!Strings.isNullOrEmpty(engineInfo)) {
//            builder.append("==================EngineInfo  start==================").append("\n");
//            builder.append(engineInfo).append("\n");
//            builder.append("===================EngineInfo  end===================").append("\n");
//        }
//        if (!Strings.isNullOrEmpty(retryTaskParams)) {
//            builder.append("==================RetryTaskParams  start==================").append("\n");
//            builder.append(retryTaskParams).append("\n");
//            builder.append("===================RetryTaskParams  end===================").append("\n");
//        }
//
//        builder.append("==================第").append(retryNum).append("次重试结束==================").append("\n");
//        for (int j = 0; j < 10; j++) {
//            builder.append("==" + "\n");
//        }
//
//        return builder.toString();
//    }
//
//    /**
//     * 根据jobid 和 计算类型，查询job的重试retry日志
//     */
//    public List<ActionRetryLogVO> retryLog( String jobId) {
//
//        if (StringUtils.isBlank(jobId)){
//            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
//        }
//        List<ActionRetryLogVO> logs = new ArrayList<>(5);
//        List<EngineJobRetry> batchJobRetrys = engineJobRetryDao.listJobRetryByJobId(jobId);
//        if (CollectionUtils.isNotEmpty(batchJobRetrys)) {
//            batchJobRetrys.forEach(jobRetry->{
//                ActionRetryLogVO vo = new ActionRetryLogVO();
//                vo.setRetryNum(jobRetry.getRetryNum());
//                vo.setLogInfo(jobRetry.getLogInfo());
//                vo.setRetryTaskParams(jobRetry.getRetryTaskParams());
//                logs.add(vo);
//            });
//        }
//        return logs;
//    }
//
//    /**
//     * 根据jobid 和 计算类型，查询job的重试retry日志
//     */
//    public ActionRetryLogVO retryLogDetail( String jobId, Integer retryNum) {
//
//        if (StringUtils.isBlank(jobId)){
//            throw new RdosDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
//        }
//        if (retryNum == null || retryNum <= 0) {
//            retryNum = 1;
//        }
//        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
//        //数组库中存储的retryNum为0开始的索引位置
//        EngineJobRetry jobRetry = engineJobRetryDao.getJobRetryByJobId(jobId, retryNum - 1);
//        ActionRetryLogVO vo = new ActionRetryLogVO();
//        if (jobRetry != null) {
//            vo.setRetryNum(jobRetry.getRetryNum());
//            vo.setLogInfo(jobRetry.getLogInfo());
//            String engineLog = jobRetry.getEngineLog();
//            if (StringUtils.isBlank(jobRetry.getEngineLog())){
//                engineLog = jobDealer.getAndUpdateEngineLog(jobId, jobRetry.getEngineJobId(), jobRetry.getApplicationId(), scheduleJob.getTenantId());
//                if (engineLog != null){
//                    LOGGER.info("engineJobRetryDao.updateEngineLog id:{}, jobId:{}, engineLog:{}", jobRetry.getId(), jobRetry.getJobId(), engineLog);
//                    engineJobRetryDao.updateEngineLog(jobRetry.getId(), engineLog);
//                } else {
//                    engineLog = "";
//                }
//            }
//            vo.setEngineLog(engineLog);
//            vo.setRetryTaskParams(jobRetry.getRetryTaskParams());
//
//        }
//        return vo;
//    }
//
//
//    private String getEngineLog(String jobId, ScheduleJob scheduleJob,ScheduleJobExpand scheduleJobExpand) {
//        String engineLog = scheduleJobExpand.getEngineLog();
//        try {
//            if (StringUtils.isBlank(engineLog)) {
//                engineLog = CompletableFuture.supplyAsync(
//                        () ->
//                                jobDealer.getAndUpdateEngineLog(jobId, scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), scheduleJob.getTenantId()),
//                        logTimeOutPool
//                ).get(environmentContext.getLogTimeout(), TimeUnit.SECONDS);
//                if (engineLog == null) {
//                    engineLog = "";
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("",e);
//        }
//        return engineLog;
//    }
//
//
//    public boolean restartJob(RestartType restartType, List<String> jobIds) {
////        CompletableFuture.runAsync(new RestartJobRunnable(jobIds,restartType, environmentContext));
//        return false;
//    }
//
//
//    public String generateUniqueSign() {
//        if (null == jobIdWorker) {
//            String[] split = AddressUtil.getOneIp().split("\\.");
//            jobIdWorker = DtJobIdWorker.getInstance(split.length >= 4 ? Integer.parseInt(split[3]) : 0, 0);
//        }
//        return jobIdWorker.nextJobId();
//    }
//}
