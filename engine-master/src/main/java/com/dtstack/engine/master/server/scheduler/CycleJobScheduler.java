package com.dtstack.engine.master.server.scheduler;

import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.server.ScheduleJobDetails;
import com.dtstack.engine.master.server.scheduler.exec.JudgeJobExecOperator;
import com.dtstack.engine.master.server.scheduler.handler.JudgeNoPassJobHandler;
import com.dtstack.engine.master.service.ScheduleJobService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Autowired(required = false)
    private List<JudgeJobExecOperator> judgeJobExecOperators;

    @Autowired(required = false)
    private List<JudgeNoPassJobHandler> judgeNoPassJobHandlers;


    @Override
    protected List<ScheduleJobDetails> listExecJob(Long startSort, String nodeAddress, Boolean isEq) {
        return null;
    }

    @Override
    protected Long getMinSort() {
        return null;
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
