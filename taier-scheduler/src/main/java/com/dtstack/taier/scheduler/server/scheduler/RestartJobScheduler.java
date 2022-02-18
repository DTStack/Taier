package com.dtstack.taier.scheduler.server.scheduler;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.OperatorType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.server.scheduler.exec.JudgeJobExecOperator;
import com.dtstack.taier.scheduler.server.scheduler.handler.JudgeNoPassJobHandler;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:22 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class RestartJobScheduler extends OperatorRecordJobScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(RestartJobScheduler.class);

    @Autowired(required = false)
    private List<JudgeJobExecOperator> judgeJobExecOperators;

    @Autowired(required = false)
    private List<JudgeNoPassJobHandler> judgeNoPassJobHandlers;

    @Override
    protected Long getMinSort() {
        return 0L;
    }

    @Override
    protected List<JudgeJobExecOperator> getJudgeJobExecOperator() {
        if (CollectionUtils.isNotEmpty(judgeJobExecOperators)) {
            return judgeJobExecOperators;
        }
        return Lists.newArrayList();
    }

    @Override
    protected List<JudgeNoPassJobHandler> getJudgeNoPassJobHandler() {
        if (CollectionUtils.isNotEmpty(judgeNoPassJobHandlers)) {
            return judgeNoPassJobHandlers;
        }
        return Lists.newArrayList();
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.RESTART;
    }

    @Override
    protected List<ScheduleJob> getScheduleJob(Set<String> jobIds) {
        return scheduleJobService.lambdaQuery().in(ScheduleJob::getJobId, jobIds)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .eq(ScheduleJob::getStatus, TaskStatus.UNSUBMIT.getStatus())
                .eq(ScheduleJob::getPhaseStatus, JobPhaseStatus.CREATE.getCode())
                .list();
    }

    public EScheduleType getScheduleType() {
       return null;
    }

    @Override
    public String getSchedulerName() {
        return getOperatorType().name();
    }
}
