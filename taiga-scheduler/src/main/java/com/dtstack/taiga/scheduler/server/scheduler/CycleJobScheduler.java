package com.dtstack.taiga.scheduler.server.scheduler;

import com.dtstack.taiga.common.enums.EScheduleType;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.ScheduleJobJob;
import com.dtstack.taiga.scheduler.enums.JobPhaseStatus;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;
import com.dtstack.taiga.scheduler.server.scheduler.exec.JudgeJobExecOperator;
import com.dtstack.taiga.scheduler.server.scheduler.handler.JudgeNoPassJobHandler;
import com.dtstack.taiga.scheduler.service.ScheduleJobJobService;
import com.dtstack.taiga.scheduler.service.ScheduleJobService;
import com.dtstack.taiga.scheduler.utils.JobExecuteOrderUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/16 4:26 PM
 * @Email: dazhi@dtstack.com
 * @Description: 周期调度
 */
@Component
public class CycleJobScheduler extends AbstractJobSummitScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(FillDataJobScheduler.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired(required = false)
    private List<JudgeJobExecOperator> judgeJobExecOperators;

    @Autowired(required = false)
    private List<JudgeNoPassJobHandler> judgeNoPassJobHandlers;

    private final String CYC_TIME_FORMAT = "yyyyMMddHHmmss";
    private final String DATA_YMD = "yyyyMMdd";


    @Override
    protected List<ScheduleJobDetails> listExecJob(Long startSort, String nodeAddress, Boolean isEq) {
        List<ScheduleJob> scheduleJobList = scheduleJobService.listCycleJob(startSort, nodeAddress, getScheduleType().getType(), isEq, JobPhaseStatus.CREATE.getCode());

        List<String> jobKeys = scheduleJobList.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
        List<ScheduleJobJob> scheduleJobJobList = scheduleJobJobService.listByJobKeys(jobKeys);
        Map<String, List<ScheduleJobJob>> jobJobMap = scheduleJobJobList.stream().collect(Collectors.groupingBy(ScheduleJobJob::getJobKey));
        List<ScheduleJobDetails> scheduleJobDetailsList = new ArrayList<>(scheduleJobList.size());

        for (ScheduleJob scheduleJob : scheduleJobList) {
            ScheduleJobDetails scheduleJobDetails = new ScheduleJobDetails();
            scheduleJobDetails.setScheduleJob(scheduleJob);
            scheduleJobDetails.setJobJobList(jobJobMap.get(scheduleJob.getJobKey()));
            scheduleJobDetailsList.add(scheduleJobDetails);
        }

        return scheduleJobDetailsList;
    }

    @Override
    protected Long getMinSort() {
        String triggerTime = new DateTime().toString(DATA_YMD);
        triggerTime += triggerTime + "000000";
        return JobExecuteOrderUtil.buildJobExecuteOrder(triggerTime,0L);
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
    public EScheduleType getScheduleType() {
        return EScheduleType.NORMAL_SCHEDULE;
    }
}
