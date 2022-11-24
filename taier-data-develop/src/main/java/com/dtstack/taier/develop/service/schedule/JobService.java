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

package com.dtstack.taier.develop.service.schedule;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.ScheduleFillDataJob;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.User;
import com.dtstack.taier.dao.domain.po.CountFillDataJobStatusPO;
import com.dtstack.taier.dao.domain.po.JobsStatusStatisticsPO;
import com.dtstack.taier.dao.domain.po.StatusCountPO;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.event.FillStatusUpdateFinishEvent;
import com.dtstack.taier.develop.mapstruct.fill.FillDataJobMapstructTransfer;
import com.dtstack.taier.develop.mapstruct.job.JobMapstructTransfer;
import com.dtstack.taier.develop.service.user.UserService;
import com.dtstack.taier.develop.vo.fill.FillDataJobVO;
import com.dtstack.taier.develop.vo.fill.ReturnFillDataJobListVO;
import com.dtstack.taier.develop.vo.fill.ReturnFillDataListVO;
import com.dtstack.taier.develop.vo.schedule.ReturnDisplayPeriodVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobListVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobStatusStatisticsVO;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.dto.fill.QueryFillDataJobListDTO;
import com.dtstack.taier.scheduler.dto.fill.QueryFillDataListDTO;
import com.dtstack.taier.scheduler.dto.fill.ScheduleFillDataInfoDTO;
import com.dtstack.taier.scheduler.dto.fill.ScheduleFillJobParticipateDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobListDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobStatusStatisticsDTO;
import com.dtstack.taier.scheduler.enums.FillDataTypeEnum;
import com.dtstack.taier.scheduler.enums.FillGeneratStatusEnum;
import com.dtstack.taier.scheduler.enums.FillJobTypeEnum;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.server.action.fill.FillDataRunnable;
import com.dtstack.taier.scheduler.server.action.fill.FillDataThreadPoolExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:26 PM
 */
@Service
public class JobService extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {

    private static Logger LOGGER = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FillDataService fillDataJobService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FillDataThreadPoolExecutor fillDataThreadPoolExecutor;

