package com.dtstack.taier.scheduler.server.scheduler;

import com.dtstack.taier.common.CustomThreadRunsPolicy;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.scheduler.enums.JobPhaseStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 7:24 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractJobSummitScheduler extends AbstractJobScanningScheduler implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractJobScanningScheduler.class);

    protected final AtomicBoolean RUNNING = new AtomicBoolean(true);

    /**
     * 提交任务线程池
     */
    private ExecutorService executorService;

    /**
     * 队列用于控制提交速度
     */
    private LinkedBlockingQueue<ScheduleJobDetails> scheduleJobQueue;

    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 实例提交条件
     *
     * @param scheduleJobDetails 实例详情
     * @return
     */
    @Override
    public Boolean submitJob(ScheduleJobDetails scheduleJobDetails) {
        try {
            ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
            boolean updateStatus = scheduleJobService.updatePhaseStatusById(scheduleJob.getId(), JobPhaseStatus.CREATE, JobPhaseStatus.JOIN_THE_TEAM);
            if (updateStatus && scheduleJobQueue.contains(scheduleJobDetails)) {
                //元素已存在，返回true
                LOGGER.info("jobId:{} scheduleType:{} queue has contains ", scheduleJob.getJobId(), getSchedulerName());
                return false;
            }
            scheduleJobQueue.put(scheduleJobDetails);
            LOGGER.info("jobId:{} scheduleType:{} enter queue", scheduleJob.getJobId(), getSchedulerName());
            return true;
        } catch (InterruptedException e) {
            ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
            LOGGER.error("jobId:{} scheduleType:{} job phase rollback, error", scheduleJob.getJobId(), getSchedulerName(), e);
            scheduleJobService.updatePhaseStatusById(scheduleJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.CREATE);
            return false;
        }
    }

    /**
     * 提交实例
     *
     * @param scheduleJobDetails 实例信息
     */
    private void submit(ScheduleJobDetails scheduleJobDetails) {
        try {
            executorService.submit(() -> {
                ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
                try {
                    // 发布提交事件

                    scheduleJobService.startJob(scheduleJobDetails);
                    LOGGER.info("--- jobId:{} scheduleType:{} send to engine.", scheduleJob.getJobId(), getSchedulerName());
                } catch (Exception e) {
                    LOGGER.info("--- jobId:{} scheduleType:{} send to engine error:", scheduleJob.getJobId(), getSchedulerName(), e);
                    scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.FAILED.getStatus(), ExceptionUtil.getErrorMessage(e));
                } finally {
                    scheduleJobService.updatePhaseStatusById(scheduleJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.EXECUTE_OVER);
                }
            });
        } catch (Exception e) {
            ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
            LOGGER.info("--- jobId:{} scheduleType:{} executorService submit to engine error:", scheduleJob.getJobId(), getSchedulerName(), e);
        }
    }

    /**
     * 停止线程
     */
    public void stop() {
        RUNNING.set(false);
        LOGGER.info("---stop {}----",getSchedulerName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        scheduleJobQueue = new LinkedBlockingQueue<>(env.getQueueSize());

        String threadName = this.getClass().getSimpleName() + "_" + getSchedulerName() + "_startJobProcessor";
        executorService = new ThreadPoolExecutor(env.getJobExecutorPoolCorePoolSize(), env.getJobExecutorPoolMaximumPoolSize(), env.getJobExecutorPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(env.getJobExecutorPoolQueueSize()),
                new CustomThreadRunsPolicy<ScheduleJob>(threadName, getSchedulerName(),(job -> {
                    scheduleJobService.updatePhaseStatusById(job.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.CREATE);
                    LOGGER.warn("start job processor reject job {},return job to db", job.getJobId());
                })));
    }


    @Override
    public void run() {
        while (RUNNING.get()) {

            ScheduleJob scheduleJob = null;
            try {
                ScheduleJobDetails scheduleJobDetails = scheduleJobQueue.take();
                scheduleJob = scheduleJobDetails.getScheduleJob();
                LOGGER.info("jobId:{} scheduleType:{} take job from queue.", scheduleJob.getJobId(), getSchedulerName());
                this.submit(scheduleJobDetails);
            } catch (InterruptedException ie){
                // swallow the interrupt as it's only possible from either a background
                // operation and, thus, doesn't apply to this loop or the instance
                // is being closed in which case the while test will get it
            } catch (Exception e) {
                LOGGER.error("happens error:", e);
                try {
                    if (scheduleJob != null) {
                        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.SUBMITFAILD.getStatus(), e.getMessage());
                        LOGGER.error("jobId:{} scheduleType:{} submit failed.", scheduleJob.getJobId(), getSchedulerName());
                    }
                } catch (Exception ex) {
                    LOGGER.error("scheduleType:{} update status happens error:", getSchedulerName(), ex);
                }
            }
        }
    }
}
