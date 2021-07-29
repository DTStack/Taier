package com.dtstack.engine.master.listener;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.queue.GroupInfo;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.executor.JobExecutorTrigger;
import com.dtstack.engine.master.queue.QueueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@Component
public class QueueListener implements InitializingBean, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueListener.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 5000;

    @Autowired
    private JobExecutorTrigger jobExecutorTrigger;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private EnvironmentContext environmentContext;

    private boolean checkJobMaxPriorityStrategy = false;

    private ScheduledExecutorService scheduledService;

    private volatile Map<Integer, Map<String, QueueInfo>> allNodesJobQueueTypes = new HashMap<>();
    private volatile Map<String, Map<String, GroupInfo>> allNodesGroupQueueJobResources = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        checkJobMaxPriorityStrategy = environmentContext.getCheckJobMaxPriorityStrategy();

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        logOutput++;
        if (LogCountUtil.count(logOutput, MULTIPLES)) {
            LOGGER.info("QueueListener start again....");
        }
        try {
            Map<String, Map<Integer, QueueInfo>> allNodesJobQueueInfo = jobExecutorTrigger.getAllNodesJobQueueInfo();
            if (allNodesJobQueueInfo != null) {
                Map<Integer, Map<String, QueueInfo>> tmpAllNodesJobQueueTypes = new HashMap<>();
                allNodesJobQueueInfo.forEach((address, typeJobQueueInfo) -> {
                    typeJobQueueInfo.forEach((type, queueInfo) -> {
                        Map<String, QueueInfo> nodesJobQueue = tmpAllNodesJobQueueTypes.computeIfAbsent(type, k -> {
                            Map<String, QueueInfo> value = new HashMap<>();
                            value.put(address, queueInfo);
                            return value;
                        });
                        nodesJobQueue.put(address, queueInfo);
                    });
                });
                this.allNodesJobQueueTypes = tmpAllNodesJobQueueTypes;
            }
        } catch (Throwable e) {
            LOGGER.error("allNodesJobQueueInfo error:", e);
        }

        if (checkJobMaxPriorityStrategy) {
            computeAllNodesGroupQueueJobResources();
        }
    }


    private void computeAllNodesGroupQueueJobResources() {
        try {
            Map<String, Map<String, GroupInfo>> allNodesGroupQueueInfo = jobDealer.getAllNodesGroupQueueInfo();
            if (allNodesGroupQueueInfo != null) {
                Map<String, Map<String, GroupInfo>> tmpAllNodesGroupQueueJobResources = new HashMap<>();
                allNodesGroupQueueInfo.forEach((address, jobResourceGroupQueueInfo) -> {
                    jobResourceGroupQueueInfo.forEach((jobResource, groupInfo) -> {
                        Map<String, GroupInfo> nodesGroupQueue = tmpAllNodesGroupQueueJobResources.computeIfAbsent(jobResource, k -> {
                            Map<String, GroupInfo> value = new HashMap<>();
                            value.put(address, groupInfo);
                            return value;
                        });
                        nodesGroupQueue.put(address, groupInfo);
                    });
                });
                this.allNodesGroupQueueJobResources = tmpAllNodesGroupQueueJobResources;
            }
        } catch (Throwable e) {
            LOGGER.error("allNodesGroupQueueInfo error:", e);
        }
    }

    public Map<Integer, Map<String, QueueInfo>> getAllNodesJobQueueInfo() {
        return allNodesJobQueueTypes;
    }

    public Map<String, Map<String, GroupInfo>> getAllNodesGroupQueueInfo() {
        //默认false，在获取 allNodesGroupQueueJobResources 之前需要先计算一次
        if (!checkJobMaxPriorityStrategy) {
            computeAllNodesGroupQueueJobResources();
        }
        return allNodesGroupQueueJobResources;
    }
}
