package com.dtstack.taiga.scheduler.server.builder;

import com.dtstack.taiga.common.enums.EScheduleType;
import com.dtstack.taiga.common.enums.ForceCancelFlag;
import com.dtstack.taiga.common.enums.Deleted;
import com.dtstack.taiga.common.enums.OperatorType;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.pluginapi.util.DateUtil;
import com.dtstack.taiga.pluginapi.util.RetryUtil;
import com.dtstack.taiga.scheduler.enums.FillJobTypeEnum;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;
import com.dtstack.taiga.scheduler.service.ScheduleJobOperatorRecordService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private static final String FILL_DATA_JOB_BUILDER = "FillDataJobBuilder";



    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;



    /**
     * 创建补数据实例
     *
     * @param all all list 所有节点
     * @param run run list 可运行节点
     * @param fillId 补数据id
     * @param fillName 补数据名称
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param startDay 每天时间范围 开始范围
     * @param endDay 每天时间范围 结束范围
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
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
     * @param fillName 补数据名称
     * @param fillId 补数据id
     * @param all all list 所有节点
     * @param run run list 可运行节点
     * @param triggerDay 具体目标天
     * @param beginTime  每天时间范围 开始范围
     * @param endTime 每天时间范围 结束范围
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    private void buildFillDataJobGraph(String fillName, Long fillId, Set<Long> all, Set<Long> run, String triggerDay,
                                       String beginTime, String endTime) throws Exception {
        List<Long> allList = Lists.newArrayList(all);
        List<List<Long>> partition = Lists.partition(allList, environmentContext.getJobLimitSize());
        AtomicJobSortWorker sortWorker = new AtomicJobSortWorker();

        for (List<Long> taskKey : partition) {
            jobGraphBuildPool.submit(()->{
                try {
                    Map<Long, ScheduleJobDetails> saveMap = Maps.newHashMap();
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
                                        jobBuilderBeanList = RetryUtil.executeWithRetry(() -> buildJob(scheduleTaskShade, fillName, triggerDay, beginTime, beginTime, fillId,sortWorker),
                                                environmentContext.getBuildJobErrorRetry(), 200, false);
                                    }
                                }

                                for (ScheduleJobDetails jobBuilderBean : jobBuilderBeanList) {
                                    addMap(run, saveMap,taskId, jobBuilderBean);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("taskKey : {} error:",taskId,e);
                        }
                    }
                    savaFillJob(saveMap);
                } catch (Exception e) {
                    LOGGER.error("fill error:",e);
                }
            });
        }
    }

    /**
     *
     * @param run  run list 可运行节点
     * @param saveMap 生成实例集合
     * @param taskId 任务id
     * @param jobBuilderBean 构建出来的实际
     */
    private void addMap(Set<Long> run, Map<Long, ScheduleJobDetails> saveMap, Long taskId, ScheduleJobDetails jobBuilderBean) {
        ScheduleJob scheduleJob = jobBuilderBean.getScheduleJob();
        if (run.contains(taskId)) {
            scheduleJob.setFillType(FillJobTypeEnum.RUN_JOB.getType());
        } else {
            scheduleJob.setFillType(FillJobTypeEnum.MIDDLE_JOB.getType());
        }

        saveMap.put(scheduleJob.getTaskId(),jobBuilderBean);

        List<ScheduleJobDetails> flowBean = jobBuilderBean.getFlowBean();
        
        if (CollectionUtils.isNotEmpty(flowBean)) {
            for (ScheduleJobDetails builderBean : flowBean) {
                ScheduleJob flowScheduleJob = builderBean.getScheduleJob();
                flowScheduleJob.setFillType(FillJobTypeEnum.RUN_JOB.getType());
                saveMap.put(flowScheduleJob.getTaskId(),builderBean);
            }
        }
    }

    /**
     * 持久化时间
     *
     * @param allJob 所有集合
     */
    private void savaFillJob(Map<Long, ScheduleJobDetails> allJob) {
        scheduleJobService.insertJobList(allJob.values(), EScheduleType.FILL_DATA.getType());
        List<ScheduleJobOperatorRecord> operatorJobIds = allJob.values()
                .stream()
                .map(jobBuilderBean -> {
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(jobBuilderBean.getScheduleJob().getJobId());
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    record.setOperatorType(OperatorType.FILL_DATA.getType());
                    record.setNodeAddress(jobBuilderBean.getScheduleJob().getNodeAddress());
                    return record;
                })
                .collect(Collectors.toList());

        scheduleJobOperatorRecordService.saveBatch(operatorJobIds);
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
