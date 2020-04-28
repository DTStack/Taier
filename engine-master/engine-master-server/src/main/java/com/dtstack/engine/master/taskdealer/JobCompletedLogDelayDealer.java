package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.CompletedTaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: jiangjunjie
 * @Date: 2020-03-05
 * @Description:
 */
public class JobCompletedLogDelayDealer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(JobCompletedLogDelayDealer.class);

    private ApplicationContext applicationContext;
    private ScheduleJobDao scheduleJobDao;
    private WorkerOperator workerOperator;

    private DelayBlockingQueue<CompletedTaskInfo> delayBlockingQueue = new DelayBlockingQueue<CompletedTaskInfo>(1000);
    private ExecutorService taskStatusPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(true), new CustomThreadFactory(this.getClass().getSimpleName()));

    public  JobCompletedLogDelayDealer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setBean();
        taskStatusPool.execute(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                CompletedTaskInfo taskInfo = delayBlockingQueue.take();
                updateJobEngineLog(taskInfo.getJobId(), taskInfo.getJobIdentifier(), taskInfo.getEngineType(), taskInfo.getPluginInfo());
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public void addCompletedTaskInfo(CompletedTaskInfo taskInfo){
        try {
            delayBlockingQueue.put(taskInfo);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    private void updateJobEngineLog(String jobId, JobIdentifier jobIdentifier, String engineType, String pluginInfo) {
        try {
            String jobLog = workerOperator.getEngineLog(engineType, pluginInfo, jobIdentifier);
            if (jobLog != null) {
                scheduleJobDao.updateEngineLog(jobId, jobLog);
            }
        } catch (Throwable e) {
            String errorLog = ExceptionUtil.getErrorMessage(e);
            logger.error("update JobEngine Log error jobId:{} ,error info {}..", jobId, errorLog);
            scheduleJobDao.updateEngineLog(jobId, errorLog);
        }
    }

    private void setBean() {
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
    }
}
