package com.dtstack.engine.master.server.scheduler;

import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.master.server.ScheduleJobDetails;
import com.dtstack.engine.master.server.scheduler.exec.JobCheckRunInfo;
import com.dtstack.engine.master.server.scheduler.exec.JudgeJobExecOperator;
import com.dtstack.engine.master.server.scheduler.handler.JudgeNoPassJobHandler;
import com.dtstack.engine.master.service.ScheduleJobService;
import com.dtstack.engine.master.service.ScheduleTaskService;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 3:41 PM
 * @Email: dazhi@dtstack.com
 * @Description: 任务扫描执行器
 */
public abstract class AbstractJobScanningScheduler implements Scheduler, InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractJobScanningScheduler.class);

    @Autowired
    protected ZkService zkService;

    @Autowired
    protected EnvironmentContext env;

    @Autowired
    protected ScheduleJobService scheduleJobService;

    @Autowired
    protected ScheduleTaskService scheduleTaskService;

    /**
     * 获得实例列表
     * @param startSort 开始id
     * @param nodeAddress 查询的实例对应的节点
     * @param isEq sql中是否包含第一个
     * @return 实例列表
     */
    protected abstract List<ScheduleJobDetails> listExecJob(Long startSort, String nodeAddress, Boolean isEq);

    /**
     * 获得排序最小序号
     * @return 最小序号
     */
    protected abstract Long getMinSort();

    /**
     * 获得实例可执行条件，只有通过了条件判断才能会提交任务
     *
     * @return JudgeJob列表
     */
    protected abstract List<JudgeJobExecOperator> getJudgeJobExecOperator();

    /**
     * 获得实例条件处理集合
     *
     * @return JobHandler列表
     */
    protected abstract List<JudgeNoPassJobHandler> getJudgeNoPassJobHandler();

    /**
     * 扫描实例
     */
    private void scanningJob () {
        try {
            String nodeAddress = zkService.getLocalAddress();
            // 1. 获得节点信息
            if (StringUtils.isBlank(nodeAddress)) {
                return;
            }
            LOGGER.info("scanningJob start scheduleType : {} nodeAddress:{}", getScheduleType().name(),nodeAddress);

            // 2. 获得排序最小序号
            Long minSort = getMinSort();
            LOGGER.info("scanning start param: scheduleType {} nodeAddress {} minSort {} ", getScheduleType(), nodeAddress, minSort);

            // 3. 扫描实例
            List<ScheduleJobDetails> scheduleJobDetails = listExecJob(minSort, nodeAddress, Boolean.TRUE);
            while (CollectionUtils.isNotEmpty(scheduleJobDetails)) {
                // 查询任务
                List<Long> taskIds = scheduleJobDetails.stream().map(ScheduleJobDetails::getScheduleJob).map(ScheduleJob::getTaskId).collect(Collectors.toList());
                Map<Long, ScheduleTaskShade> scheduleTaskShadeMap = scheduleTaskService.lambdaQuery()
                        .in(ScheduleTaskShade::getTaskId, taskIds)
                        .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                        .list()
                        .stream()
                        .collect(Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g)));

                for (ScheduleJobDetails scheduleJobDetail : scheduleJobDetails) {
                    // 提交实例
                    ScheduleJob scheduleJob = scheduleJobDetail.getScheduleJob();
                    ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeMap.get(scheduleJob.getTaskId());

                    if (scheduleTaskShade == null) {
                        String errMsg = JobCheckStatus.NO_TASK.getMsg();
                        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.SUBMITFAILD.getStatus(), errMsg);
                        LOGGER.warn("jobId:{} scheduleType:{} submit failed for taskId:{} already deleted.", scheduleJob.getJobId(), getScheduleType(), scheduleJob.getTaskId());
                        continue;
                    }
                    scheduleJobDetail.setScheduleTaskShade(scheduleTaskShade);

                    if (isSubmitJob(scheduleJobDetail)) {
                        submitJob(scheduleJobDetail);
                    }

                    if (minSort < scheduleJob.getJobExecuteOrder()) {
                        minSort = scheduleJob.getJobExecuteOrder();
                    }
                }
                scheduleJobDetails = listExecJob(minSort, nodeAddress, Boolean.FALSE);
            }
        } catch (Exception e) {
            LOGGER.error("scheduleType:{} emitJob2Queue error:", getScheduleType(), e);
        }
    }

    /**
     *  判断是否通过提交校验
     * @param scheduleJobDetails 实例
     * @return 校验解雇
     */
    private Boolean isSubmitJob(ScheduleJobDetails scheduleJobDetails) {
        List<JudgeJobExecOperator> judgeJobExecOperator = getJudgeJobExecOperator();

        if (CollectionUtils.isNotEmpty(judgeJobExecOperator)) {
            for (JudgeJobExecOperator jobExecOperator : judgeJobExecOperator) {
                JobCheckRunInfo exec = jobExecOperator.isExec(scheduleJobDetails);

                if (!exec.getPass() && exec.getStatus() != null) {
                    // 没有通过校验 处理
                    List<JudgeNoPassJobHandler> judgeNoPassJobHandlerList = getJudgeNoPassJobHandler();

                    for (JudgeNoPassJobHandler judgeNoPassJobHandler : judgeNoPassJobHandlerList) {
                        if (judgeNoPassJobHandler.isSupportJobCheckStatus(exec.getStatus())) {
                            return judgeNoPassJobHandler.handlerJob(scheduleJobDetails,exec.getStatus());
                        }
                    }

                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing scheduleType:{} acquireQueueJobInterval:{} queueSize:{}", getScheduleType(), env.getAcquireQueueJobInterval(), env.getQueueSize());
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(getScheduleType() + "_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(this::scanningJob, 0, env.getAcquireQueueJobInterval(), TimeUnit.MILLISECONDS);
    }


}
