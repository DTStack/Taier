package com.dtstack.engine.master.task;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.common.queue.GroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class QueueListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(QueueListener.class);

    private final static int listener = 5 * 1000;

    public QueueListener() {
    }

    @Override
    public void run() {

        while (true) {
            try {
                logger.warn("QueueListener start again....");
                //获取所有节点的queue
                Map<String, Map<String, GroupInfo>> queueInfo = WorkNode.getInstance().getQueueInfo();


                //更新当前节点的queue 信息

            } catch (Throwable e) {
                logger.error("QueueListener error:{}", ExceptionUtil.getErrorMessage(e));
            } finally {
                try {
                    Thread.sleep(listener);
                } catch (InterruptedException e1) {
                    logger.error("", e1);
                }
            }
        }

    }
}
