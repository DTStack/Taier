package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.jobdealer.bo.JobCompletedInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: jiangjunjie
 * @Date: 2020-03-05
 * @Description:
 */
public class JobCompletedLogDelayDealer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletedLogDelayDealer.class);

    private ApplicationContext applicationContext;
    private ScheduleJobDao scheduleJobDao;
    private WorkerOperator workerOperator;

    private DelayBlockingQueue<JobCompletedInfo> delayBlockingQueue = new DelayBlockingQueue<JobCompletedInfo>(1000);
    private ExecutorService taskStatusPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1), new CustomThreadFactory(this.getClass().getSimpleName()));

    public  JobCompletedLogDelayDealer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setBean();
        taskStatusPool.execute(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobCompletedInfo taskInfo = delayBlockingQueue.take();
                updateJobEngineLog(taskInfo.getJobId(), taskInfo.getJobIdentifier());
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    public void addCompletedTaskInfo(JobCompletedInfo taskInfo){
        try {
            delayBlockingQueue.put(taskInfo);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    private void updateJobEngineLog(String jobId, JobIdentifier jobIdentifier) {
        try {
            String jobLog = workerOperator.getEngineLog(jobIdentifier);
            if (jobLog != null) {
                scheduleJobDao.updateEngineLog(jobId, jobLog);
            }
        } catch (Throwable e) {
            String errorLog = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("update JobEngine Log error jobId:{} ,error info {}..", jobId, errorLog);
            scheduleJobDao.updateEngineLog(jobId, errorLog);
        }
    }

    private void setBean() {
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
    }
}
