package com.dtstack.batch.service.schedule;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.batch.enums.EScheduleType;
import com.dtstack.batch.event.FillStatusUpdateFinishEvent;
import com.dtstack.batch.mapstruct.fill.FillDataJobMapstructTransfer;
import com.dtstack.batch.mapstruct.job.JobMapstructTransfer;
import com.dtstack.batch.vo.fill.ReturnFillDataJobListVO;
import com.dtstack.batch.vo.fill.FillDataJobVO;
import com.dtstack.batch.vo.fill.ReturnFillDataListVO;
import com.dtstack.batch.vo.schedule.ReturnDisplayPeriodVO;
import com.dtstack.batch.vo.schedule.ReturnJobListVO;
import com.dtstack.batch.vo.schedule.ReturnJobStatusStatisticsVO;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.domain.po.CountFillDataJobStatusPO;
import com.dtstack.engine.domain.po.JobsStatusStatisticsPO;
import com.dtstack.engine.domain.po.StatusCountPO;
import com.dtstack.engine.mapper.ScheduleJobMapper;
import com.dtstack.engine.master.server.action.fill.FillDataRunnable;
import com.dtstack.engine.master.dto.fill.QueryFillDataJobListDTO;
import com.dtstack.engine.master.dto.fill.QueryFillDataListDTO;
import com.dtstack.engine.master.dto.fill.ScheduleFillDataInfoDTO;
import com.dtstack.engine.master.dto.fill.ScheduleFillJobParticipateDTO;
import com.dtstack.engine.master.dto.schedule.QueryJobListDTO;
import com.dtstack.engine.master.dto.schedule.QueryJobStatusStatisticsDTO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.pool.FillDataThreadPoolExecutor;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:26 PM
 */
