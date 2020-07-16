package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.executor.JobExecutorTrigger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/29
 */
@Service
public class NodeRecoverService implements com.dtstack.engine.api.service.NodeRecoverService {

    private static final Logger logger = LoggerFactory.getLogger(NodeRecoverService.class);

    @Autowired
    private JobExecutorTrigger jobExecutorTrigger;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private JobDealer jobDealer;

    /**
     * 接收 master 节点容灾后的消息
     */
    public void masterTriggerNode() {
        logger.info("--- accept masterTriggerNode");
        try {
            jobExecutorTrigger.recoverOtherNode();
            logger.info("--- deal recoverOtherNode done ------");
            recoverJobCaches();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public void recoverJobCaches() {
        String localAddress = environmentContext.getLocalAddress();
        try {
            long startId = 0L;
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByFailover(startId, localAddress, EJobCacheStage.SUBMITTED.getStage());
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                List<JobClient> afterJobClients = new ArrayList<>(jobCaches.size());
                for (EngineJobCache jobCache : jobCaches) {
                    try {
                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                        JobClient jobClient = new JobClient(paramAction);
                        afterJobClients.add(jobClient);
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        logger.error("", e);
                        //数据转换异常--打日志
                        jobDealer.dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
                if (CollectionUtils.isNotEmpty(afterJobClients)) {
                    jobDealer.afterSubmitJobBatch(afterJobClients);
                }
            }
        } catch (Exception e) {
            logger.error("----broker:{} RecoverDealer error:{}", localAddress, e);
        }
    }

}
