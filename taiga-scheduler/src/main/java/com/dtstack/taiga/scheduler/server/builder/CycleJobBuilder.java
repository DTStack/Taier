package com.dtstack.taiga.scheduler.server.builder;

import com.dtstack.taiga.common.enums.EScheduleStatus;
import com.dtstack.taiga.common.enums.EScheduleType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.pluginapi.util.RetryUtil;
import com.dtstack.taiga.scheduler.druid.DtDruidRemoveAbandoned;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;
import com.dtstack.taiga.scheduler.service.JobGraphTriggerService;
import com.dtstack.taiga.scheduler.utils.JobExecuteOrderUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: dazhi
 * @Date: 2022/1/5 3:52 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class CycleJobBuilder extends AbstractJobBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CycleJobBuilder.class);

    private static final String CRON_JOB_NAME = "cronJob";

    @Autowired
    protected JobGraphTriggerService jobGraphTriggerService;

    private final Lock lock = new ReentrantLock();

    public void buildTaskJobGraph(String triggerDay) {
        if (environmentContext.getJobGraphBuilderSwitch()) {
            return;
        }

        lock.lock();
        try {
            String triggerTimeStr = triggerDay + " 00:00:00";
            Timestamp triggerTime = Timestamp.valueOf(triggerTimeStr);

            boolean hasBuild = jobGraphTriggerService.checkHasBuildJobGraph(triggerTime);

            if (hasBuild) {
                LOGGER.info("trigger Day {} has build so break", triggerDay);
                return;
            }

            // 1. 如果今天已经生成实例，是需要把今天生成的实例清理调
            cleanDirtyJobGraph(triggerDay);

            // 2. 获得今天预计要生成的所有周期实例
            Integer totalTask = getTotalTask();

            LOGGER.info("{} need build job : {}",triggerTimeStr, totalTask);
            if (totalTask <= 0) {
                return;
            }

            // 3. 切割总数 限制 thread 并发
            int totalBatch = totalTask / environmentContext.getJobLimitSize();
            if (totalTask % environmentContext.getJobLimitSize() != 0) {
                totalBatch++;
            }

            Semaphore sph = new Semaphore(environmentContext.getMaxTaskBuildThread());
            CountDownLatch ctl = new CountDownLatch(totalBatch);
            AtomicJobSortWorker sortWorker = new AtomicJobSortWorker();

            // 4. 查询db多线程生成周期实例
            Long startId = 0L;
            for (int i = 0; i < totalBatch; i++) {
                // 取50个任务
                final List<ScheduleTaskShade> batchTaskShades = scheduleTaskService.listRunnableTask(startId,
                        Lists.newArrayList(EScheduleStatus.NORMAL.getVal(), EScheduleStatus.FREEZE.getVal()),
                        environmentContext.getJobLimitSize());

                // 如果取出来的任务集合是空的
                if (CollectionUtils.isEmpty(batchTaskShades)) {
                    continue;
                }

                startId = batchTaskShades.get(batchTaskShades.size() - 1).getId();
                LOGGER.info("job-number:{} startId:{}", i, startId);

                try {
                    sph.acquire();
                    jobGraphBuildPool.submit(()->{
                        try {
                            for (ScheduleTaskShade batchTaskShade : batchTaskShades) {
                                try {
                                    List<ScheduleJobDetails> scheduleJobDetails = RetryUtil.executeWithRetry(() -> buildJob(batchTaskShade, triggerDay, sortWorker),
                                            environmentContext.getBuildJobErrorRetry(), 200, false);
                                    // 插入周期实例
                                    savaJobList(scheduleJobDetails);
                                } catch (Throwable e) {
                                    LOGGER.error("build task failure taskId:{} apptype:{}",batchTaskShade.getTaskId(),null, e);
                                }
                            }
                        } catch (Throwable e) {
                            LOGGER.error("!!! buildTaskJobGraph  build job error !!!", e);
                        } finally {
                            sph.release();
                            ctl.countDown();
                        }
                    });
                }  catch (Throwable e) {
                    LOGGER.error("[acquire pool error]:", e);
                    throw new RdosDefineException(e);
                }
            }
            ctl.await();

            // 循环已经结束，说明周期实例已经全部生成了
            saveJobGraph(triggerDay);
        } catch (Exception e) {
            LOGGER.error("buildTaskJobGraph ！！！", e);
        } finally {
            LOGGER.info("buildTaskJobGraph exit & unlock ...");
            lock.unlock();
        }
    }

    /**
     * 保存周期实例
     *
     * @param scheduleJobDetails 实例详情
     */
    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    private void savaJobList(List<ScheduleJobDetails> scheduleJobDetails) {
        List<ScheduleJobDetails> savaJobDetails = Lists.newArrayList();
        for (ScheduleJobDetails scheduleJobDetail : scheduleJobDetails) {
            savaJobDetails.add(scheduleJobDetail);
            List<ScheduleJobDetails> flowBean = scheduleJobDetail.getFlowBean();

            if (CollectionUtils.isNotEmpty(flowBean)) {
                savaJobDetails.addAll(flowBean);
            }
        }

        scheduleJobService.insertJobList(savaJobDetails, getType());
    }

    /**
     * 保存生成的jobGraph记录
     */
    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public boolean saveJobGraph(String triggerDay) {
        LOGGER.info("start saveJobGraph to db {}", triggerDay);
        //记录当天job已经生成
        String triggerTimeStr = triggerDay + " 00:00:00";
        Long minJobId = JobExecuteOrderUtil.buildJobExecuteOrder(triggerTimeStr,0L);
        Timestamp timestamp = Timestamp.valueOf(triggerTimeStr);
        try {
            RetryUtil.executeWithRetry(() -> {
                jobGraphTriggerService.addJobTrigger(timestamp,minJobId);
                return null;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            LOGGER.error("addJobTrigger triggerTimeStr {} error ", triggerTimeStr,e);
            throw new RdosDefineException(e);
        }
        return true;
    }

    private Integer getTotalTask() {
        return 0;
    }

    private void cleanDirtyJobGraph(String triggerDay) {

    }

    @Override
    protected String getPrefix() {
        return CRON_JOB_NAME;
    }

    @Override
    protected Integer getType() {
        return EScheduleType.NORMAL_SCHEDULE.getType();
    }
}
