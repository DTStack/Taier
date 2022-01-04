package com.dtstack.engine.master.server.builder;

import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.mapper.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.enums.FillJobTypeEnum;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.master.server.builder.dependency.DependencyManager;
import com.dtstack.engine.master.service.ScheduleActionService;
import com.dtstack.engine.master.service.ScheduleJobOperatorRecordService;
import com.dtstack.engine.master.service.ScheduleJobService;
import com.dtstack.engine.master.service.ScheduleTaskService;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 7:11 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
@Component
public class FillDataJobBuilder extends AbstractJobBuilder implements InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(FillDataJobBuilder.class);

    private static final String FILL_DATA_TYPE = "fillData";
    private static final String FILL_DATA_JOB_BUILDER = "FillDataJobBuilder";

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskService scheduleTaskService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    private ExecutorService jobGraphBuildPool;

    @Transactional(rollbackFor = Exception.class)
    public void createFillJob(Set<Long> all, Set<Long> run, Long fillId, String fillName, String beginTime, String endTime,
                              String startDay, String endDay, Long tenantId, Long userId) throws Exception {
        Date startDate = DateUtil.parseDate(startDay, DateUtil.DATE_FORMAT, Locale.CHINA);
        Date endDate = DateUtil.parseDate(endDay, DateUtil.DATE_FORMAT, Locale.CHINA);

        DateTime startTime = new DateTime(startDate);
        DateTime finishTime = new DateTime(endDate);
        while (startTime.getMillis() <= finishTime.getMillis()) {
            startTime = startTime.plusDays(1);
            String triggerDay = startTime.toString(DateUtil.DATE_FORMAT);
            buildFillDataJobGraph(fillName, fillId, all, run, triggerDay, beginTime, endTime, tenantId, userId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void buildFillDataJobGraph(String fillName, Long fillId, Set<Long> all, Set<Long> run, String triggerDay,
                                       String beginTime, String endTime, Long tenantId, Long userId) throws Exception {
        // 切割控制并发数
        List<Long> allList = Lists.newArrayList(all);
        List<List<Long>> partition = Lists.partition(allList, environmentContext.getFillDataJobLimitSize());
        Semaphore buildSemaphore = new Semaphore(environmentContext.getFillDataMaxTaskBuildThread());
        CountDownLatch ctl = new CountDownLatch(partition.size());
        AtomicJobSortWorker sortWorker = new AtomicJobSortWorker();

        for (List<Long> taskKey : partition) {
            buildSemaphore.acquire();
            jobGraphBuildPool.submit(()->{
                try {
                    Map<Long, JobBuilderBean> saveMap = Maps.newHashMap();
                    for (Long taskId : taskKey) {
                        try {
                            String preStr = FILL_DATA_TYPE + "_" + fillName;
                            ScheduleTaskShade scheduleTaskShade = scheduleTaskService
                                    .lambdaQuery()
                                    .eq(ScheduleTaskShade::getTaskId, taskId)
                                    .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                                    .one();

                            if (scheduleTaskShade != null) {
                                List<JobBuilderBean> jobBuilderBeanList = Lists.newArrayList();
                                // 非工作流任务子任务
                                if (scheduleTaskShade.getFlowId() == 0) {
                                    // 生成周期实例
                                    jobBuilderBeanList = buildJob(scheduleTaskShade, preStr, triggerDay, beginTime, endTime, fillId, sortWorker);

                                } else {
                                    Long flowId = scheduleTaskShade.getFlowId();
                                    if (!allList.contains(flowId)) {
                                        // 生成周期实例
                                        jobBuilderBeanList = buildJob(scheduleTaskShade, preStr, triggerDay, beginTime, beginTime, fillId,sortWorker);
                                    }
                                }

                                for (JobBuilderBean jobBuilderBean : jobBuilderBeanList) {
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
                } finally {
                    buildSemaphore.release();
                    ctl.countDown();
                }
            });
        }
        ctl.await();
        jobGraphBuildPool.shutdown();

    }

    private void addMap(Set<Long> run, Map<Long, JobBuilderBean> saveMap, Long taskId, JobBuilderBean jobBuilderBean) {
        ScheduleJob scheduleJob = jobBuilderBean.getScheduleJob();
        if (run.contains(taskId)) {
            scheduleJob.setFillType(FillJobTypeEnum.RUN_JOB.getType());
        } else {
            scheduleJob.setFillType(FillJobTypeEnum.MIDDLE_JOB.getType());
        }

        saveMap.put(scheduleJob.getTaskId(),jobBuilderBean);

        List<JobBuilderBean> flowBean = jobBuilderBean.getFlowBean();
        
        if (CollectionUtils.isNotEmpty(flowBean)) {
            for (JobBuilderBean builderBean : flowBean) {
                ScheduleJob flowScheduleJob = builderBean.getScheduleJob();
                saveMap.put(flowScheduleJob.getTaskId(),builderBean);
            }
        }
    }

    private void savaFillJob(Map<Long, JobBuilderBean> allJob) {
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
    protected String getKeyPreStr() {
        return FILL_DATA_TYPE;
    }

    @Override
    protected Integer getType() {
        return EScheduleType.FILL_DATA.getType();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jobGraphBuildPool = new ThreadPoolExecutor(environmentContext.getFillDataJobGraphBuildPoolCorePoolSize(), environmentContext.getFillDataJobGraphBuildPoolMaximumPoolSize(), 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(environmentContext.getFillDataJobGraphBuildPoolQueueSize()), new CustomThreadFactory(FILL_DATA_JOB_BUILDER));
    }
}
