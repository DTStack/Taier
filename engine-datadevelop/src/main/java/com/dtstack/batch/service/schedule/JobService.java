package com.dtstack.batch.service.schedule;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.mapstruct.fill.FillDataJobMapstructTransfer;
import com.dtstack.batch.vo.fill.FillDataJobReturnListVO;
import com.dtstack.batch.vo.fill.FillDataJobVO;
import com.dtstack.batch.vo.fill.FillDataReturnListVO;
import com.dtstack.engine.domain.ScheduleFillDataJob;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.po.CountFillDataJobStatusPO;
import com.dtstack.engine.mapper.ScheduleJobMapper;
import com.dtstack.engine.master.action.fill.FillDataRunnable;
import com.dtstack.engine.master.dto.fill.FillDataJobListDTO;
import com.dtstack.engine.master.dto.fill.FillDataListDTO;
import com.dtstack.engine.master.dto.fill.ScheduleFillDataInfoDTO;
import com.dtstack.engine.master.dto.fill.ScheduleFillJobParticipateDTO;
import com.dtstack.engine.master.enums.FillDataTypeEnum;
import com.dtstack.engine.master.enums.FillGeneratStatusEnum;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.pool.FillDataThreadPoolExecutor;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:26 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobService extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FillDataService fillDataJobService;

    @Autowired
    private FillDataThreadPoolExecutor fillDataThreadPoolExecutor;

    @Autowired
    private TaskService taskService;

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
        fillDataThreadPoolExecutor.submit(new FillDataRunnable(
                        fillDataJob.getId()
                        ,dto
                        ,dto.getFillDataInfo()
                        ,(fillId,originalStatus,currentStatus)->{
                            ScheduleFillDataJob updateFillDataJob = new ScheduleFillDataJob();
                            updateFillDataJob.setFillGenerateStatus(currentStatus);
                            fillDataJobService.lambdaUpdate()
                                .eq(ScheduleFillDataJob::getId,fillId)
                                .eq(ScheduleFillDataJob::getFillGenerateStatus,originalStatus)
                                .update(updateFillDataJob);
                        }
                        ,applicationContext)
        );
        return fillDataJob.getId();
    }

    /**
     * 补数据列表
     *
     * @param dto 查询列表条件
     * @return 补数据列表数据
     */
    public PageResult<List<FillDataReturnListVO>> fillDataList(FillDataListDTO dto) {
        Page<ScheduleFillDataJob> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        long bizDay = DateUtil.getTimestamp(dto.getBizDay(), DateUtil.DATE_FORMAT);
        page = fillDataJobService.lambdaQuery()
                .like(StringUtils.isNotBlank(dto.getJobName()), ScheduleFillDataJob::getJobName, dto.getJobName())
                .eq(dto.getUserId() != null, ScheduleFillDataJob::getCreateUserId, dto.getUserId())
                .eq(StringUtils.isNotBlank(dto.getRunDay()), ScheduleFillDataJob::getRunDay, dto.getRunDay())
                .eq(ScheduleFillDataJob::getTenantId, dto.getTenantId())
                .apply(StringUtils.isNotBlank(dto.getBizDay()), "str_to_date(from_day, '%Y-%m-%d') >=" + bizDay + " and str_to_date(from_day, '%Y-%m-%d') < " + bizDay)
                .page(page);

        List<ScheduleFillDataJob> records = page.getRecords();
        List<FillDataReturnListVO> fillDataReturnListVOs = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(records)) {
            Map<Long, ScheduleFillDataJob> fillDataJobMap = records.stream().collect(Collectors.toMap(ScheduleFillDataJob::getId, g -> (g)));
            
            List<CountFillDataJobStatusPO> statistics = this.baseMapper.countByFillIdGetAllStatus(fillDataJobMap.keySet());
            Map<Long, List<CountFillDataJobStatusPO>> statisticsGroup = statistics.stream().collect(Collectors.groupingBy(CountFillDataJobStatusPO::getFillId));

            for (ScheduleFillDataJob scheduleFillDataJob : records) {
                FillDataReturnListVO fillDataReturnListVO = FillDataJobMapstructTransfer.INSTANCE.fillDataListDTOToFillDataReturnListVO(scheduleFillDataJob);

                // 计算补数据执行进度
                List<CountFillDataJobStatusPO> countFillDataJobStatusPOS = statisticsGroup.get(fillDataReturnListVO.getId());
                Map<Integer, IntSummaryStatistics> statusCount = countFillDataJobStatusPOS.stream().collect(Collectors.groupingBy(CountFillDataJobStatusPO::getStatus, Collectors.summarizingInt(CountFillDataJobStatusPO::getCount)));

                calculateStatusCount(fillDataReturnListVO, statusCount);
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
    public PageResult<FillDataJobReturnListVO> fillDataJobList(FillDataJobListDTO dto) {
        Integer totalCount = 0;
        FillDataJobReturnListVO dataJobDetailVO = new FillDataJobReturnListVO();

        ScheduleFillDataJob fillDataJob = fillDataJobService.getById(dto.getFillId());
        if(checkFillDataJobList(fillDataJob,dataJobDetailVO)){
            return new PageResult<>(dto.getCurrentPage(),dto.getPageSize(),totalCount,dataJobDetailVO);
        }

        dataJobDetailVO.setId(fillDataJob.getId());
        dataJobDetailVO.setFillDataName(fillDataJob.getJobName());

        List<Long> taskIds = taskService.findTaskIdByTaskName(dto.getTaskName(),dto.getUserId());
        if (CollectionUtils.isEmpty(taskIds)){
            return new PageResult<>(dto.getCurrentPage(),dto.getPageSize(),totalCount,dataJobDetailVO);
        }

        Page<ScheduleJob> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        page = this.lambdaQuery()
                .eq(ScheduleJob::getTenantId, dto.getTenantId())
                .eq(ScheduleJob::getFillId, dto.getFillId())
                .ne(ScheduleJob::getFlowJobId,0)
                .in(ScheduleJob::getFillType, Lists.newArrayList(FillJobTypeEnum.RUN_JOB.getType()))
                .in(CollectionUtils.isNotEmpty(taskIds), ScheduleJob::getTaskId, taskIds)
                .in(CollectionUtils.isNotEmpty(dto.getTaskTypeList()), ScheduleJob::getTaskType, dto.getTaskTypeList())
                .in(CollectionUtils.isNotEmpty(dto.getJobStatusList()), ScheduleJob::getStatus, transform(dto.getJobStatusList()))
                .between((dto.getCycStartDay() != null && dto.getCycEndDay() != null), ScheduleJob::getCycTime, getCycTime(dto.getCycStartDay()), getCycTime(dto.getCycEndDay()))
                .orderBy(StringUtils.isNotBlank(dto.getExecTimeSort()),Boolean.FALSE,ScheduleJob::getExecTime)
                .orderBy(StringUtils.isNotBlank(dto.getExecStartSort()),Boolean.FALSE,ScheduleJob::getExecStartTime)
                .orderBy(StringUtils.isNotBlank(dto.getCycSort()),Boolean.FALSE,ScheduleJob::getCycTime)
                .orderBy(StringUtils.isNotBlank(dto.getRetryNumSort()),Boolean.FALSE,ScheduleJob::getRetryNum)
                .orderBy(Boolean.TRUE,Boolean.FALSE,ScheduleJob::getGmtCreate)
                .page(page);

        List<ScheduleJob> records = page.getRecords();
        
        if (CollectionUtils.isNotEmpty(records)) {
            List<FillDataJobVO> fillDataJobVOS = Lists.newArrayList();

            records.forEach(record ->{
                FillDataJobVO vo = FillDataJobMapstructTransfer.INSTANCE.scheduleJobToFillDataJobVO(record);
                vo.setExeStartTime(DateUtil.getDate(record.getExecStartTime(), DateUtil.STANDARD_DATETIME_FORMAT));
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
    private String getCycTime(Long cycTime) {
        return new DateTime(cycTime * 1000).toString(DateUtil.UN_STANDARD_DATETIME_FORMAT);
    }

    /**
     * 页码显示状态和代码运行状态并非一一对应，所以需要一层状态转换
     *
     * @param originalStatus 初始状态
     * @return 转换后状态
     */
    private List<Integer> transform(List<Integer> originalStatus) {
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
    private void calculateStatusCount(FillDataReturnListVO fillDataReturnListVO, Map<Integer, IntSummaryStatistics> statusCount) {
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
        scheduleFillDataJob.setMaxParallelNum(scheduleFillJobParticipateDTO.getMaxParallelNum());
        scheduleFillDataJob.setTenantId(scheduleFillJobParticipateDTO.getTenantId());
        scheduleFillDataJob.setCreateUserId(scheduleFillJobParticipateDTO.getUserId());
        scheduleFillDataJob.setRunDay(DateTime.now().toString(DateUtil.DATE_FORMAT));
        scheduleFillDataJob.setNumberParallelNum(scheduleFillJobParticipateDTO.getMaxParallelNum());
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
     * 校验
     * 
     * @param fillDataJob
     * @param dataJobDetailVO
     * @return
     */
    private Boolean checkFillDataJobList(ScheduleFillDataJob fillDataJob,FillDataJobReturnListVO dataJobDetailVO) {
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
}
