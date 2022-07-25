package com.dtstack.taier.scheduler.server.pipeline.operator;


import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.server.pipeline.IPipeline;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;
import java.util.function.Function;

@Component
public class UnnecessaryPreprocessJobPipeline extends IPipeline.AbstractPipeline {

    @Autowired
    public ScheduleJobService scheduleJobService;

    public UnnecessaryPreprocessJobPipeline() {
        super(null);
    }


    public boolean preprocess(ScheduleJob job) {
        if (EScheduleJobType.WORK_FLOW.getVal().equals(job.getTaskType())) {
            return BUILD_TO_UPDATE.andThen(s -> {
                s.setStatus(TaskStatus.SUBMITTING.getStatus());
                scheduleJobService.updateStatusWithExecTime(s);
                return true;
            }).apply(job);
        }

        if (EScheduleJobType.VIRTUAL.getType().equals(job.getTaskType())) {
            //虚节点写入开始时间和结束时间
            return BUILD_TO_UPDATE.andThen(s -> {
                s.setExecTime(0L);
                s.setExecEndTime(new Timestamp(System.currentTimeMillis()));
                s.setStatus(TaskStatus.FINISHED.getStatus());
                scheduleJobService.updateStatusWithExecTime(s);
                return true;
            }).apply(job);
        }

        return false;
    }

    private static final Function<ScheduleJob, ScheduleJob> BUILD_TO_UPDATE = s -> {
        ScheduleJob updateJob = new ScheduleJob();
        updateJob.setJobId(s.getJobId());
        updateJob.setExecStartTime(new Timestamp(System.currentTimeMillis()));
        updateJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        return updateJob;
    };


    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
        ScheduleJob job = (ScheduleJob) pipelineParam.get(AbstractPipeline.scheduleJobKey);
        boolean preprocess = preprocess(job);
        LOGGER.info("process {} result {}", job.getJobId(), preprocess);
    }

    public boolean isMatch(Integer taskType) {
        return EScheduleJobType.WORK_FLOW.getVal().equals(taskType) || EScheduleJobType.VIRTUAL.getType().equals(taskType);
    }
}
