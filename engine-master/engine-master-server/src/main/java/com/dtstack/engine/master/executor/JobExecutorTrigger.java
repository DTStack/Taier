package com.dtstack.engine.master.executor;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.queue.QueueInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private CronJobExecutor cronJobExecutor;

    @Autowired
    private FillJobExecutor fillJobExecutor;

    @Autowired
    private RestartJobExecutor restartJobExecutor;

    @Autowired
    private JobRichOperator jobRichOperator;

    private List<AbstractJobExecutor> executors = new ArrayList<>(EScheduleType.values().length);

    private ExecutorService executorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Initializing " + this.getClass().getName());

        executors.add(fillJobExecutor);
        executors.add(cronJobExecutor);
        executors.add(restartJobExecutor);

        executorService = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
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
        List<String> allNodeAddress = scheduleJobDao.getAllNodeAddress();
        Pair<String, String> cycTime = jobRichOperator.getCycTimeLimit();
        Map<String, Map<Integer, QueueInfo>> allNodeJobInfo = Maps.newHashMap();
        for (String nodeAddress : allNodeAddress) {
            if (StringUtils.isBlank(nodeAddress)) {
                continue;
            }
            allNodeJobInfo.computeIfAbsent(nodeAddress, na -> {
                Map<Integer, QueueInfo> nodeJobInfo = Maps.newHashMap();
                executors.forEach(executor -> nodeJobInfo.computeIfAbsent(executor.getScheduleType().getType(), k -> {
                    int queueSize = scheduleJobDao.countTasksByCycTimeTypeAndAddress(nodeAddress, executor.getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight());
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
