package com.dtstack.taier.scheduler.server.scheduler.exec;

import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.JobCheckStatus;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.enums.RelyRule;
import com.dtstack.taier.scheduler.enums.RelyType;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/12 7:28 PM
 * @Email: dazhi@dtstack.com
 * @Description: 校验实例上游是是否全部运行成功
 */
@Component
public class JobUpStreamJudgeJobExecOperator implements JudgeJobExecOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobUpStreamJudgeJobExecOperator.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public JobCheckRunInfo isExec(ScheduleJobDetails scheduleJobDetails) {
        List<ScheduleJobJob> jobJobList = scheduleJobDetails.getJobJobList();
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        JobCheckRunInfo checkRunInfo = new JobCheckRunInfo();

        if (CollectionUtils.isNotEmpty(jobJobList)) {
            List<String> parentJobKeys = jobJobList.stream().map(ScheduleJobJob::getParentJobKey).collect(Collectors.toList());

            Map<String, ScheduleJob> scheduleJobMap = scheduleJobService.lambdaQuery()
                    .select(ScheduleJob::getStatus, ScheduleJob::getJobId, ScheduleJob::getJobKey, ScheduleJob::getJobName)
                    .in(ScheduleJob::getJobKey, parentJobKeys)
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list().stream().collect(Collectors.toMap(ScheduleJob::getJobKey, g -> (g)));


            for (ScheduleJobJob scheduleJobJob : jobJobList) {
                ScheduleJob parentScheduleJob = scheduleJobMap.get(scheduleJobJob.getParentJobKey());
                // 父实例没有生成 这里有两种情况：
                // 1. 补数据实例父实例没有生成,就不去判断父实例状态 2. 周期实例父实例没有生成，就直接设置成实例失败，原因是父实例没有生成
                if (parentScheduleJob == null) {
                    if (EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleJob.getType())) {
                        checkRunInfo.setPass(Boolean.FALSE);
                        checkRunInfo.setStatus(JobCheckStatus.FATHER_NO_CREATED);
                        checkRunInfo.setLogInfo(String.format(JobCheckStatus.FATHER_NO_CREATED.getMsg(),scheduleJob.getJobName(),scheduleJob.getJobId(),scheduleJobJob.getParentJobKey()));
                        return checkRunInfo;
                    } else {
                        continue;
                    }
                }

                // 判断上游依赖规则
                //  1. 父实例运行完成，可以运行,也就是说父实例状态不影响子任务状态
                //  2. 父实例运行成功，可以运行,也就是说父实例影响子任务状态
                Integer rule = scheduleJobJob.getRule();
                Integer status = parentScheduleJob.getStatus();
                if (RelyRule.RUN_SUCCESS.getType().equals(rule)) {
                    Integer jobKeyType = scheduleJobJob.getJobKeyType();
                    // 父任务有运行失败的
                    if (TaskStatus.FAILED.getStatus().equals(status)
                            || TaskStatus.SUBMITFAILD.getStatus().equals(status)
                            || TaskStatus.PARENTFAILED.getStatus().equals(status)) {
                        checkRunInfo.setPass(Boolean.FALSE);
                        checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_EXCEPTION);
                        checkRunInfo.setLogInfo(String.format(JobCheckStatus.FATHER_JOB_EXCEPTION.getMsg(),parentScheduleJob.getJobName(),parentScheduleJob.getJobId()));
                        return checkRunInfo;
                    }

                    // 父实例是冻结(但是这些实例不能是自依赖,自依赖实例是用自己任务的状态判断是否冻结)
                    if (TaskStatus.FROZEN.getStatus().equals(status) && !RelyType.SELF_RELIANCE.getType().equals(jobKeyType)) {
                        checkRunInfo.setPass(Boolean.FALSE);
                        checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_FROZEN);
                        checkRunInfo.setLogInfo(String.format(JobCheckStatus.FATHER_JOB_FROZEN.getMsg(),parentScheduleJob.getJobName(),parentScheduleJob.getJobId()));
                        return checkRunInfo;
                    }

                    // 父实例是取消
                    if (TaskStatus.CANCELED.getStatus().equals(status)
                            || TaskStatus.KILLED.getStatus().equals(status)
                            || TaskStatus.AUTOCANCELED.getStatus().equals(status)) {
                        checkRunInfo.setPass(Boolean.FALSE);
                        checkRunInfo.setStatus(JobCheckStatus.DEPENDENCY_JOB_CANCELED);
                        checkRunInfo.setLogInfo(String.format(JobCheckStatus.DEPENDENCY_JOB_CANCELED.getMsg(),scheduleJob.getJobName(),scheduleJob.getJobId(),parentScheduleJob.getJobName(),parentScheduleJob.getJobId()));
                        return checkRunInfo;
                    }
                }

                if (!TaskStatus.FINISHED.getStatus().equals(status) &&
                        !TaskStatus.MANUALSUCCESS.getStatus().equals(status)) {
                    checkRunInfo.setPass(Boolean.FALSE);
                    checkRunInfo.setStatus(JobCheckStatus.FATHER_JOB_NOT_FINISHED);
                    checkRunInfo.setLogInfo(JobCheckStatus.FATHER_JOB_NOT_FINISHED.getMsg());
                    return checkRunInfo;
                }
            }
        }

        checkRunInfo.setPass(Boolean.TRUE);
        return checkRunInfo;
    }
}