@Service
public class JobService extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {

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
        List<ReturnJobListVO> returnJobListVOS= Lists.newArrayList();
        // 关联任务
        List<Long> taskIds = null;
        if (StringUtils.isNotBlank(dto.getTaskName()) || dto.getOwnerId() != null) {
            List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(dto.getTaskName(), dto.getOwnerId());
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
                .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .in(ScheduleJob::getFillType, Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(),FillJobTypeEnum.RUN_JOB.getType()))
                .eq(ScheduleJob::getType, EScheduleType.NORMAL_SCHEDULE.getType())
                .eq(ScheduleJob::getTenantId, dto.getTenantId())
                .in(CollectionUtils.isNotEmpty(taskIds), ScheduleJob::getTaskId, taskIds)
                .between((dto.getCycStartDay() != null && dto.getCycEndDay() != null), ScheduleJob::getCycTime, getCycTime(dto.getCycStartDay()), getCycTime(dto.getCycEndDay()))
                .in(CollectionUtils.isNotEmpty(dto.getTaskTypeList()), ScheduleJob::getTaskType, dto.getTaskTypeList())
                .in(CollectionUtils.isNotEmpty(dto.getJobStatusList()), ScheduleJob::getStatus, transform(dto.getJobStatusList()))
                .in(CollectionUtils.isNotEmpty(dto.getTaskPeriodTypeList()), ScheduleJob::getPeriodType, dto.getTaskPeriodTypeList())
                .orderBy(StringUtils.isNotBlank(dto.getCycSort()),isAsc(dto.getCycSort()),ScheduleJob::getCycTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecStartSort()),isAsc(dto.getExecStartSort()),ScheduleJob::getExecStartTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecEndSort()),isAsc(dto.getExecEndSort()),ScheduleJob::getExecEndTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecTimeSort()),isAsc(dto.getExecTimeSort()),ScheduleJob::getExecTime)
                .orderBy(StringUtils.isNotBlank(dto.getRetryNumSort()),isAsc(dto.getRetryNumSort()),ScheduleJob::getRetryNum)
                .orderBy(Boolean.TRUE,Boolean.FALSE,ScheduleJob::getGmtCreate)
                .page(page);

        // 处理查询出来的结果集
        List<ScheduleJob> records = page.getRecords();
        totalCount = (int)page.getTotal();
        if (CollectionUtils.isNotEmpty(records)) {
            // 查询实例对应的任务
            buildReturnJobListVO(returnJobListVOS, records);
        }

        return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), totalCount, returnJobListVOS);
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
        if (StringUtils.isNotBlank(dto.getTaskName()) || dto.getOwnerId() != null) {
            List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(dto.getTaskName(), dto.getOwnerId());
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
        jobsStatusStatistics.setFillTypeList(Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(),FillJobTypeEnum.RUN_JOB.getType()));
        jobsStatusStatistics.setTaskIdList(taskIdList);

        List<StatusCountPO> statusCountList = this.baseMapper.queryJobsStatusStatistics(jobsStatusStatistics);
        // 封装结果集
        return mergeStatusAndShow(statusCountList);
    }

    /**
     *
     * @param jobId
     * @return
     */
    public List<ReturnJobListVO> queryFlowWorkSubJobs(String jobId) {
        List<ScheduleJob> jobs = this.lambdaQuery().eq(ScheduleJob::getFlowJobId, jobId).eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType()).list();
        List<ReturnJobListVO> returnJobListVOS = Lists.newArrayList();
        buildReturnJobListVO(returnJobListVOS,jobs);
        return returnJobListVOS;
    }

    /**
     * 查询上一个周期或者下一个周期实例
     *
     * @param isAfter 是否是上个周期
     * @param jobId 实例id
     * @param limit 查询个数
     */
    public List<ReturnDisplayPeriodVO> displayPeriods(Boolean isAfter, String jobId, Integer limit) {
        ScheduleJob scheduleJob = this.lambdaQuery()
                .eq(ScheduleJob::getJobId, jobId)
                .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .one();
        if (scheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
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
            vo.setJobId(nextScheduleJob.getId());
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
        fillDataThreadPoolExecutor.submit(new FillDataRunnable(fillDataJob.getId(),dto, fillDataInfo,fillStatusUpdateFinishEvent,applicationContext));
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
                .eq(dto.getOwnerId() != null, ScheduleFillDataJob::getCreateUserId, dto.getOwnerId())
                .eq(StringUtils.isNotBlank(dto.getRunDay()), ScheduleFillDataJob::getRunDay, dto.getRunDay())
                .eq(ScheduleFillDataJob::getTenantId, dto.getTenantId())
                .orderBy(true,true,ScheduleFillDataJob::getGmtCreate)
                .page(page);

        List<ScheduleFillDataJob> records = page.getRecords();
        List<ReturnFillDataListVO> fillDataReturnListVOs = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(records)) {
            // 封装结果集
            Map<Long, ScheduleFillDataJob> fillDataJobMap = records.stream().collect(Collectors.toMap(ScheduleFillDataJob::getId, g -> (g)));

            List<CountFillDataJobStatusPO> statistics = this.baseMapper.countByFillIdGetAllStatus(fillDataJobMap.keySet());
            Map<Long, List<CountFillDataJobStatusPO>> statisticsGroup = statistics.stream().collect(Collectors.groupingBy(CountFillDataJobStatusPO::getFillId));

            for (ScheduleFillDataJob scheduleFillDataJob : records) {
                ReturnFillDataListVO fillDataReturnListVO = FillDataJobMapstructTransfer.INSTANCE.fillDataListDTOToFillDataReturnListVO(scheduleFillDataJob);
                fillDataReturnListVO.setGmtCreate(DateUtil.getDate(scheduleFillDataJob.getGmtCreate(),DateUtil.STANDARD_DATETIME_FORMAT));
                // 计算补数据执行进度
                List<CountFillDataJobStatusPO> countFillDataJobStatusPOS = statisticsGroup.get(fillDataReturnListVO.getId());
                if (CollectionUtils.isNotEmpty(countFillDataJobStatusPOS)) {
                    Map<Integer, IntSummaryStatistics> statusCount = countFillDataJobStatusPOS.stream().collect(Collectors.groupingBy(CountFillDataJobStatusPO::getStatus, Collectors.summarizingInt(CountFillDataJobStatusPO::getCount)));
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
        if(!checkFillDataJobList(fillDataJob,dataJobDetailVO)){
            return new PageResult<>(dto.getCurrentPage(),dto.getPageSize(),totalCount,dataJobDetailVO);
        }

        dataJobDetailVO.setId(fillDataJob.getId());
        dataJobDetailVO.setFillDataName(fillDataJob.getJobName());


        // 关联任务
        List<Long> taskIds = null;
        if (StringUtils.isNotBlank(dto.getTaskName()) || dto.getOwnerId() != null) {
            List<ScheduleTaskShade> scheduleTaskShadeList = taskService.findTaskByTaskName(dto.getTaskName(), dto.getOwnerId());
            if (CollectionUtils.isEmpty(scheduleTaskShadeList)) {
                return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), totalCount, dataJobDetailVO);
            } else {
                taskIds = scheduleTaskShadeList.stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
            }
        }

        // 查询实例表
        Page<ScheduleJob> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        page = this.lambdaQuery()
                .eq(ScheduleJob::getFlowJobId,0)
                .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .eq(ScheduleJob::getTenantId, dto.getTenantId())
                .eq(ScheduleJob::getFillId, dto.getFillId())
                .eq(ScheduleJob::getType, EScheduleType.FILL_DATA.getType())
                .in(ScheduleJob::getFillType, Lists.newArrayList(FillJobTypeEnum.DEFAULT.getType(),FillJobTypeEnum.RUN_JOB.getType()))
                .in(CollectionUtils.isNotEmpty(taskIds), ScheduleJob::getTaskId, taskIds)
                .in(CollectionUtils.isNotEmpty(dto.getTaskTypeList()), ScheduleJob::getTaskType, dto.getTaskTypeList())
                .in(CollectionUtils.isNotEmpty(dto.getJobStatusList()), ScheduleJob::getStatus, transform(dto.getJobStatusList()))
                .between((dto.getCycStartDay() != null && dto.getCycEndDay() != null), ScheduleJob::getCycTime, getCycTime(dto.getCycStartDay()), getCycTime(dto.getCycEndDay()))
                .orderBy(StringUtils.isNotBlank(dto.getExecTimeSort()),isAsc(dto.getExecTimeSort()),ScheduleJob::getExecTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecStartSort()),isAsc(dto.getExecStartSort()),ScheduleJob::getExecStartTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecEndSort()),isAsc(dto.getExecEndSort()),ScheduleJob::getExecEndTime)
                .orderBy(StringUtils.isNotBlank(dto.getCycSort()),isAsc(dto.getCycSort()),ScheduleJob::getCycTime)
                .orderBy(StringUtils.isNotBlank(dto.getRetryNumSort()),isAsc(dto.getRetryNumSort()),ScheduleJob::getRetryNum)
                .orderBy(Boolean.TRUE,Boolean.FALSE,ScheduleJob::getGmtCreate)
                .page(page);

        List<ScheduleJob> records = page.getRecords();

        // 封装结果集
        if (CollectionUtils.isNotEmpty(records)) {
            List<FillDataJobVO> fillDataJobVOS = Lists.newArrayList();

            List<Long> taskIdList = records.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
            List<ScheduleTaskShade> taskShadeList = taskService.lambdaQuery().in(ScheduleTaskShade::getTaskId, taskIdList).eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE).list();
            Map<Long, ScheduleTaskShade> taskShadeMap = taskShadeList.stream().collect(Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g)));

            records.forEach(record ->{
                FillDataJobVO vo = FillDataJobMapstructTransfer.INSTANCE.scheduleJobToFillDataJobVO(record);
                vo.setTaskName(taskShadeMap.get(record.getTaskId()) != null ? taskShadeMap.get(record.getTaskId()).getName() : "");
                vo.setStartExecTime(DateUtil.getDate(record.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
                vo.setEndExecTime(DateUtil.getDate(record.getExecEndTime(), DateUtil.STANDARD_DATETIME_FORMAT));
                vo.setCycTime(DateUtil.addTimeSplit(record.getCycTime()));
                vo.setExecTime(getExecTime(record));
                vo.setOwnerId(taskShadeMap.get(record.getTaskId()) != null ? taskShadeMap.get(record.getTaskId()).getOwnerUserId() : 0L);
                fillDataJobVOS.add(vo);
            });

            dataJobDetailVO.setFillDataJobVOLists(fillDataJobVOS);
        }
        dataJobDetailVO.setFillGenerateStatus(FillGeneratStatusEnum.FILL_FINISH.getType());
        return new PageResult<>(dto.getCurrentPage(),dto.getPageSize(),totalCount,dataJobDetailVO);
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
        Map<Integer, List<Integer>> statusMap = RdosTaskStatus.getStatusFailedDetailAndExpire();
        for (Integer status : originalStatus) {
            List<Integer> statusList = statusMap.get(status);
            if (CollectionUtils.isNotEmpty(statusList)) {
                statues.addAll(statusList);
            }
        }
        return statues;
    }

    /**
     * 计算补数据执行进度
     *
     * @param fillDataReturnListVO 补数据返回列表vo
     * @param statusCount 查询出来的计数
     */
    private void calculateStatusCount(ReturnFillDataListVO fillDataReturnListVO, Map<Integer, IntSummaryStatistics> statusCount) {
        Long unSubmit = statusCount.get(RdosTaskStatus.UNSUBMIT.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.UNSUBMIT.getStatus()).getSum();
        Long running = statusCount.get(RdosTaskStatus.RUNNING.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.RUNNING.getStatus()).getSum();
        Long notFound = statusCount.get(RdosTaskStatus.NOTFOUND.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.NOTFOUND.getStatus()).getSum();
        Long finished = statusCount.get(RdosTaskStatus.FINISHED.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.FINISHED.getStatus()).getSum();
        Long failed = statusCount.get(RdosTaskStatus.FAILED.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.FAILED.getStatus()).getSum();
        Long waitEngine = statusCount.get(RdosTaskStatus.WAITENGINE.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.WAITENGINE.getStatus()).getSum();
        Long submitting = statusCount.get(RdosTaskStatus.SUBMITTING.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.SUBMITTING.getStatus()).getSum();
        Long canceled = statusCount.get(RdosTaskStatus.CANCELED.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.CANCELED.getStatus()).getSum();
        Long frozen = statusCount.get(RdosTaskStatus.FROZEN.getStatus()) == null ? 0L : statusCount.get(RdosTaskStatus.FROZEN.getStatus()).getSum();

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
            throw new RdosDefineException("(fillName 参数不能为空)", ErrorCode.INVALID_PARAMETERS);
        }

        //补数据的名称中-作为分割名称和后缀信息的分隔符,故不允许使用
        if (fillName.contains("-")) {
            throw new RdosDefineException("(fillName 参数不能包含字符 '-')", ErrorCode.INVALID_PARAMETERS);
        }

        if (!startTime.isBefore(DateTime.now())) {
            throw new RdosDefineException("(补数据业务日期开始时间不能晚于结束时间)", ErrorCode.INVALID_PARAMETERS);
        }

        if (fillDataInfo == null) {
            throw new RdosDefineException("fillDataInfo is not null", ErrorCode.INVALID_PARAMETERS);
        }

        if (FillDataTypeEnum.PROJECT.getType().equals(fillDataInfo.getFillDataType()) && (endTime.getMillis() - startTime.getMillis()) / (1000 * 3600 * 24) > 7) {
            throw new RdosDefineException("The difference between the start and end days cannot exceed 7 days", ErrorCode.INVALID_PARAMETERS);
        }

        if (fillDataJobService.checkExistsName(fillName)) {
            throw new RdosDefineException("补数据任务名称已存在", ErrorCode.NAME_ALREADY_EXIST);
        }
    }

    /**
     * 校验补数据列表
     * 
     * @param fillDataJob 补数据信息
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
     *
     * 判断排序规则
     * @param sort 配置字段
     * @return 是否正序
     */
    private boolean isAsc(String sort) {
        return "asc".equals(sort);
    }

    /**
     * 获得执行时间
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
            return DateUtil.getTimeDifference(System.currentTimeMillis() - execStartTime.getTime() );
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
        Map<Integer, List<Integer>> statusMap = RdosTaskStatus.getStatusFailedDetail();
        for (Map.Entry<Integer, List<Integer>> entry : statusMap.entrySet()) {
            ReturnJobStatusStatisticsVO vo = new ReturnJobStatusStatisticsVO();
            String statusName = RdosTaskStatus.getCode(entry.getKey());
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
                vo.setCount(vo.getCount()+num);
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
     * @param returnJobListVOS 结果集
     * @param records 记录数
     */
    private void buildReturnJobListVO(List<ReturnJobListVO> returnJobListVOS, List<ScheduleJob> records) {
        List<Long> taskIdList = records.stream().map(ScheduleJob::getTaskId).collect(Collectors.toList());
        List<ScheduleTaskShade> taskShadeList = taskService.lambdaQuery().in(ScheduleTaskShade::getTaskId, taskIdList).eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE).list();
        Map<Long, ScheduleTaskShade> taskShadeMap = taskShadeList.stream().collect(Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g)));

        // 封装返回值
        for (ScheduleJob scheduleJob : records) {
            ReturnJobListVO returnJobListVO = JobMapstructTransfer.INSTANCE.scheduleJobToReturnJobListVO(scheduleJob);
            returnJobListVO.setTaskName(taskShadeMap.get(returnJobListVO.getTaskId()) != null ? taskShadeMap.get(returnJobListVO.getTaskId()).getName() : "");
            returnJobListVO.setCycTime(DateUtil.addTimeSplit(scheduleJob.getCycTime()));
            returnJobListVO.setStartExecTime(DateUtil.getDate(scheduleJob.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            returnJobListVO.setEndExecTime(DateUtil.getDate(scheduleJob.getExecEndTime(), DateUtil.STANDARD_DATETIME_FORMAT));
            returnJobListVO.setExecTime(getExecTime(scheduleJob));
            returnJobListVO.setOwnerId(taskShadeMap.get(scheduleJob.getTaskId()) != null ? taskShadeMap.get(scheduleJob.getTaskId()).getOwnerUserId() : 0L);
            returnJobListVOS.add(returnJobListVO);
        }
    }

    public ScheduleJob getScheduleJob(String jobId){
        return getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleJob.class).eq(ScheduleJob::getJobId, jobId));
    }

}
