package com.dtstack.rdos.engine.entrance.node;

import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.dtstack.rdos.engine.util.TaskIdUtil;
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

    private MasterNode masterNode;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDao engineJobCacheDao = new RdosEngineJobCacheDao();

    private JobStopAction jobStopAction = new JobStopAction();

    private ExecutorService simpleES = Executors.newSingleThreadExecutor();

    private StopProcessor stopProcessor = new StopProcessor();

    public JobStopQueue(MasterNode masterNode){
        this.masterNode = masterNode;
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

                    //在master等待队列中查找
                    if(masterNode.stopTaskIfExists(paramAction.getEngineType(), paramAction.getGroupName(), jobId)){
                        LOG.info("stop job:{} success." + paramAction.getTaskId());
                        return;
                    }

                    //cache记录被删除说明已经在引擎上执行了,往对应的引擎发送停止任务指令
                    if(engineJobCacheDao.getJobById(jobId) == null){
                        jobStopAction.stopJob(paramAction);
                        LOG.info("stop job:{} success." + paramAction.getTaskId());
                        return;
                    }

                    //在zk上查找任务所在的worker-address
                    Integer computeType  = paramAction.getComputeType();
                    String zkTaskId = TaskIdUtil.getZkTaskId(computeType, paramAction.getEngineType(), jobId);
                    String addr = zkDistributed.getJobLocationAddr(zkTaskId);
                    if(addr == null){
                        LOG.info("can't get info from engine zk for jobId:" + jobId);
                        return;
                    }

                    paramAction.setRequestStart(RequestStart.NODE.getStart());
                    HttpSendClient.actionStopJobToWorker(addr, paramAction);
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }

            LOG.info("job stop process thread is shutdown...");

        }



        public void stop(){
            this.run = false;
        }

        public void reStart(){
            this.run = true;
        }
    }
}
