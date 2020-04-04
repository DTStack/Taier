package com.dtstack.engine.master.executor;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.sql.Twins;
import com.dtstack.engine.common.constrant.JobFieldInfo;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.domain.po.SimpleBatchJobPO;
import com.dtstack.engine.master.queue.QueueInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class JobExecutorTrigger implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutorTrigger.class);

    private static final AtomicBoolean INIT = new AtomicBoolean(true);

    /**
     * 已经提交到的job的status
     */
    private static final List<Integer> SUBMIT_ENGINE_STATUSES = new ArrayList<>();

    static {
        SUBMIT_ENGINE_STATUSES.addAll(TaskStatusConstrant.RUNNING_STATUS);
        SUBMIT_ENGINE_STATUSES.addAll(TaskStatusConstrant.WAIT_STATUS);
        SUBMIT_ENGINE_STATUSES.add(TaskStatus.SUBMITTING.getStatus());
    }

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private BatchJobDao batchJobDao;

    @Autowired
    private CronJobExecutor cronJobExecutor;

    @Autowired
    private FillJobExecutor fillJobExecutor;

    @Autowired
    private JobRichOperator jobRichOperator;

    @Autowired
    private ActionService actionService;

    private List<AbstractJobExecutor> executors = new ArrayList<>(EScheduleType.values().length);

    private ExecutorService executorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Initializing " + this.getClass().getName());

        executors.add(fillJobExecutor);
        executors.add(cronJobExecutor);

        executorService = new ThreadPoolExecutor(executors.size(), executors.size(), 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("ExecutorDealer"));
        for (AbstractJobExecutor executor : executors) {
            executorService.submit(executor);
        }

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("JobStatusDealer"));
        scheduledService.scheduleWithFixedDelay(
                new JobStatusDealer(),
                0,
                environmentContext.getJobStatusDealerInterval(),
                TimeUnit.MILLISECONDS);
    }

    /**
     * 同步所有节点的 type类型下的 job实例信息
     * key1: nodeAddress,
     * key2: scheduleType
     */
    public Map<String, Map<Integer, QueueInfo>> getAllNodesJobQueueInfo() {
        List<String> allNodeAddress = batchJobDao.getAllNodeAddress();
        Twins<String, String> cycTime = jobRichOperator.getCycTimeLimit();
        Map<String, Map<Integer, QueueInfo>> allNodeJobInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeJobInfo.computeIfAbsent(nodeAddress, na -> {
                Map<Integer, QueueInfo> nodeJobInfo = Maps.newHashMap();
                executors.forEach(executor -> nodeJobInfo.computeIfAbsent(executor.getScheduleType(), k -> {
                    int queueSize = batchJobDao.countTasksByCycTimeTypeAndAddress(nodeAddress, executor.getScheduleType(), cycTime.getKey(), cycTime.getType());
                    QueueInfo queueInfo = new QueueInfo();
                    queueInfo.setSize(queueSize);
                    return queueInfo;
                }));
                return nodeJobInfo;
            });
        }
        return allNodeJobInfo;
    }

    @Override
    public void destroy() throws Exception {
        for (AbstractJobExecutor executor : executors) {
            executor.stop();
        }

        executorService.shutdownNow();
    }

    public void recoverOtherNode() {
        for (AbstractJobExecutor executor : executors) {
            executor.recoverOtherNode();
        }
    }

    class JobStatusDealer implements Runnable {

        private final static int MULTIPLES = 10;

        private int logOutput = 0;

        private long lastSyncTime = 0;

        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public void run() {
            if (StringUtils.isBlank(environmentContext.getLocalAddress())) {
                return;
            }

            if (LogCountUtil.count(logOutput, MULTIPLES)) {
                LOG.info("-----start JobStatusDealer----");
            }
            long syncStartTime = System.currentTimeMillis();
            try {
                long syncJobCount = syncStatus();
                long syncTime = System.currentTimeMillis() - syncStartTime;
                String lastSyncTimeStr = null;
                if (lastSyncTime != 0) {
                    lastSyncTimeStr = sdf.format(new Date(lastSyncTime));
                }
                lastSyncTime = syncStartTime;
                if (LogCountUtil.count(logOutput, MULTIPLES)) {
                    LOG.info("-----end JobStatusDealer, syncJobCount:{} syncStatusTimeUsed（ms）:{} lastSyncTime: {} -----", syncJobCount, syncTime, lastSyncTimeStr);
                }
                logOutput++;
            } catch (Exception e) {
                LOG.error("----syncStatus happens error:{}", e);
            }
        }

        private Long syncStatus() {
            if (INIT.compareAndSet(true, false)) {
                return syncAllStatus();
            }
            return syncBulkStatus();
        }


        private Long syncAllStatus() {
            long jobCount = 0L;
            try {
                long startId = 0L;
                while (true) {
                    List<SimpleBatchJobPO> jobs = batchJobDao.listSimpleJobByStatusAddress(startId, SUBMIT_ENGINE_STATUSES, environmentContext.getLocalAddress());
                    if (CollectionUtils.isEmpty(jobs)) {
                        break;
                    }
                    List<String> jobIds = Lists.newArrayList();
                    for (SimpleBatchJobPO batchJob : jobs) {
                        jobIds.add(batchJob.getJobId());
                        startId = batchJob.getId();
                    }
                    List<Map<String, Object>> jobStatusInfos = actionService.listJobStatusByJobIds(jobIds);
                    batchUpdateJobStatusInfo(jobStatusInfos);
                    jobCount += jobs.size();
                }
            } catch (Exception e) {
                INIT.compareAndSet(true, false);
                LOG.error("----nodeAddress:{} syncAllStatus error:{}", environmentContext.getLocalAddress(), e);
            }
            return jobCount;
        }

        private Long syncBulkStatus() {
            long jobCount = 0L;
            try {
                //多同步10分钟数据，以免时钟不一致
                lastSyncTime -= 600000;
                List<Map<String, Object>> jobStatusInfos = actionService.listJobStatus(lastSyncTime);
                batchUpdateJobStatusInfo(jobStatusInfos);
                jobCount = jobStatusInfos.size();
            } catch (Exception e) {
                LOG.error("----nodeAddress:{} syncBulkStatus error:{}", environmentContext.getLocalAddress(), e);
                throw e;
            }
            return jobCount;
        }

        private void batchUpdateJobStatusInfo(List<Map<String, Object>> jobStatusInfos) {
            if (CollectionUtils.isEmpty(jobStatusInfos)) {
                return;
            }
            for (Map<String, Object> jobStatusInfo : jobStatusInfos) {
                String jobId = MapUtils.getString(jobStatusInfo, JobFieldInfo.JOB_ID);
                Integer status = MapUtils.getInteger(jobStatusInfo, JobFieldInfo.STATUS);
                Timestamp execStartTimestamp = null;
                Timestamp execEndTimestamp = null;
                Object execStartTime = MapUtils.getObject(jobStatusInfo, JobFieldInfo.EXEC_START_TIME);
                Object execEndTime = MapUtils.getObject(jobStatusInfo, JobFieldInfo.EXEC_END_TIME);
                if (execStartTime instanceof Date) {
                    Date startTime = (Date) execStartTime;
                    execStartTimestamp = new Timestamp(startTime.getTime());
                }
                if (execEndTime instanceof Date) {
                    Date endTime = (Date) execEndTime;
                    execEndTimestamp = new Timestamp(endTime.getTime());
                }
                Long execTime = MapUtils.getLong(jobStatusInfo, JobFieldInfo.EXEC_TIME);
                Integer retryNum = MapUtils.getInteger(jobStatusInfo, JobFieldInfo.RETRY_NUM);

                batchJobDao.updateJobInfoByJobId(jobId, status, execStartTimestamp, execEndTimestamp, execTime, retryNum);
            }
        }
    }

}
