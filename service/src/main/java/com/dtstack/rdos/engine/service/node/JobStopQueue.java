package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.enums.RequestStart;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.send.HttpSendClient;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务停止消息
 * 不需要区分是不是主节点才启动处理线程
 * Date: 2018/1/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobStopQueue {

    private static final Logger LOG = LoggerFactory.getLogger(JobStopQueue.class);

    private BlockingQueue<ParamAction> queue = Queues.newLinkedBlockingQueue();

    private WorkNode workNode;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private RdosEngineBatchJobDAO engineBatchJobDAO = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO engineStreamJobDAO = new RdosEngineStreamJobDAO();

    private JobStopAction jobStopAction;

    private ExecutorService simpleES = Executors.newSingleThreadExecutor();

    private StopProcessor stopProcessor = new StopProcessor();

    public JobStopQueue(WorkNode workNode){
        this.workNode = workNode;
        this.jobStopAction = new JobStopAction(workNode);
    }

    public void start(){
        if(simpleES.isShutdown()){
            simpleES = Executors.newSingleThreadExecutor();
            stopProcessor.reStart();
        }

        simpleES.submit(stopProcessor);
    }

    public void stop(){
        stopProcessor.stop();
        simpleES.shutdownNow();
    }

    public void addJob(ParamAction paramAction){
        queue.add(paramAction);
    }


    class StopProcessor implements Runnable{

        private boolean run = true;

        @Override
        public void run() {

            LOG.info("job stop process thread is start...");

            while (run){
                try {
                    ParamAction paramAction = queue.take();
                    String jobId = paramAction.getTaskId();

                    if(!checkCanStop(jobId, paramAction.getComputeType())){
                        continue;
                    }

                    //在zk上查找任务所在的worker-address
                    Integer computeType  = paramAction.getComputeType();
                    String zkTaskId = TaskIdUtil.getZkTaskId(computeType, paramAction.getEngineType(), jobId);
                    String addr = zkDistributed.getJobLocationAddr(zkTaskId);
                    if(addr == null){
                        LOG.info("can't get info from engine zk for jobId" + jobId);
                        continue;
                    }

                    if (!addr.equals(zkDistributed.getLocalAddress())){
                        paramAction.setRequestStart(RequestStart.NODE.getStart());
                        HttpSendClient.actionStopJobToWorker(addr, paramAction);
                        LOG.info("action stop job:{} to worker node addr:{}." + paramAction.getTaskId(), addr);
                        continue;
                    }

                    jobStopAction.stopJob(paramAction);
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }

            LOG.info("job stop process thread is shutdown...");

        }

        /**
         * 判断任务是否可停止
         * @param taskId
         * @param computeType
         * @return
         */
        private boolean checkCanStop(String taskId, Integer computeType){

            Integer sta;
            if(ComputeType.BATCH.getType().equals(computeType)){
                RdosEngineBatchJob rdosEngineBatchJob = engineBatchJobDAO.getRdosTaskByTaskId(taskId);
                sta = rdosEngineBatchJob.getStatus().intValue();
            }else if(ComputeType.STREAM.getType().equals(computeType)){
                RdosEngineStreamJob rdosEngineStreamJob = engineStreamJobDAO.getRdosTaskByTaskId(taskId);
                sta = rdosEngineStreamJob.getStatus().intValue();
            }else{
                LOG.error("invalid compute type:{}", computeType);
                return false;
            }

            return RdosTaskStatus.getCanStopStatus().contains(sta);
        }


        public void stop(){
            this.run = false;
        }

        public void reStart(){
            this.run = true;
        }
    }
}
