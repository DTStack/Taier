package com.dtstack.engine.master.executor;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.master.queue.QueueInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.sql.Twins;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class JobExecutorTrigger implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutorTrigger.class);

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
    private BatchJobDao batchJobDao;

    @Autowired
    private CronJobExecutor cronJobExecutor;

    @Autowired
    private FillJobExecutor fillJobExecutor;

    @Autowired
    private JobRichOperator jobRichOperator;


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

}
