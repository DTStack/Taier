package com.dtstack.engine.master.zk.listener;

import com.dtstack.engine.common.util.ExceptionUtil;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.executor.JobExecutorTrigger;
import com.dtstack.engine.master.queue.ClusterQueueInfo;
import com.dtstack.engine.master.queue.QueueInfo;
import com.dtstack.engine.master.zk.ZkService;
import com.dtstack.engine.master.zk.data.BrokerQueueNode;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class QueueListener implements Listener {

    private static final Logger logger = LoggerFactory.getLogger(QueueListener.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 5000;

    private final ScheduledExecutorService scheduledService;

    private JobExecutorTrigger jobExecutorTrigger;
    private ZkService zkService;

    public QueueListener(JobExecutorTrigger jobExecutorTrigger, ZkService zkService) {
        this.jobExecutorTrigger = jobExecutorTrigger;
        this.zkService = zkService;

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("QueueListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            logOutput++;
            //获取所有节点的queue
            Map<String, BrokerQueueNode> queueNodeMap = zkService.getAllBrokerQueueNode();
            Map<String, Map<Integer, QueueInfo>> queueInfo = Maps.newHashMap();
            queueNodeMap.forEach((address, queueNode) -> queueInfo.put(address, queueNode.getQueueInfo()));
            ClusterQueueInfo.getInstance().updateClusterQueueInfo(queueInfo);

            //更新当前节点的queue 信息
            Map<Integer, QueueInfo> nodeQueueInfo = jobExecutorTrigger.getNodeQueueInfo();
            BrokerQueueNode localQueueNode = new BrokerQueueNode();
            localQueueNode.setQueueInfo(nodeQueueInfo);
            zkService.updateSynchronizedLocalQueueNode(zkService.getLocalAddress(), localQueueNode);
            if (LogCountUtil.count(logOutput, MULTIPLES)) {
                logger.info("QueueListener start again....");
            }
        } catch (Throwable e) {
            logger.error("QueueListener error:{}", ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public void close() throws Exception {
        scheduledService.shutdownNow();
    }
}
