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

package com.dtstack.taier.scheduler.server.builder;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.ForceCancelFlag;
import com.dtstack.taier.common.enums.OperatorType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.scheduler.enums.FillJobTypeEnum;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobOperatorRecordService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 7:11 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class FillDataJobBuilder extends AbstractJobBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(FillDataJobBuilder.class);

    private static final String FILL_DATA_TYPE = "fillData";

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;


    /**
     * 创建补数据实例
     *
     * @param all       all list 所有节点
     * @param run       run list 可运行节点
     * @param fillId    补数据id
     * @param fillName  补数据名称
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param startDay  每天时间范围 开始范围
     * @param endDay    每天时间范围 结束范围
     * @throws Exception
     */
    public void createFillJob(Set<Long> all, Set<Long> run, Long fillId, String fillName, String beginTime, String endTime,
                              String startDay, String endDay) throws Exception {
        Date startDate = DateUtil.parseDate(startDay, DateUtil.DATE_FORMAT, Locale.CHINA);
        Date endDate = DateUtil.parseDate(endDay, DateUtil.DATE_FORMAT, Locale.CHINA);

        DateTime startTime = new DateTime(startDate);
        DateTime finishTime = new DateTime(endDate);
        while (startTime.getMillis() <= finishTime.getMillis()) {
            String triggerDay = startTime.toString(DateUtil.DATE_FORMAT);
            buildFillDataJobGraph(fillName, fillId, all, run, triggerDay, beginTime, endTime);
            startTime = startTime.plusDays(1);
        }
    }

    /**
     * 创建一天的补数据实例
     *
     * @param fillName   补数据名称
     * @param fillId     补数据id
     * @param all        all list 所有节点
     * @param run        run list 可运行节点
     * @param triggerDay 具体目标天
     * @param beginTime  每天时间范围 开始范围
     * @param endTime    每天时间范围 结束范围
     * @throws Exception
     */
    public void buildFillDataJobGraph(String fillName, Long fillId, Set<Long> all, Set<Long> run, String triggerDay,
                                      String beginTime, String endTime) throws Exception {
        List<Long> allList = Lists.newArrayList(all);
        List<List<Long>> partition = Lists.partition(allList, environmentContext.getJobGraphTaskLimitSize());
        AtomicJobSortWorker sortWorker = new AtomicJobSortWorker();
        List<ScheduleJobDetails> saveList = Lists.newArrayList();
        CompletableFuture.allOf(partition.stream()
                .map(taskKey ->
                        CompletableFuture.runAsync(() ->
                                        fillTaskPartition(fillName, fillId, run, triggerDay, beginTime, endTime, allList, sortWorker, saveList, taskKey),
                                jobGraphBuildPool))
                .toArray(CompletableFuture[]::new)).thenAccept(a -> savaFillJob(saveList)).join();
    }

    private void fillTaskPartition(String fillName, Long fillId, Set<Long> run, String triggerDay, String beginTime, String endTime, List<Long> allList, AtomicJobSortWorker sortWorker, List<ScheduleJobDetails> saveList, List<Long> taskKey) {
        for (Long taskId : taskKey) {
            try {
                ScheduleTaskShade scheduleTaskShade = scheduleTaskService
                        .lambdaQuery()
                        .eq(ScheduleTaskShade::getTaskId, taskId)
                        .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                        .one();

                if (scheduleTaskShade != null) {
                    List<ScheduleJobDetails> jobBuilderBeanList = Lists.newArrayList();
                    // 非工作流任务子任务
                    if (scheduleTaskShade.getFlowId() == 0) {
                        // 生成补数据实例
                        jobBuilderBeanList = RetryUtil.executeWithRetry(() -> buildJob(scheduleTaskShade, fillName, triggerDay, beginTime, endTime, fillId, sortWorker),
                                environmentContext.getBuildJobErrorRetry(), 200, false);
                    } else {
                        Long flowId = scheduleTaskShade.getFlowId();
                        if (!allList.contains(flowId)) {
                            // 生成周期实例
                            jobBuilderBeanList = RetryUtil.executeWithRetry(() -> buildJob(scheduleTaskShade, fillName, triggerDay, beginTime, endTime, fillId, sortWorker),
                                    environmentContext.getBuildJobErrorRetry(), 200, false);
                        }
                    }

                    for (ScheduleJobDetails jobBuilderBean : jobBuilderBeanList) {
                        addMap(run, saveList, taskId, jobBuilderBean);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("taskKey : {} error:", taskId, e);
            }
        }
    }

    /**
     * @param run            run list 可运行节点
     * @param saveList       生成实例集合
     * @param taskId         任务id
     * @param jobBuilderBean 构建出来的实际
     */
    private void addMap(Set<Long> run, List<ScheduleJobDetails> saveList, Long taskId, ScheduleJobDetails jobBuilderBean) {
        ScheduleJob scheduleJob = jobBuilderBean.getScheduleJob();
        if (run.contains(taskId)) {
            scheduleJob.setFillType(FillJobTypeEnum.RUN_JOB.getType());
        } else {
            scheduleJob.setFillType(FillJobTypeEnum.MIDDLE_JOB.getType());
        }

        saveList.add(jobBuilderBean);
        if (CollectionUtils.isNotEmpty(jobBuilderBean.getFlowBean())) {
            saveList.addAll(jobBuilderBean.getFlowBean().stream().peek(flowBean -> flowBean.getScheduleJob().setFillType(FillJobTypeEnum.RUN_JOB.getType())).collect(Collectors.toList()));
        }
    }

    /**
     * 持久化时间
     *
     * @param allJobList 所有集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void savaFillJob(List<ScheduleJobDetails> allJobList) {
        scheduleJobService.insertJobList(allJobList, EScheduleType.FILL_DATA.getType());
        Set<ScheduleJobOperatorRecord> operatorJobIds = allJobList
                .stream()
                .map(jobBuilderBean -> {
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(jobBuilderBean.getScheduleJob().getJobId());
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    record.setOperatorType(OperatorType.FILL_DATA.getType());
                    record.setNodeAddress(jobBuilderBean.getScheduleJob().getNodeAddress());
                    return record;
                })
                .collect(Collectors.toSet());

        scheduleJobOperatorRecordService.insertBatch(operatorJobIds);
    }

    @Override
    protected String getPrefix() {
        return FILL_DATA_TYPE;
    }

    @Override
    protected Integer getType() {
        return EScheduleType.FILL_DATA.getType();
    }

}
