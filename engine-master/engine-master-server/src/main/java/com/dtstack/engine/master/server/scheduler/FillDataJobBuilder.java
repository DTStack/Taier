package com.dtstack.engine.master.server.scheduler;

import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.ForceCancelFlag;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.mapper.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.pluginapi.util.DateUtil;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/7 5:12 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class FillDataJobBuilder extends AbstractBuilder  {

    private final static Logger LOGGER = LoggerFactory.getLogger(FillDataJobBuilder.class);

    private static final String FILL_DATA_TYPE = "fillData";
    private static final String FILL_DATA_JOB_BUILDER = "FillDataJobBuilder";

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Transactional(rollbackFor = Exception.class)
    public void createFillJob(Set<Long> all, Set<Long> run, List<Long> blackTaskKeyList, Long fillId, String fillName, String beginTime, String endTime,
                              String startDay, String endDay, Long tenantId, Long userId) throws Exception {
        Date startDate = DateUtil.parseDate(startDay, DateUtil.DATE_FORMAT, Locale.CHINA);
        Date endDate = DateUtil.parseDate(endDay, DateUtil.DATE_FORMAT, Locale.CHINA);

        DateTime startTime = new DateTime(startDate);
        DateTime finishTime = new DateTime(endDate);
        while (startTime.getMillis() <= finishTime.getMillis()) {
            startTime = startTime.plusDays(1);
            String triggerDay = startTime.toString(DateUtil.DATE_FORMAT);
            buildFillDataJobGraph(fillName, fillId, all, run, blackTaskKeyList, triggerDay, beginTime, endTime, tenantId, userId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void buildFillDataJobGraph(String fillName, Long fillId, Set<Long> all, Set<Long> run,
                                      List<Long> blackTaskKey, String triggerDay, String beginTime,
                                      String endTime, Long tenantId, Long userId) throws Exception{

        ExecutorService jobGraphBuildPool = new ThreadPoolExecutor(MAX_TASK_BUILD_THREAD, MAX_TASK_BUILD_THREAD, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_TASK_BUILD_THREAD), new CustomThreadFactory(FILL_DATA_JOB_BUILDER));

        // 切割控制并发数
        List<Long> allList = Lists.newArrayList(all);
        List<List<Long>> partition = Lists.partition(allList, TASK_BATCH_SIZE);
        Semaphore buildSemaphore = new Semaphore(MAX_TASK_BUILD_THREAD);
        CountDownLatch ctl = new CountDownLatch(partition.size());
        AtomicInteger count = new AtomicInteger();

        for (List<Long> taskKey : partition) {
            buildSemaphore.acquire();
            jobGraphBuildPool.submit(()->{
                try {
                    Map<String, ScheduleBatchJob> saveMap = Maps.newHashMap();
                    for (Long taskId : taskKey) {
                        try {
                            String preStr = FILL_DATA_TYPE + "_" + fillName;
                            ScheduleTaskShade batchTask = batchTaskShadeService.getBatchTaskById(taskId);
                            if (batchTask != null) {
                                // 查询绑定任务
                                List<ScheduleBatchJob> batchJobs = Lists.newArrayList();
                                if (batchTask.getFlowId() == 0) {
                                    // 查询出该任务的周期实例
                                    batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, true,
                                            true, triggerDay, fillName, userId, beginTime, endTime, tenantId,count);

                                    //工作流情况的处理
                                    if (batchTask.getTaskType().intValue() == EScheduleJobType.WORK_FLOW.getVal()) {
                                        Map<String, String> flowJobId = Maps.newHashMap();
                                        for (ScheduleBatchJob jobRunBean : batchJobs) {
                                            flowJobId.put(batchTask.getTaskId() + "_" + jobRunBean.getCycTime(), jobRunBean.getJobId());
                                        }
                                        //将工作流下的子任务生成补数据任务实例
                                        List<ScheduleBatchJob> subTaskJobs = buildSubTasksJobForFlowWork(batchTask.getTaskId(), preStr, fillName, triggerDay, userId, beginTime, endTime, tenantId);
                                        LOGGER.error("buildFillDataJobGraph for flowTask with flowJobId map [{}]", flowJobId);
                                        doSetFlowJobIdForSubTasks(subTaskJobs, flowJobId);
                                        batchJobs.addAll(subTaskJobs);
                                    }
                                } else {
                                    Long flowId = batchTask.getFlowId();
                                    if (!allList.contains(flowId)) {
                                        // 生成周期实例
                                        batchJobs = buildJobRunBean(batchTask, preStr, EScheduleType.FILL_DATA, true,
                                                true, triggerDay, fillName, userId, beginTime, endTime, tenantId, count);

                                        if (CollectionUtils.isNotEmpty(batchJobs)) {
                                            for (ScheduleBatchJob batchJob : batchJobs) {
                                                if (batchJob.getScheduleJob()!=null) {
                                                    batchJob.getScheduleJob().setFlowJobId(NORMAL_TASK_FLOW_ID);
                                                }
                                            }
                                        }
                                    }
                                }

                                for (ScheduleBatchJob batchJob : batchJobs) {
                                    addMap(fillId, run, blackTaskKey, saveMap, taskId, batchJob);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("taskKey : {} error:",taskId,e);
                        }
                    }
                    savaFillJob(saveMap);
                } catch (Exception e) {
                    LOGGER.error("fill error:",e);
                } finally {
                    buildSemaphore.release();
                    ctl.countDown();
                }
            });
        }
        ctl.await();
        jobGraphBuildPool.shutdown();
    }

    private List<ScheduleBatchJob> buildSubTasksJobForFlowWork(Long taskId, String preStr, String fillJobName,
                                                               String triggerDay, Long createUserId,
                                                               String beginTime, String endTime, Long tenantId) throws Exception {
        List<ScheduleBatchJob> result = Lists.newArrayList();
        //获取全部子任务
        List<ScheduleTaskShade> subTasks = batchTaskShadeService.getFlowWorkSubTasks(taskId, null, null);
        AtomicInteger atomicInteger = new AtomicInteger();
        for (ScheduleTaskShade taskShade : subTasks) {
            String subKeyPreStr = preStr;
            String subFillJobName = fillJobName;
            //子任务需添加依赖关系
            List<ScheduleBatchJob> batchJobs = buildJobRunBean(taskShade, subKeyPreStr, EScheduleType.FILL_DATA, true, true,
                    triggerDay, subFillJobName, createUserId, beginTime, endTime, tenantId,atomicInteger);
            result.addAll(batchJobs);
        }

        return result;
    }

    private void addMap(Long fillId, Set<Long> runList, List<Long> blackTaskKey, Map<String, ScheduleBatchJob> allJob, Long taskId, ScheduleBatchJob batchJob) {
        ScheduleJob scheduleJob = batchJob.getScheduleJob();
        scheduleJob.setFillId(fillId);


//        if (runList.contains(key)) {
//            scheduleJob.setFillType(FillJobTypeEnum.RUN_JOB.getType());
//        } else if (blackTaskKey.contains(key)) {
//            scheduleJob.setFillType(FillJobTypeEnum.BLACK_JOB.getType());
//        } else {
//            scheduleJob.setFillType(FillJobTypeEnum.MIDDLE_JOB.getType());
//        }

        allJob.put(batchJob.getJobKey(),batchJob);
    }

    private void savaFillJob(Map<String, ScheduleBatchJob> allJob) {
        batchJobService.insertJobList(allJob.values(), EScheduleType.FILL_DATA.getType());
        List<ScheduleJobOperatorRecord> operatorJobIds = allJob.values()
                .stream()
                .map(scheduleBatchJob -> {
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(scheduleBatchJob.getJobId());
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    record.setOperatorType(OperatorType.FILL_DATA.getType());
                    record.setNodeAddress(scheduleBatchJob.getScheduleJob().getNodeAddress());
                    return record;
                })
                .collect(Collectors.toList());

        scheduleJobOperatorRecordDao.insertBatch(operatorJobIds);
    }

}