    @Autowired
    private FillStatusUpdateFinishEvent fillStatusUpdateFinishEvent;
    /**
     * 查询周期实例列表
     *
     * @param dto 查询条件
     * @return
     */
    public PageResult<List<ReturnJobListVO>> queryJobs(QueryJobListDTO dto) {
        int totalCount = 0;
        List<ReturnJobListVO> returnJobListVOS = Lists.newArrayList();
        // 关联任务
        List<Long> taskIds = null;
        if (StringUtils.isNotBlank(dto.getTaskName()) || dto.getOperatorId() != null) {
            List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(dto.getTaskName(), null, dto.getOperatorId());
            if (CollectionUtils.isEmpty(scheduleTaskShadeList)) {
                return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), totalCount, returnJobListVOS);
            } else {
                taskIds = scheduleTaskShadeList.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            }
        }

        // 查询实例表
        Page<ScheduleJob> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        page = this.lambdaQuery()
                .eq(ScheduleJob::getFlowJobId, 0)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .in(ScheduleJob::getFillType, Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(), FillJobTypeEnum.RUN_JOB.getType()))
                .eq(ScheduleJob::getType, EScheduleType.NORMAL_SCHEDULE.getType())
                .eq(ScheduleJob::getTenantId, dto.getTenantId())
                .in(CollectionUtils.isNotEmpty(taskIds), ScheduleJob::getTaskId, taskIds)
                .between((dto.getCycStartDay() != null && dto.getCycEndDay() != null), ScheduleJob::getCycTime, getCycTime(dto.getCycStartDay()), getCycTime(dto.getCycEndDay()))
                .in(CollectionUtils.isNotEmpty(dto.getTaskTypeList()), ScheduleJob::getTaskType, dto.getTaskTypeList())
                .in(CollectionUtils.isNotEmpty(dto.getJobStatusList()), ScheduleJob::getStatus, transform(dto.getJobStatusList()))
                .in(CollectionUtils.isNotEmpty(dto.getTaskPeriodTypeList()), ScheduleJob::getPeriodType, dto.getTaskPeriodTypeList())
                .orderBy(StringUtils.isNotBlank(dto.getCycSort()), isAsc(dto.getCycSort()), ScheduleJob::getCycTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecStartSort()), isAsc(dto.getExecStartSort()), ScheduleJob::getExecStartTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecEndSort()), isAsc(dto.getExecEndSort()), ScheduleJob::getExecEndTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecTimeSort()), isAsc(dto.getExecTimeSort()), ScheduleJob::getExecTime)
                .orderBy(StringUtils.isNotBlank(dto.getRetryNumSort()), isAsc(dto.getRetryNumSort()), ScheduleJob::getRetryNum)
                .orderBy(Boolean.TRUE, Boolean.FALSE, ScheduleJob::getGmtCreate)
                .page(page);

        // 处理查询出来的结果集
        List<ScheduleJob> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            // 查询实例对应的任务
            buildReturnJobListVO(returnJobListVOS, records);
        }

        return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), page.getTotal(), (int) page.getPages(), returnJobListVOS);
    }

    /**
     * 统计周期实例状态
     *
     * @param dto
     * @return
     */
    public List<ReturnJobStatusStatisticsVO> queryJobsStatusStatistics(QueryJobStatusStatisticsDTO dto) {
        // 关联任务
        List<Long> taskIdList = null;
        if (StringUtils.isNotBlank(dto.getTaskName()) || dto.getOperatorId() != null) {
            List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(dto.getTaskName(), null, dto.getOperatorId());
            if (CollectionUtils.isEmpty(scheduleTaskShadeList)) {
                return Lists.newArrayList();
            } else {
                taskIdList = scheduleTaskShadeList.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            }
        }

        // 查询db统计数据
        JobsStatusStatisticsPO jobsStatusStatistics = JobMapstructTransfer.INSTANCE.queryJobStatusStatisticsDTOToJobsStatusStatistics(dto);
        jobsStatusStatistics.setCycStartTime(getCycTime(dto.getCycStartDay()));
        jobsStatusStatistics.setCycEndTime(getCycTime(dto.getCycEndDay()));
        jobsStatusStatistics.setFillTypeList(Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(), FillJobTypeEnum.RUN_JOB.getType()));
        jobsStatusStatistics.setTaskIdList(taskIdList);

        List<StatusCountPO> statusCountList = this.baseMapper.queryJobsStatusStatistics(jobsStatusStatistics);
        // 封装结果集
        return mergeStatusAndShow(statusCountList);
    }

    /**
     * @param jobId
     * @return
     */
    public List<ReturnJobListVO> queryFlowWorkSubJobs(String jobId) {
        List<ScheduleJob> jobs = this.lambdaQuery().eq(ScheduleJob::getFlowJobId, jobId).eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus()).list();
        List<ReturnJobListVO> returnJobListVOS = Lists.newArrayList();
        buildReturnJobListVO(returnJobListVOS, jobs);
        return returnJobListVOS;
    }

    /**
     * 查询上一个周期或者下一个周期实例
     *
     * @param isAfter 是否是上个周期
     * @param jobId   实例id
     * @param limit   查询个数
     */
    public List<ReturnDisplayPeriodVO> displayPeriods(Boolean isAfter, String jobId, Integer limit) {
        ScheduleJob scheduleJob = this.lambdaQuery()
                .eq(ScheduleJob::getJobId, jobId)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .one();
        if (scheduleJob == null) {
            throw new TaierDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        //需要根据查询的job的类型来
        List<ScheduleJob> scheduleJobList = this.baseMapper.listAfterOrBeforeJobs(scheduleJob.getTaskId(), isAfter, scheduleJob.getCycTime(), scheduleJob.getType());
        scheduleJobList.sort((o1, o2) -> {
            if (!NumberUtils.isNumber(o1.getCycTime())) {
                return 1;
            }

            if (!NumberUtils.isNumber(o2.getCycTime())) {
                return -1;
            }
            return Long.compare(Long.parseLong(o2.getCycTime()), Long.parseLong(o1.getCycTime()));
        });

        if (scheduleJobList.size() > limit) {
            scheduleJobList = scheduleJobList.subList(0, limit);
        }

        List<ReturnDisplayPeriodVO> vos = new ArrayList<>(scheduleJobList.size());
        scheduleJobList.forEach(nextScheduleJob -> {
            ReturnDisplayPeriodVO vo = new ReturnDisplayPeriodVO();
            vo.setJobId(nextScheduleJob.getJobId());
            vo.setCycTime(DateUtil.addTimeSplit(nextScheduleJob.getCycTime()));
            vo.setStatus(nextScheduleJob.getStatus());
            vos.add(vo);
        });
        return vos;
    }

    /**
     * 生成补数据
     *
     * @param dto 补数据需要的参数
     * @return 补数据标识
     */
    public Long fillData(ScheduleFillJobParticipateDTO dto) {
        // 必要的校验
        checkFillData(dto);

        // 生成schedule_fill_data_job数据
        ScheduleFillDataJob fillDataJob = buildScheduleFillDataJob(dto);
        fillDataJobService.save(fillDataJob);

        // 提交补数据任务
        ScheduleFillDataInfoDTO fillDataInfo = dto.getFillDataInfo();
        fillDataThreadPoolExecutor.submit(new FillDataRunnable(fillDataJob.getId(), dto, fillDataInfo, fillStatusUpdateFinishEvent, applicationContext));
        return fillDataJob.getId();
    }

    /**
     * 补数据列表
     *
     * @param dto 查询列表条件
     * @return 补数据列表数据
     */
    public PageResult<List<ReturnFillDataListVO>> fillDataList(QueryFillDataListDTO dto) {
        Page<ScheduleFillDataJob> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        // 查询补数据列表
        page = fillDataJobService.lambdaQuery()
                .like(StringUtils.isNotBlank(dto.getJobName()), ScheduleFillDataJob::getJobName, dto.getJobName())
                .eq(dto.getOperatorId() != null, ScheduleFillDataJob::getCreateUserId, dto.getOperatorId())
                .eq(StringUtils.isNotBlank(dto.getRunDay()), ScheduleFillDataJob::getRunDay, dto.getRunDay())
                .eq(ScheduleFillDataJob::getTenantId, dto.getTenantId())
                .orderBy(true, false, ScheduleFillDataJob::getGmtCreate)
                .page(page);

        List<ScheduleFillDataJob> records = page.getRecords();
        List<ReturnFillDataListVO> fillDataReturnListVOs = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(records)) {
            // 封装结果集
            Map<Long, ScheduleFillDataJob> fillDataJobMap = records.stream().collect(Collectors.toMap(ScheduleFillDataJob::getId, g -> (g)));
            List<Long> userIds = records.stream().map(ScheduleFillDataJob::getCreateUserId).collect(Collectors.toList());

            Map<Long, User> userMap = userService.getUserMap(userIds);
            List<CountFillDataJobStatusPO> statistics = this.baseMapper.countByFillIdGetAllStatus(fillDataJobMap.keySet());
            Map<Long, List<CountFillDataJobStatusPO>> statisticsGroup = statistics.stream().collect(Collectors.groupingBy(CountFillDataJobStatusPO::getFillId));

            for (ScheduleFillDataJob scheduleFillDataJob : records) {
                ReturnFillDataListVO fillDataReturnListVO = FillDataJobMapstructTransfer.INSTANCE.fillDataListDTOToFillDataReturnListVO(scheduleFillDataJob);
                User user = userMap.get(scheduleFillDataJob.getCreateUserId());
                if (user != null) {
                    fillDataReturnListVO.setOperatorName(user.getUserName());
                }
                fillDataReturnListVO.setGmtCreate(DateUtil.getDate(scheduleFillDataJob.getGmtCreate(), DateUtil.STANDARD_DATETIME_FORMAT));
                // 计算补数据执行进度
                List<CountFillDataJobStatusPO> countFillDataJobStatusPOS = statisticsGroup.get(fillDataReturnListVO.getId());
                if (CollectionUtils.isNotEmpty(countFillDataJobStatusPOS)) {
                    Map<Integer, IntSummaryStatistics> statusCount = countFillDataJobStatusPOS
                            .stream()
                            .collect(Collectors.groupingBy(
                                    countFillDataJobStatusPO -> TaskStatus.getShowStatus(countFillDataJobStatusPO.getStatus()),
                                    Collectors.summarizingInt(CountFillDataJobStatusPO::getCount)));

                    calculateStatusCount(fillDataReturnListVO, statusCount);
                }
                fillDataReturnListVOs.add(fillDataReturnListVO);

            }
        }
        return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), page.getTotal(), (int) page.getPages(), fillDataReturnListVOs);
    }

    /**
     * 补数据实例列表
     *
     * @param dto 查询列表条件
     * @return 列表
     */
    public PageResult<ReturnFillDataJobListVO> fillDataJobList(QueryFillDataJobListDTO dto) {
        Integer totalCount = 0;
        ReturnFillDataJobListVO dataJobDetailVO = new ReturnFillDataJobListVO();

        // 查询补数据是否存在，不存在直接返回结果
        ScheduleFillDataJob fillDataJob = fillDataJobService.getById(dto.getFillId());
        if (!checkFillDataJobList(fillDataJob, dataJobDetailVO)) {
            return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), totalCount, dataJobDetailVO);
        }

        dataJobDetailVO.setId(fillDataJob.getId());
        dataJobDetailVO.setFillDataName(fillDataJob.getJobName());


        // 关联任务
        List<Long> taskIds = null;
        if (StringUtils.isNotBlank(dto.getTaskName()) || dto.getOperatorId() != null) {
            List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(dto.getTaskName(), null, dto.getOperatorId());
            if (CollectionUtils.isEmpty(scheduleTaskShadeList)) {
                return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), totalCount, dataJobDetailVO);
            } else {
                taskIds = scheduleTaskShadeList.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            }
        }

        // 查询实例表
        Page<ScheduleJob> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        page = this.lambdaQuery()
                .eq(ScheduleJob::getFlowJobId, 0)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .eq(ScheduleJob::getFillId, dto.getFillId())
                .eq(ScheduleJob::getType, EScheduleType.FILL_DATA.getType())
                .in(ScheduleJob::getFillType, Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(), FillJobTypeEnum.RUN_JOB.getType()))
                .in(CollectionUtils.isNotEmpty(taskIds), ScheduleJob::getTaskId, taskIds)
                .in(CollectionUtils.isNotEmpty(dto.getTaskTypeList()), ScheduleJob::getTaskType, dto.getTaskTypeList())
                .in(CollectionUtils.isNotEmpty(dto.getJobStatusList()), ScheduleJob::getStatus, transform(dto.getJobStatusList()))
                .between((dto.getCycStartDay() != null && dto.getCycEndDay() != null), ScheduleJob::getCycTime, getCycTime(dto.getCycStartDay()), getCycTime(dto.getCycEndDay()))
                .orderBy(StringUtils.isNotBlank(dto.getExecTimeSort()), isAsc(dto.getExecTimeSort()), ScheduleJob::getExecTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecStartSort()), isAsc(dto.getExecStartSort()), ScheduleJob::getExecStartTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecEndSort()), isAsc(dto.getExecEndSort()), ScheduleJob::getExecEndTime)
                .orderBy(StringUtils.isNotBlank(dto.getCycSort()), isAsc(dto.getCycSort()), ScheduleJob::getCycTime)
                .orderBy(StringUtils.isNotBlank(dto.getRetryNumSort()), isAsc(dto.getRetryNumSort()), ScheduleJob::getRetryNum)
                .orderBy(Boolean.TRUE, Boolean.FALSE, ScheduleJob::getGmtCreate)
                .page(page);

        List<ScheduleJob> records = page.getRecords();

        // 封装结果集
        if (CollectionUtils.isNotEmpty(records)) {
            List<FillDataJobVO> fillDataJobVOS = Lists.newArrayList();

            List<Long> taskIdList = records.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
            Map<Long, ScheduleTaskShade> taskShadeMap = taskService.lambdaQuery().in(ScheduleTaskShade::getTaskId, taskIdList).eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus()).list().stream().collect(Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g)));
            Map<Long, User> userMap = userService.listAll().stream().collect(Collectors.toMap(User::getId, g -> (g)));

            records.forEach(record -> {
                FillDataJobVO vo = FillDataJobMapstructTransfer.INSTANCE.scheduleJobToFillDataJobVO(record);
                vo.setStartExecTime(DateUtil.getDate(record.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
                vo.setEndExecTime(DateUtil.getDate(record.getExecEndTime(), DateUtil.STANDARD_DATETIME_FORMAT));
                vo.setCycTime(DateUtil.addTimeSplit(record.getCycTime()));
                vo.setExecTime(getExecTime(record));
                vo.setStatus(TaskStatus.getShowStatus(record.getStatus()));

                ScheduleTaskShade scheduleTaskShade = taskShadeMap.get(record.getTaskId());
                if (scheduleTaskShade != null) {
                    vo.setTaskName(scheduleTaskShade.getName());
                    vo.setOperatorId(scheduleTaskShade.getCreateUserId());
                    vo.setOperatorName(userMap.get(scheduleTaskShade.getCreateUserId()) != null ? userMap.get(scheduleTaskShade.getCreateUserId()).getUserName() : "");
                }
                fillDataJobVOS.add(vo);
            });

            dataJobDetailVO.setFillDataJobVOLists(fillDataJobVOS);
        }
        dataJobDetailVO.setFillGenerateStatus(FillGeneratStatusEnum.FILL_FINISH.getType());
        return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), page.getTotal(), (int) page.getPages(), dataJobDetailVO);
    }

    /**
     * 更具业务时间获取计划时间
     *
     * @param cycTime 计划时间
     * @return 计划时间
     */
    public String getCycTime(Long cycTime) {
        if (cycTime == null) {
            return "";
        }
        return new DateTime(cycTime * 1000).toString(DateUtil.UN_STANDARD_DATETIME_FORMAT);
    }

    /**
     * 页码显示状态和代码运行状态并非一一对应，所以需要一层状态转换
     *
     * @param originalStatus 初始状态
     * @return 转换后状态
     */
    private List<Integer> transform(List<Integer> originalStatus) {
        if (CollectionUtils.isEmpty(originalStatus)) {
            return Lists.newArrayList();
        }
        List<Integer> statues = Lists.newArrayList();
        Map<Integer, List<Integer>> statusMap = TaskStatus.getStatusFailedDetailAndExpire();
        for (Integer status : originalStatus) {
            List<Integer> statusList = statusMap.get(status);
            if (CollectionUtils.isNotEmpty(statusList)) {
                statues.addAll(statusList);
            } else {
                statues.add(status);
            }
        }
        return statues;
    }

    /**
     * 计算补数据执行进度
     *
     * @param fillDataReturnListVO 补数据返回列表vo
     * @param statusCount          查询出来的计数
     */
    private void calculateStatusCount(ReturnFillDataListVO fillDataReturnListVO, Map<Integer, IntSummaryStatistics> statusCount) {
        Long unSubmit = statusCount.get(TaskStatus.UNSUBMIT.getStatus()) == null ? 0L : statusCount.get(TaskStatus.UNSUBMIT.getStatus()).getSum();
        Long running = statusCount.get(TaskStatus.RUNNING.getStatus()) == null ? 0L : statusCount.get(TaskStatus.RUNNING.getStatus()).getSum();
        Long notFound = statusCount.get(TaskStatus.NOTFOUND.getStatus()) == null ? 0L : statusCount.get(TaskStatus.NOTFOUND.getStatus()).getSum();
        Long finished = statusCount.get(TaskStatus.FINISHED.getStatus()) == null ? 0L : statusCount.get(TaskStatus.FINISHED.getStatus()).getSum();
        Long failed = statusCount.get(TaskStatus.FAILED.getStatus()) == null ? 0L : statusCount.get(TaskStatus.FAILED.getStatus()).getSum();
        Long waitEngine = statusCount.get(TaskStatus.WAITENGINE.getStatus()) == null ? 0L : statusCount.get(TaskStatus.WAITENGINE.getStatus()).getSum();
        Long submitting = statusCount.get(TaskStatus.SUBMITTING.getStatus()) == null ? 0L : statusCount.get(TaskStatus.SUBMITTING.getStatus()).getSum();
        Long canceled = statusCount.get(TaskStatus.CANCELED.getStatus()) == null ? 0L : statusCount.get(TaskStatus.CANCELED.getStatus()).getSum();
        Long frozen = statusCount.get(TaskStatus.FROZEN.getStatus()) == null ? 0L : statusCount.get(TaskStatus.FROZEN.getStatus()).getSum();

        fillDataReturnListVO.setFinishedJobSum(finished);
        fillDataReturnListVO.setAllJobSum(unSubmit + running + notFound + finished + failed + waitEngine + submitting + canceled + frozen);
        fillDataReturnListVO.setDoneJobSum(failed + canceled + frozen + finished);
    }

    /**
     * 封装ScheduleFillDataJob
     *
     * @param scheduleFillJobParticipateDTO 原数据
     * @return ScheduleFillDataJob
     */
    private ScheduleFillDataJob buildScheduleFillDataJob(ScheduleFillJobParticipateDTO scheduleFillJobParticipateDTO) {
        ScheduleFillDataJob scheduleFillDataJob = new ScheduleFillDataJob();

        scheduleFillDataJob.setFillDataInfo(JSON.toJSONString(scheduleFillJobParticipateDTO.getFillDataInfo()));
        scheduleFillDataJob.setFillGenerateStatus(FillGeneratStatusEnum.REALLY_GENERATED.getType());
        scheduleFillDataJob.setFromDay(scheduleFillJobParticipateDTO.getStartDay());
        scheduleFillDataJob.setToDay(scheduleFillJobParticipateDTO.getEndDay());
        scheduleFillDataJob.setJobName(scheduleFillJobParticipateDTO.getFillName());
        scheduleFillDataJob.setTenantId(scheduleFillJobParticipateDTO.getTenantId());
        scheduleFillDataJob.setCreateUserId(scheduleFillJobParticipateDTO.getUserId());
        scheduleFillDataJob.setRunDay(DateTime.now().toString(DateUtil.DATE_FORMAT));
        scheduleFillDataJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleFillDataJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        return scheduleFillDataJob;
    }

    /**
     * 校验补数据任务参数
     *
     * @param scheduleFillJobParticipateDTO 补数据参数
     **/
    private void checkFillData(ScheduleFillJobParticipateDTO scheduleFillJobParticipateDTO) {
        String fillName = scheduleFillJobParticipateDTO.getFillName();
        String startDay = scheduleFillJobParticipateDTO.getStartDay();
        String endDay = scheduleFillJobParticipateDTO.getEndDay();
        ScheduleFillDataInfoDTO fillDataInfo = scheduleFillJobParticipateDTO.getFillDataInfo();
        DateTime startTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(startDay, DateUtil.DATE_FORMAT));
        DateTime endTime = new DateTime(DateUtil.getDateMilliSecondTOFormat(endDay, DateUtil.DATE_FORMAT));

        if (fillName == null) {
            throw new TaierDefineException("(fillName 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        //补数据的名称中-作为分割名称和后缀信息的分隔符,故不允许使用
        if (fillName.contains("-")) {
            throw new TaierDefineException("(fillName 参数不能包含字符 '-')", ErrorCode.INVALID_PARAMETERS);
        }

        if (!startTime.isBefore(DateTime.now())) {
            throw new TaierDefineException("(补数据业务日期开始时间不能晚于结束时间)", ErrorCode.INVALID_PARAMETERS);
        }

        if (fillDataInfo == null) {
            throw new TaierDefineException("fillDataInfo is not null", ErrorCode.INVALID_PARAMETERS);
        }

        if (FillDataTypeEnum.PROJECT.getType().equals(fillDataInfo.getFillDataType()) && (endTime.getMillis() - startTime.getMillis()) / (1000 * 3600 * 24) > 7) {
            throw new TaierDefineException("The difference between the start and end days cannot exceed 7 days", ErrorCode.INVALID_PARAMETERS);
        }

        if (fillDataJobService.checkExistsName(fillName)) {
            throw new TaierDefineException("补数据任务名称已存在", ErrorCode.NAME_ALREADY_EXIST);
        }
    }

    /**
     * 校验补数据列表
     *
     * @param fillDataJob     补数据信息
     * @param dataJobDetailVO 返回值结果
     */
    private Boolean checkFillDataJobList(ScheduleFillDataJob fillDataJob, ReturnFillDataJobListVO dataJobDetailVO) {
        if (fillDataJob == null) {
            dataJobDetailVO.setFillGenerateStatus(FillGeneratStatusEnum.FILL_FAIL.getType());
            return Boolean.FALSE;
        }

        if (FillGeneratStatusEnum.REALLY_GENERATED.getType().equals(fillDataJob.getFillGenerateStatus())) {
            dataJobDetailVO.setFillGenerateStatus(FillGeneratStatusEnum.REALLY_GENERATED.getType());
            return Boolean.FALSE;
        }

        if (FillGeneratStatusEnum.FILL_FAIL.getType().equals(fillDataJob.getFillGenerateStatus())) {
            dataJobDetailVO.setFillGenerateStatus(FillGeneratStatusEnum.FILL_FAIL.getType());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 判断排序规则
     *
     * @param sort 配置字段
     * @return 是否正序
     */
    private boolean isAsc(String sort) {
        return "asc".equals(sort);
    }

    /**
     * 获得执行时间
     *
     * @param scheduleJob
     * @return
     */
    private String getExecTime(ScheduleJob scheduleJob) {
        Long execTime = scheduleJob.getExecTime();

        if (execTime != null) {
            return DateUtil.getTimeDifference(execTime * 1000);
        }

        Timestamp execStartTime = scheduleJob.getExecStartTime();
        Timestamp execEndTime = scheduleJob.getExecEndTime();

        if (execStartTime == null) {
            return "";
        }

        if (execEndTime == null) {
            return DateUtil.getTimeDifference(System.currentTimeMillis() - execStartTime.getTime());
        }
        return "";
    }

    /**
     * 统计job状态数
     *
     * @param statusCountList 从db中查询出的status数据
     * @return
     */
    private List<ReturnJobStatusStatisticsVO> mergeStatusAndShow(List<StatusCountPO> statusCountList) {
        Map<String, ReturnJobStatusStatisticsVO> returnJobStatusStatisticsVOList = Maps.newHashMap();
        long totalNum = 0;
        Map<Integer, List<Integer>> statusMap = TaskStatus.getStatusFailedDetail();
        for (Map.Entry<Integer, List<Integer>> entry : statusMap.entrySet()) {
            ReturnJobStatusStatisticsVO vo = new ReturnJobStatusStatisticsVO();
            vo.setCount(0L);
            String statusName = TaskStatus.getCode(entry.getKey());
            List<Integer> statuses = entry.getValue();
            vo.setStatusKey(statusName);
            long num = 0;
            for (StatusCountPO statusCount : statusCountList) {
                if (statuses.contains(statusCount.getStatus())) {
                    num += statusCount.getCount();
                }
            }

            if (!returnJobStatusStatisticsVOList.containsKey(statusName)) {
                vo.setCount(num);
                returnJobStatusStatisticsVOList.put(statusName, vo);
            } else {
                //上一个该状态的数量
                vo.setCount(vo.getCount() + num);
                returnJobStatusStatisticsVOList.put(statusName, vo);
            }
            totalNum += num;
        }
        ReturnJobStatusStatisticsVO vo = new ReturnJobStatusStatisticsVO();
        vo.setCount(totalNum);
        vo.setStatusKey("ALL");
        Collection<ReturnJobStatusStatisticsVO> collection = returnJobStatusStatisticsVOList.values();
        List<ReturnJobStatusStatisticsVO> returnJobStatusStatisticsVOS = Lists.newArrayList(collection);
        returnJobStatusStatisticsVOS.add(vo);
        return returnJobStatusStatisticsVOS;
    }

    /**
     * 构建ReturnJobListVO
     *
     * @param returnJobListVOS 结果集
     * @param records          记录数
     */
    private void buildReturnJobListVO(List<ReturnJobListVO> returnJobListVOS, List<ScheduleJob> records) {
        List<Long> taskIdList = records.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
        List<ScheduleTaskShade> taskShadeList = taskService.lambdaQuery().in(ScheduleTaskShade::getTaskId, taskIdList).list();
        Map<Long, ScheduleTaskShade> taskShadeMap = taskShadeList.stream().collect(Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g)));
        Map<Long, User> userMap = userService.listAll().stream().collect(Collectors.toMap(User::getId, g -> (g)));

        // 封装返回值
        for (ScheduleJob scheduleJob : records) {
            ReturnJobListVO returnJobListVO = JobMapstructTransfer.INSTANCE.scheduleJobToReturnJobListVO(scheduleJob);
            returnJobListVO.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
            returnJobListVO.setStartExecTime(DateUtil.getDate(scheduleJob.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            returnJobListVO.setEndExecTime(DateUtil.getDate(scheduleJob.getExecEndTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            returnJobListVO.setExecTime(getExecTime(scheduleJob));
            returnJobListVO.setStatus(TaskStatus.getShowStatus(scheduleJob.getStatus()));
            ScheduleTaskShade scheduleTaskShade = taskShadeMap.get(returnJobListVO.getTaskId());
            if (scheduleTaskShade != null) {
                returnJobListVO.setTaskName(scheduleTaskShade.getName());
                returnJobListVO.setOperatorId(scheduleTaskShade.getCreateUserId());
                returnJobListVO.setIsDeleted(scheduleTaskShade.getIsDeleted());
                returnJobListVO.setOperatorName(userMap.get(scheduleTaskShade.getCreateUserId()) != null ? userMap.get(scheduleTaskShade.getCreateUserId()).getUserName() : "");
            }
            returnJobListVOS.add(returnJobListVO);
        }
    }

    public ScheduleJob getScheduleJob(String jobId) {
        return getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleJob.class).eq(ScheduleJob::getJobId, jobId));
    }


    public ScheduleJob getScheduleJob(Long taskId, Integer computeType) {
        return getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleJob.class).eq(ScheduleJob::getTaskId, taskId).eq(ScheduleJob::getComputeType, computeType));
    }


    public boolean resetTaskStatus(String jobId, Integer currStatus, String localAddress) {
        //check job status can reset
        if (!TaskStatus.canReset(currStatus)) {
            LOGGER.error("jobId:{} can not update status current status is :{} ", jobId, currStatus);
            throw new TaierDefineException(String.format(" taskId(%s) can't reset status, current status(%d)", jobId, currStatus));
        }
        ScheduleJob updateScheduleJob = new ScheduleJob();
        updateScheduleJob.setApplicationId("");
        updateScheduleJob.setJobId(jobId);
        updateScheduleJob.setEngineJobId("");
        updateScheduleJob.setExecTime(0L);
        updateScheduleJob.setExecStartTime(null);
        updateScheduleJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        updateScheduleJob.setRetryNum(0);
        updateScheduleJob.setNodeAddress(localAddress);
        updateScheduleJob.setStatus(TaskStatus.UNSUBMIT.getStatus());
        updateScheduleJob.setPhaseStatus(JobPhaseStatus.CREATE.getCode());
        getBaseMapper().update(updateScheduleJob, Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getJobId, jobId));
        LOGGER.info("jobId:{} update job status:{}.", jobId, TaskStatus.UNSUBMIT.getStatus());
        return true;
    }

    /**
     * 获取工作流节点的父节点和子节点关联信息
     *
     * @param jobId
     * @return List<ReturnJobListVO>
     */
    public List<ReturnJobListVO> getRelatedJobs(String jobId) {
        ArrayList<ReturnJobListVO> queryRelatedJobsVOS = new ArrayList<>();
        List<ScheduleJob> scheduleJobs = this.baseMapper.selectList(Wrappers.lambdaQuery(ScheduleJob.class)
                .eq(ScheduleJob::getFlowJobId, jobId)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus()));
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return queryRelatedJobsVOS;
        }
        List<Long> taskIds = scheduleJobs.stream()
                .map(ScheduleJob::getTaskId)
                .collect(Collectors.toList());
        List<ScheduleTaskShade> scheduleTaskShades = taskService.getBaseMapper().selectList(Wrappers.lambdaQuery(ScheduleTaskShade.class)
                .in(ScheduleTaskShade::getTaskId, taskIds)
                .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus()));
        Map<Long, ScheduleTaskShade> taskShadeMap = scheduleTaskShades.stream()
                .collect(Collectors.groupingBy(ScheduleTaskShade::getTaskId, Collectors.collectingAndThen(Collectors.toList(),
                        list -> list.get(0))));
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ReturnJobListVO returnJobListVO = new ReturnJobListVO();
            BeanUtils.copyProperties(scheduleJob, returnJobListVO);
            returnJobListVO.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
            returnJobListVO.setStartExecTime(DateUtil.getDate(scheduleJob.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            returnJobListVO.setEndExecTime(DateUtil.getDate(scheduleJob.getExecEndTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            returnJobListVO.setExecTime(getExecTime(scheduleJob));
            returnJobListVO.setStatus(TaskStatus.getShowStatus(scheduleJob.getStatus()));
            ScheduleTaskShade task = taskShadeMap.get(scheduleJob.getTaskId());
            returnJobListVO.setTaskName(task == null ? "" : task.getName());
            queryRelatedJobsVOS.add(returnJobListVO);
        }
        return queryRelatedJobsVOS;
    }


}
