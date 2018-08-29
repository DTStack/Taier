package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.enums.RequestStart;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.queue.OrderLinkedBlockingQueue;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enums.EngineType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.base.queue.ExeQueueMgr;
import com.dtstack.rdos.engine.service.send.HttpSendClient;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理重启后任务恢复
 *
 * Date: 2018/1/8
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MasterNode {

    private static final Logger LOG = LoggerFactory.getLogger(MasterNode.class);

    /***循环间隔时间20s*/
    private static final int WAIT_INTERVAL = 20 * 1000;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private RdosEngineBatchJobDAO rdosEngineBatchJobDao = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO rdosEngineStreamJobDao = new RdosEngineStreamJobDAO();

    private RecoverDealer recoverDealer;

    private String localAddress = ConfigParse.getLocalAddress();

    private ExecutorService senderExecutor;

    private static MasterNode masterNode = new MasterNode();

    private boolean currIsMaster = false;

    public static MasterNode getInstance(){
        return masterNode;
    }

    private MasterNode(){
    }

    public void setIsMaster(boolean isMaster){
        if(isMaster && !currIsMaster){
            currIsMaster = true;
            if(senderExecutor.isShutdown()){
                senderExecutor = Executors.newSingleThreadExecutor();
            }

            recoverDealer = new RecoverDealer();
            senderExecutor.submit(recoverDealer);
            LOG.warn("---start master node deal thread------");
        }else if (!isMaster && currIsMaster){
            currIsMaster = false;
            senderExecutor.shutdownNow();
            LOG.warn("---stop master node deal thread------");
        }
    }

    /**
     * master 节点分发任务失败
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, Integer computeType, String errorMsg){
        engineJobCacheDao.deleteJob(taskId);

        if(ComputeType.BATCH.typeEqual(computeType)){
            rdosEngineBatchJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));

        }else if(ComputeType.STREAM.typeEqual(computeType)){
            rdosEngineStreamJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));

        }else{
            LOG.error("not support compute type:" + computeType);
        }
    }

    public String generateErrorMsg(String msgInfo){
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }

    /**
     * 转变为master之后
     */
    public void loadQueueFromDB(){
        List<RdosEngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(EJobCacheStage.IN_PRIORITY_QUEUE.getStage());
        if(CollectionUtils.isEmpty(jobCaches)){
            return;
        }

        jobCaches.forEach(jobCache ->{

            try{
                ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                JobClient jobClient = new JobClient(paramAction);
                //更新任务状态为engineAccepted
                rdosEngineBatchJobDao.updateJobStatus(jobCache.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus());
                WorkNode.getInstance().addStartJob(jobClient);
            }catch (Exception e){
                //数据转换异常--打日志
                LOG.error("", e);
                dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "该任务存储信息异常,无法转换." + e.toString());
            }

        });
    }

    class RecoverDealer implements Runnable{

        @Override
        public void run() {
            LOG.info("-----重启后任务开始恢复----");
                try{
                    loadQueueFromDB();
                }catch (Exception e){
                    LOG.error("----load data from DB error:{}", e);
                }
            LOG.info("-----重启后任务结束恢复-----");
        }

    }

}
