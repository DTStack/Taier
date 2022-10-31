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

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.Restarted;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleConfManager;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taier.scheduler.server.builder.dependency.JobDependency;
import com.dtstack.taier.scheduler.server.builder.dependency.DependencyManager;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.service.ScheduleTaskShadeService;
import com.dtstack.taier.scheduler.utils.JobExecuteOrderUtil;
import com.dtstack.taier.scheduler.utils.JobKeyUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2021/12/30 3:11 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractJobBuilder implements JobBuilder, InitializingBean {

    protected static final String NORMAL_TASK_FLOW_ID = "0";

    protected ExecutorService jobGraphBuildPool;

    @Autowired
    protected ScheduleJobService scheduleJobService;

    @Autowired
    protected DependencyManager dependencyManager;

    @Autowired
    protected ScheduleTaskShadeService scheduleTaskService;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Autowired
    protected ScheduleActionService actionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJobBuilder.class);

    @Override
    public List<ScheduleJobDetails> buildJob(ScheduleTaskShade scheduleTaskShade,
                                             String name,
                                             String triggerDay,
                                             String beginTime,
                                             String endTime,
                                             Long fillId,
                                             JobSortWorker jobSortWorker) throws Exception {

        // 解析周期信息
        ScheduleCorn corn = ScheduleConfManager.parseFromJson(scheduleTaskShade.getScheduleConf());
        ScheduleConf scheduleConf = corn.getScheduleConf();

        // 校验时间是否符合规范，且获得时间范围
        Pair<Date, Date> triggerRange = getTriggerRange(triggerDay, beginTime, endTime);

        // 获得实例命中的实际范围
        Date startDate = getStartData(scheduleConf, triggerRange, scheduleTaskShade.getTaskId());
        Date endDate = getEndDate(scheduleConf, triggerRange, scheduleTaskShade.getTaskId());

        List<ScheduleJobDetails> jobBuilderBeanList = Lists.newArrayList();

        Date next = corn.isMatch(startDate) ? startDate : corn.next(startDate);
        while (next != null) {
            // 如下下一次执行时间已经在结束时间之后，停止生成实例
            if (next.after(endDate)) {
                break;
            }
            ScheduleJobDetails jobBuilderBean = buildJobBuilderBean(scheduleTaskShade, name, fillId, jobSortWorker, corn, scheduleConf, next, NORMAL_TASK_FLOW_ID);

            if (EScheduleJobType.WORK_FLOW.getVal().equals(scheduleTaskShade.getTaskType())) {
                // 该任务是工作流任务 先生成子任务
                List<ScheduleTaskShade> subTasks = scheduleTaskService.lambdaQuery()
                        .eq(ScheduleTaskShade::getFlowId, scheduleTaskShade.getTaskId())
                        .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                        .list();
                List<ScheduleJobDetails> flowBean = Lists.newArrayList();
                ScheduleJob scheduleJob = jobBuilderBean.getScheduleJob();
                for (ScheduleTaskShade subTask : subTasks) {
                    flowBean.add(buildJobBuilderBean(subTask, name, fillId, jobSortWorker, corn, scheduleConf, next, scheduleJob.getJobId()));
                }
                jobBuilderBean.setFlowBean(flowBean);
            }


            jobBuilderBeanList.add(jobBuilderBean);
            next = corn.next(next);
        }
        return jobBuilderBeanList;
    }

    /**
     * 周期实例生成bean方法
     *
     * @param batchTaskShade 任务
     * @param triggerDay     目标天
     * @param sortWorker     排序器
     */
    public List<ScheduleJobDetails> buildJob(ScheduleTaskShade batchTaskShade, String triggerDay, AtomicJobSortWorker sortWorker) throws Exception {
        return buildJob(batchTaskShade, "", triggerDay, "00:00", "23:59", 0L, sortWorker);
    }

    /**
     * 获得实例名称
     *
     * @param scheduleTaskShade 任务
     * @param name              名称前缀
     * @return 名称
     */
    private String getName(ScheduleTaskShade scheduleTaskShade, String name, String cycTime) {
        return getPrefix() + "_" + name + "_" + scheduleTaskShade.getName() + "_" + cycTime;
    }

    /**
     * 构建JobBuilderBean
     *
     * @param scheduleTaskShade 需要被构建的任务
     * @param name              实例名称
     * @param fillId            补数据id
     * @param jobSortWorker     排序器
     * @param corn              周期实例
     * @param scheduleConf      调度配置
     * @param currentData       当前时间
     * @return
     */
    private ScheduleJobDetails buildJobBuilderBean(ScheduleTaskShade scheduleTaskShade,
                                                   String name,
                                                   Long fillId,
                                                   JobSortWorker jobSortWorker,
                                                   ScheduleCorn corn,
                                                   ScheduleConf scheduleConf,
                                                   Date currentData,
                                                   String flowJobId) {
        String triggerTime = DateUtil.getDate(currentData, DateUtil.STANDARD_DATETIME_FORMAT);
        String cycTime = DateUtil.getTimeStrWithoutSymbol(triggerTime);
        String jobKey = JobKeyUtils.generateJobKey(getKeyPreStr(name), scheduleTaskShade.getTaskId(), cycTime);


        // 实例
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setTenantId(scheduleTaskShade.getTenantId());
        scheduleJob.setJobId(actionService.generateUniqueSign());
        scheduleJob.setJobKey(jobKey);
        scheduleJob.setJobName(getName(scheduleTaskShade, name, cycTime));
        scheduleJob.setTaskId(scheduleTaskShade.getTaskId());
        scheduleJob.setCreateUserId(scheduleTaskShade.getCreateUserId());
        scheduleJob.setIsDeleted(Deleted.NORMAL.getStatus());
        scheduleJob.setType(getType());
        scheduleJob.setIsRestart(Restarted.NORMAL.getStatus());
        scheduleJob.setCycTime(cycTime);
        scheduleJob.setDependencyType(scheduleConf.getSelfReliance());
        scheduleJob.setFlowJobId(flowJobId);
        scheduleJob.setPeriodType(scheduleConf.getPeriodType());
        scheduleJob.setStatus(TaskStatus.UNSUBMIT.getStatus());
        scheduleJob.setTaskType(scheduleTaskShade.getTaskType());
        scheduleJob.setFillId(fillId);
        scheduleJob.setMaxRetryNum(scheduleConf.getMaxRetryNum());
        scheduleJob.setVersionId(scheduleTaskShade.getVersionId());
        scheduleJob.setComputeType(scheduleTaskShade.getComputeType());
        scheduleJob.setNextCycTime(DateUtil.getDate(corn.next(currentData), DateUtil.STANDARD_DATETIME_FORMAT));
        scheduleJob.setJobExecuteOrder(JobExecuteOrderUtil.buildJobExecuteOrder(cycTime, jobSortWorker.getSort()));

        // 获得依赖
        List<ScheduleJobJob> jobJobList = Lists.newArrayList();
        JobDependency dependencyHandler = dependencyManager.getDependencyHandler(getKeyPreStr(name), scheduleTaskShade, corn);
        jobJobList.addAll(dependencyHandler.generationJobJobForTask(corn, currentData, jobKey));

        ScheduleJobDetails jobBuilderBean = new ScheduleJobDetails();
        jobBuilderBean.setJobJobList(jobJobList);
        jobBuilderBean.setScheduleJob(scheduleJob);
        return jobBuilderBean;
    }

    private String getKeyPreStr(String name) {
        if (StringUtils.isBlank(name)) {
            return getPrefix();
        }
        return getPrefix() + "_" + name;
    }

    /**
     * 获得实例前缀
     */
    protected abstract String getPrefix();

    /**
     * 获得实例类型
     *
     * @return 实例类型
     */
    protected abstract Integer getType();

    /**
     * 校验时间是否符合规范，且获得时间范围
     *
     * @param triggerDay 目标时间
     * @param beginTime  开始时间
     * @param endTime    结束时间
     * @return 范围时间
     */
    private Pair<Date, Date> getTriggerRange(String triggerDay, String beginTime, String endTime) {
        if (StringUtils.isBlank(triggerDay)) {
            throw new RdosDefineException("triggerDay is not null");
        }

        if (StringUtils.isBlank(beginTime)) {
            beginTime = "00:00:00";
        }

        if (StringUtils.isBlank(endTime)) {
            endTime = "23:59:59";
        }

        String start = triggerDay + " " + beginTime + ":00";
        String end = triggerDay + " " + endTime + ":59";

        Date startDate = DateUtil.parseDate(start, DateUtil.STANDARD_DATETIME_FORMAT, Locale.CHINA);
        Date endDate = DateUtil.parseDate(end, DateUtil.STANDARD_DATETIME_FORMAT, Locale.CHINA);
        if (startDate == null || endDate == null) {
            throw new RdosDefineException("triggerDay or beginTime or endTime invalid");
        }

        return new ImmutablePair<>(startDate, endDate);
    }

    /**
     * 获得周期实例实际命中的开始时间范围
     *
     * @param scheduleConf 周期
     * @param triggerRange 预计时间范围
     * @return 实际开始时间
     */
    private Date getStartData(ScheduleConf scheduleConf, Pair<Date, Date> triggerRange, Long taskId) {
        int beginHour = scheduleConf.getBeginHour() != null ? scheduleConf.getBeginHour() : 0;
        int beginMin = scheduleConf.getBeginMin() != null ? scheduleConf.getBeginMin() : 0;
        String startStr = DateUtil.getDate(scheduleConf.getBeginDate(), DateUtil.DATE_FORMAT) + " " + beginHour + ":" + beginMin + ":00";
        Date beginDate = DateUtil.parseDate(startStr, DateUtil.STANDARD_DATETIME_FORMAT, Locale.CHINA);
        // 这里有两个时间范围 1 任务运行的时间范围 2 计划的时间范围
        // 任务运行开始时间在计划时间之前 任务运行开始时间在计划时间之中
        if (beginDate.before(triggerRange.getLeft()) || beginDate.after(triggerRange.getLeft()) && beginDate.before(triggerRange.getRight())) {
            DateTime dateTime = new DateTime(triggerRange.getLeft());
            return compareAndReplaceMinuteAndHour(dateTime, beginHour, beginMin, true).toDate();
        }

        throw new RdosDefineException("task:" + taskId + " out of time range");
    }

    /**
     * 获得周期实例实际命中的结束时间范围
     *
     * @param scheduleConf 周期
     * @param triggerRange 预计时间范围
     * @return 实际结束时间
     */
    private Date getEndDate(ScheduleConf scheduleConf, Pair<Date, Date> triggerRange, Long taskId) {
        int endHour = scheduleConf.getBeginHour() != null ? scheduleConf.getEndHour() : 23;
        int endMin = scheduleConf.getBeginMin() != null ? scheduleConf.getEndMin() : 59;
        String endDateStr = DateUtil.getDate(scheduleConf.getEndDate(), DateUtil.DATE_FORMAT) + " " + endHour + ":" + endMin + ":00";
        Date endDate = DateUtil.parseDate(endDateStr, DateUtil.STANDARD_DATETIME_FORMAT, Locale.CHINA);
        if ((endDate.after(triggerRange.getLeft()) && endDate.before(triggerRange.getRight())) || endDate.after(triggerRange.getRight())) {
            DateTime dateTime = new DateTime(triggerRange.getRight());
            return compareAndReplaceMinuteAndHour(dateTime, endHour, endMin, false).toDate();
        }

        throw new RdosDefineException("task:" + taskId + " out of time range");
    }

    /**
     * 替换小时和分钟
     *
     * @param dateTime
     * @param minute
     * @param hour
     * @return
     */
    private DateTime replaceMinuteAndHour(DateTime dateTime, Integer minute, Integer hour) {
        if (dateTime != null) {
            if (minute != null && minute >= 0 && minute < 60) {
                dateTime = dateTime.withMinuteOfHour(minute);
            }

            if (hour != null && hour >= 0 && hour < 24) {
                dateTime = dateTime.withHourOfDay(hour);
            }
        }
        return dateTime;
    }

    /**
     * 比较和替换时间
     *
     * @param dateTime         目标周期时间
     * @param scheduleHour     调度周期配置小时
     * @param scheduleMin      调度周期配置分钟
     * @param startTimeCompare 开始时间比较 true , false 结束时间比较
     * @return
     */
    private DateTime compareAndReplaceMinuteAndHour(DateTime dateTime, Integer scheduleHour, Integer scheduleMin, boolean startTimeCompare) {
        Integer hour = dateTime.getHourOfDay();
        Integer min = dateTime.getMinuteOfHour();
        DateTime dateTimeNew = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), scheduleHour, scheduleMin, dateTime.getSecondOfMinute(), DateTimeZone.forID("+08:00"));
        if (startTimeCompare) {
            if (dateTimeNew.compareTo(dateTime) >= 0) {
                hour = scheduleHour;
                min = scheduleMin;
            }

        } else {
            if (dateTimeNew.compareTo(dateTime) < 0) {
                hour = scheduleHour;
                min = scheduleMin;
            }
        }

        return replaceMinuteAndHour(dateTime, min, hour);

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        jobGraphBuildPool = new ThreadPoolExecutor(environmentContext.getGraphBuildPoolCorePoolSize(), environmentContext.getGraphBuildPoolMaximumPoolSize(), 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(environmentContext.getGraphBuildPoolQueueSize()), new CustomThreadFactory(getPrefix()));

    }
}
