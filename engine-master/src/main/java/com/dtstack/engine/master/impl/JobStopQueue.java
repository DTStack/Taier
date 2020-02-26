package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.pojo.StoppedJob;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobStopRecordDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.EngineJobStopRecord;
import com.dtstack.engine.common.enums.StoppedStatus;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.cache.ShardCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 任务停止消息
 * 不需要区分是不是主节点才启动处理线程
 * Date: 2018/1/22
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
@Component
public class JobStopQueue implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JobStopQueue.class);

    @Autowired
    private ShardCache shardCache;

    private DelayBlockingQueue<StoppedJob<JobElement>> stopJobQueue = new DelayBlockingQueue<StoppedJob<JobElement>>(1000);

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobStopRecordDao engineJobStopRecordDao;

    @Autowired
    private EngineJobDao engineJobDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobStopAction jobStopAction;

    private static final int WAIT_INTERVAL = 1000;
    private static final int OPERATOR_EXPIRED_INTERVAL = 60000;

    private int jobStoppedRetry;
    private long jobStoppedDelay;

    private ExecutorService simpleEs = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new CustomThreadFactory("stopProcessor"));

    private ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("acquire-stopJob"));

    private StopProcessor stopProcessor = new StopProcessor();
    private AcquireStopJob acquireStopJob = new AcquireStopJob();


    @Override
    public void afterPropertiesSet() {
        logger.info("Initializing " + this.getClass().getName());

        jobStoppedRetry = environmentContext.getJobStoppedRetry();
        jobStoppedDelay = environmentContext.getJobStoppedDelay();

        if (simpleEs.isShutdown()) {
            simpleEs = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), new CustomThreadFactory("stopProcessor"));
            stopProcessor.reStart();
        }

        simpleEs.submit(stopProcessor);

        scheduledService.scheduleAtFixedRate(
                acquireStopJob,
                WAIT_INTERVAL,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        stopProcessor.stop();
        simpleEs.shutdownNow();
    }

    public boolean tryPutStopJobQueue(ParamAction paramAction) {
        JobElement jobElement = new JobElement(paramAction.getTaskId(), paramAction.getStopJobId());
        return stopJobQueue.tryPut(new StoppedJob<JobElement>(jobElement, jobStoppedRetry, jobStoppedDelay));
    }

    private class AcquireStopJob implements Runnable {
        @Override
        public void run() {
            long tmpStartId = 0L;
            Timestamp operatorExpired = new Timestamp(System.currentTimeMillis() + OPERATOR_EXPIRED_INTERVAL);
            Timestamp lessThanOperatorExpired = new Timestamp(System.currentTimeMillis());
            while (true) {
                try {
                    //根据条件判断是否有数据存在
                    List<EngineJobStopRecord> jobStopRecords = engineJobStopRecordDao.listStopJob(tmpStartId, lessThanOperatorExpired);
                    if (jobStopRecords.isEmpty()) {
                        break;
                    }
                    //使用乐观锁防止多节点重复停止任务
                    Iterator<EngineJobStopRecord> it = jobStopRecords.iterator();
                    while (it.hasNext()) {
                        EngineJobStopRecord jobStopRecord = it.next();
                        tmpStartId = jobStopRecord.getId();
                        //已经被修改过version的任务代表其他节点正在处理，可以忽略
                        Integer update = engineJobStopRecordDao.updateOperatorExpiredVersion(jobStopRecord.getId(), operatorExpired, jobStopRecord.getVersion());
                        if (update != 1) {
                            it.remove();
                        }
                    }
                    //经乐观锁判断，经过remove后所剩下的数据
                    if (jobStopRecords.isEmpty()) {
                        break;
                    }
                    List<String> jobIds = jobStopRecords.stream().map(EngineJobStopRecord::getTaskId).collect(Collectors.toList());
                    List<EngineJobCache> jobCaches = engineJobCacheDao.getByJobIds(jobIds);

                    //为了下面兼容异常状态的任务停止
                    Map<String, EngineJobCache> jobCacheMap = new HashMap<>(jobCaches.size());
                    for (EngineJobCache jobCache : jobCaches) {
                        jobCacheMap.put(jobCache.getJobId(), jobCache);
                    }

                    for (EngineJobStopRecord jobStopRecord : jobStopRecords) {
                        EngineJobCache jobCache = jobCacheMap.get(jobStopRecord.getTaskId());
                        if (jobCache != null) {
                            //停止任务的时效性，发起停止操作要比任务存入jobCache表的时间要迟
                            if (jobCache.getGmtCreate().after(jobStopRecord.getGmtCreate())) {
                                engineJobStopRecordDao.delete(jobStopRecord.getId());
                                continue;
                            }

                            JobElement jobElement = new JobElement(jobCache.getJobId(), jobStopRecord.getId());
                            stopJobQueue.put(new StoppedJob<JobElement>(jobElement, jobStoppedRetry, jobStoppedDelay));
                        } else {
                            logger.warn("[Unnormal Job] jobId:{}", jobStopRecord.getTaskId());
                            //jobcache表没有记录，可能任务已经停止。在update表时增加where条件不等于stopped
                            engineJobDao.updateTaskStatusNotStopped(jobStopRecord.getTaskId(), RdosTaskStatus.CANCELED.getStatus(), RdosTaskStatus.getStoppedStatus());
                            shardCache.updateLocalMemTaskStatus(jobStopRecord.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
                            engineJobStopRecordDao.delete(jobStopRecord.getId());
                        }
                    }

                    Thread.sleep(500);
                } catch (Throwable e) {
                    logger.error("when acquire stop jobs happens error:{}", e);
                }
            }
        }
    }

    private class StopProcessor implements Runnable {

        private boolean run = true;

        @Override
        public void run() {

            logger.info("job stop process thread is start...");

            while (run) {
                try {
                    StoppedJob<JobElement> stoppedJob = stopJobQueue.take();
                    StoppedStatus stoppedStatus = jobStopAction.stopJob(stoppedJob.getJob());
                    switch (stoppedStatus) {
                        case STOPPED:
                        case MISSED:
                            engineJobStopRecordDao.delete(stoppedJob.getJob().stopJobId);
                            break;
                        case STOPPING:
                        case RETRY:
                            if (stoppedJob.isRetry()) {
                                if (StoppedStatus.STOPPING == stoppedStatus) {
                                    stoppedJob.resetDelay(jobStoppedDelay * 20);
                                } else if (StoppedStatus.RETRY == stoppedStatus) {
                                    stoppedJob.resetDelay(jobStoppedDelay);
                                }
                                stoppedJob.incrCount();
                                stopJobQueue.put(stoppedJob);
                            } else {
                                logger.warn("jobId:{} retry limited!", stoppedJob.getJob().jobId);
                                engineJobStopRecordDao.delete(stoppedJob.getJob().stopJobId);
                            }
                        default:
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }

            logger.info("job stop process thread is shutdown...");

        }

        public void stop() {
            this.run = false;
        }

        public void reStart() {
            this.run = true;
        }
    }

    public static class JobElement {

        public String jobId;
        public long stopJobId;

        public JobElement(String jobId, long stopJobId) {
            this.jobId = jobId;
            this.stopJobId = stopJobId;
        }
    }
}
