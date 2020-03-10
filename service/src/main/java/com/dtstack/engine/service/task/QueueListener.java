package com.dtstack.engine.service.task;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.service.node.WorkNode;
import com.dtstack.engine.service.queue.GroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author sishu.yss
 *
 */
public class QueueListener implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(QueueListener.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 5000;

    private ScheduledExecutorService scheduledService;

    private static volatile Map<String, Map<String, GroupInfo>> allNodesGroupQueueJobResources = new HashMap<>();

    public QueueListener(){
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
        if (PublicUtil.count(logOutput, MULTIPLES)) {
            logger.info("QueueListener start again....");
        }

        try {
            Map<String, Map<String, GroupInfo>> allNodesGroupQueueInfo = WorkNode.getInstance().getAllNodesGroupQueueInfo();
            if (allNodesGroupQueueInfo != null) {
                final Map<String, Map<String, GroupInfo>> tmpAllNodesGroupQueueJobResources = new HashMap<>();
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
                allNodesGroupQueueJobResources = tmpAllNodesGroupQueueJobResources;
            }
        } catch (Throwable e) {
            logger.error("allNodesGroupQueueInfo error:{}", e);
        }
    }

    public static Map<String, Map<String, GroupInfo>> getAllNodesGroupQueueInfo() {
        return allNodesGroupQueueJobResources;
    }

}
