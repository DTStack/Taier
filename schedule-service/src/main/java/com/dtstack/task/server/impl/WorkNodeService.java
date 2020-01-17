package com.dtstack.task.server.impl;

import com.dtstack.task.server.node.WorkNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/29
 */
@Service
public class WorkNodeService {

    private static final Logger logger = LoggerFactory.getLogger(WorkNodeService.class);

    @Autowired
    private WorkNode workNode;

    /**
     * 接收 master 节点容灾后的消息
     */
    public void masterSendJobs() {
        logger.info("--- accept masterSendJobs, prepare deal recoverOtherNode ------");
        try {
            workNode.recoverOtherNode();
            logger.info("--- deal recoverOtherNode done ------");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
