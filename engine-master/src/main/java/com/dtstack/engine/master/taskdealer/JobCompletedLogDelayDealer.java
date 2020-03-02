package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.CompletedTaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class JobCompletedLogDelayDealer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(JobCompletedLogDelayDealer.class);

    private ApplicationContext applicationContext;
    private EngineJobDao engineJobDao;
    private WorkerOperator workerOperator;

    private DelayBlockingQueue<CompletedTaskInfo> delayBlockingQueue = new DelayBlockingQueue<CompletedTaskInfo>(1000);

    public  JobCompletedLogDelayDealer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setBean();
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

    public void addTaskToDelayQueue(CompletedTaskInfo taskInfo){
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
                engineJobDao.updateEngineLog(jobId, jobLog);
            }
        } catch (Throwable e) {
            String errorLog = ExceptionUtil.getErrorMessage(e);
            logger.error("update JobEngine Log error jobId:{} ,error info {}..", jobId, errorLog);
            engineJobDao.updateEngineLog(jobId, errorLog);
        }
    }

    private void setBean() {
        this.engineJobDao = applicationContext.getBean(EngineJobDao.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
    }
}
